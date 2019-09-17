package io.xapk.apkinstaller.utils.asset

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.bean.xapk.ApksBean
import io.xapk.apkinstaller.utils.bean.xapk.XApkManifest
import io.xapk.apkinstaller.utils.JsonUtils
import io.xapk.apkinstaller.utils.io.AppFolder
import io.xapk.apkinstaller.utils.io.FileWriterUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.unit.FormatUtils
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

object XApkInstallUtils {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    enum class InstallError {
        SplitApksError,
        ObbError,
        ApkError,
    }

    interface XApkInstallProgressCallback {
        @MainThread
        fun onStart()

        @MainThread
        fun onObbProgress(currentOffset: Long, totalLength: Long, percent: Int)

        @MainThread
        fun onApkProgress(currentOffset: Long, totalLength: Long, percent: Int)

        @MainThread
        fun onCompedApk(apkFile: File)

        @MainThread
        fun onCompedApks(apksBean: ApksBean)

        @MainThread
        fun onError(installError: InstallError)
    }

    @WorkerThread
    fun parseXApkZipFile(xApkFilePath: String): ZipFile? {
        return this.parseXApkZipFile(File(xApkFilePath))
    }

    @WorkerThread
    private fun parseXApkZipFile(xApkFile: File): ZipFile? {
        var zipFile: ZipFile? = null
        if (FsUtils.exists(xApkFile)) {
            try {
                zipFile = ZipFile(xApkFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return zipFile
    }

    @WorkerThread
    fun getXApkManifest(xApkFile: File): XApkManifest? {
        var xApkManifest: XApkManifest? = null
        parseXApkZipFile(xApkFile)?.apply {
            xApkManifest = getXApkManifest(this)
            this.close()
        }
        return xApkManifest
    }

    @WorkerThread
    private fun getXApkManifest(zipFile: ZipFile): XApkManifest? {
        var xApkManifest: XApkManifest? = null
        getZipFileInputStream(zipFile, "manifest.json")?.let {
            xApkManifest = JsonUtils.objectFromJson(InputStreamReader(it, "UTF-8"), XApkManifest::class.java)
        }
        return xApkManifest
    }

    @WorkerThread
    fun getXApkIcon(zipFile: ZipFile): InputStream? {
        return getZipFileInputStream(zipFile, "icon.png")
    }

    @WorkerThread
    private fun getZipFileInputStream(zipFile: ZipFile, inputName: String, isRaw: Boolean = false): InputStream? {
        var inputStream: InputStream? = null
        try {
            zipFile.getEntry(inputName)?.apply {
                inputStream = if (isRaw) {
                    zipFile.getRawInputStream(this)
                } else {
                    zipFile.getInputStream(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inputStream
    }

    @MainThread
    fun installXApk(xApkFile: File, xApkInstallProgressCallback: XApkInstallProgressCallback?) {
        Thread(Runnable {
            var zipFile: ZipFile? = null
            try {
                handler.post {
                    xApkInstallProgressCallback?.onStart()
                }
                parseXApkZipFile(xApkFile)?.apply {
                    zipFile = this
                    getXApkManifest(this)?.apply {
                        if (this.useObbs()) {
                            installXApkObb(zipFile!!, this, xApkInstallProgressCallback)
                        }
                        if (this.useSplitApks()) {
                            installSplitApks(zipFile!!, this, xApkInstallProgressCallback)
                        } else {
                            installApk(zipFile!!, this, xApkInstallProgressCallback)
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    zipFile?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    private fun installXApkObb(zipFile: ZipFile, xApkManifest: XApkManifest,
                               xApkInstallProgressCallback: XApkInstallProgressCallback?){
        var obbSuccess = false
        if (xApkManifest.useObbs()) {
            val obbTotalSize = getXApkObbTotalSize(zipFile, xApkManifest)
            for (item in xApkManifest.expansionList!!) {
                val inputStream = getZipFileInputStream(zipFile, item.xFile, true)!!
                val obbFile = File(FsUtils.getStorageDir(), item.installPath)
                if (!obbFile.parentFile.exists()) {
                    obbFile.parentFile.mkdirs()
                }
                obbSuccess = FileWriterUtils.writeFileFromIS(
                    obbFile,
                    inputStream,
                    object : FileWriterUtils.FileWriterProgressCallback {
                        var percent = 0
                        override fun onProgress(currentOffset: Long) {
                            val percent1 = FormatUtils.formatPercent(currentOffset, obbTotalSize)
                            if (percent1 > percent) {
                                percent = percent1
                                handler.post {
                                    xApkInstallProgressCallback?.onObbProgress(currentOffset, obbTotalSize, percent)
                                }
                            }
                        }
                    })
            }
            if (!obbSuccess){
                xApkInstallProgressCallback?.onError(InstallError.ObbError)
            }
        }
    }

    private fun installApk(zipFile: ZipFile, xApkManifest: XApkManifest,
                           xApkInstallProgressCallback: XApkInstallProgressCallback?){
        val apkFileName = "${xApkManifest.packageName}.apk"
        var isApkSuccess = false
        val tempApk = File(AppFolder.tempFolder, apkFileName)
        val totalLength = getXApkTotalSize(zipFile, xApkManifest)
        getZipFileInputStream(zipFile, apkFileName)?.apply {
            isApkSuccess = FileWriterUtils.writeFileFromIS(tempApk, this, object : FileWriterUtils.FileWriterProgressCallback {
                var percent = 0
                override fun onProgress(currentOffset: Long) {
                    val percent1 = FormatUtils.formatPercent(currentOffset, totalLength)
                    if (percent1 > percent) {
                        percent = percent1
                        handler.post {
                            xApkInstallProgressCallback?.onApkProgress(currentOffset, totalLength,percent)
                        }
                    }
                }
            })
        }
        if (isApkSuccess) {
            handler.post {
                xApkInstallProgressCallback?.onCompedApk(tempApk)
            }
        } else {
            handler.post {
                xApkInstallProgressCallback?.onError(InstallError.ApkError)
            }
        }
    }

    private fun installSplitApks(zipFile: ZipFile, xApkManifest: XApkManifest,
                                 xApkInstallProgressCallback: XApkInstallProgressCallback?){
        val fileList= arrayListOf<String>()
        if (xApkManifest.useSplitApks()){
            val totalLength = getXApkTotalSize(zipFile, xApkManifest)
            var percent = 0
            var currentTotal = 0L
            xApkManifest.XSplitApks?.forEach {
               var singFileOffset = 0L
               getZipFileInputStream(zipFile, it.file!!)?.apply {
                   val tempApk = File(AppFolder.getXApkInstallTempFolder(xApkManifest.packageName), it.file!!)
                   val isApkSuccess = FileWriterUtils.writeFileFromIS(tempApk, this, object : FileWriterUtils.FileWriterProgressCallback {
                       override fun onProgress(currentOffset: Long) {
                           val updateOffset = currentOffset - singFileOffset
                           singFileOffset = currentOffset
                           currentTotal += updateOffset
                           val percent1 = FormatUtils.formatPercent(currentTotal, totalLength)
                           if (percent1 > percent) {
                               percent = percent1
                               handler.post {
                                   xApkInstallProgressCallback?.onApkProgress(currentTotal,totalLength,percent)
                               }
                           }
                       }
                   })
                   if (isApkSuccess && FsUtils.exists(tempApk)) {
                       fileList.add(tempApk.absolutePath)
                   }
               }
           }
            if (fileList.isNotEmpty()) {
                handler.post {
                    xApkInstallProgressCallback?.onCompedApks(ApksBean().apply {
                        this.label = xApkManifest.label
                        this.packageName = xApkManifest.packageName
                        this.splitApkPaths = fileList
                        this.outputFileDir = AppFolder.getXApkInstallTempFolder(packageName).absolutePath
                    })
                }
            } else {
                handler.post {
                    xApkInstallProgressCallback?.onError(InstallError.SplitApksError)
                }
            }
        }
    }

    private fun getXApkTotalSize(zipFile: ZipFile, xApkManifest: XApkManifest): Long {
        return if (xApkManifest.useSplitApks()) {
            var totalLength = 0L
            xApkManifest.XSplitApks?.forEach {
                totalLength += zipFile.getEntry(it.file)?.compressedSize?:0L
            }
            totalLength
        } else {
            val apkFileName = "${xApkManifest.packageName}.apk"
            zipFile.getEntry(apkFileName).size
        }
    }

    private fun getXApkObbTotalSize(zipFile: ZipFile, xApkManifest: XApkManifest): Long {
        return if (xApkManifest.useObbs()) {
            var totalLength = 0L
            for (item in xApkManifest.expansionList!!) {
                totalLength += zipFile.getEntry(item.xFile)?.size?:0L
            }
            totalLength
        } else {
            0L
        }
    }
}
