package io.xapk.apkinstaller.utils.asset

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.ApksInfo
import io.xapk.apkinstaller.utils.io.AppFolder
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.ZipUtils
import io.xapk.apkinstaller.utils.io.filefilter.ApkFileFilter
import io.xapk.apkinstaller.utils.unit.FormatUtils
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File

object ApksUtils {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    interface ApksInstallProgressCallback {
        @MainThread
        fun onStart()

        @MainThread
        fun onApkProgress(currentOffset: Long, totalLength: Long, percent: Int)

        @MainThread
        fun onCompedApk(apksFile: ArrayList<File>, apkAssetBean: ApkAssetBean?, outputDir: File)

        @MainThread
        fun onError(outputDir: File)
    }

    @WorkerThread
     fun parseApksZipFile(apksFile: File): ApksInfo? {
        var apksInfo: ApksInfo? = null
        if (FsUtils.exists(apksFile)) {
            try {
                val apkNameArrays= arrayListOf<String>()
                val zipFile = ZipFile(apksFile)
                val entriesArray = zipFile.entries
                while (entriesArray.hasMoreElements()){
                    val nextElement= entriesArray.nextElement()
                    if (!nextElement.isDirectory && nextElement.name.endsWith(
                            ApkAssetType.Apk.suffix, true)) {
                        apkNameArrays.add(nextElement.name)
                    }
                }
                if (apkNameArrays.isNotEmpty()) {
                    apksInfo = ApksInfo()
                    apksInfo.apkNameArrays = apkNameArrays
                    apksInfo.fileName = apksFile.name
                    apksInfo.lastModified = apksFile.lastModified()
                    apksInfo.filePath = apksFile.absolutePath
                    apksInfo.fileSize = apksFile.length()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return apksInfo
    }

    @MainThread
    fun installApks(mContext: Context, apksInfo: ApksInfo, onProgressCallback: ApksInstallProgressCallback? = null) {
        Thread(Runnable {
            val zipFile = File(apksInfo.filePath)
            val totalLength = zipFile.length()
            val outputDir = File(AppFolder.tempFolder, apksInfo.fileName)
            ZipUtils.unZip(outputDir, zipFile,object :ZipUtils.IProgress{
                var percent = 0
                override fun onStart() {
                     handler.post {
                         onProgressCallback?.onStart()
                     }
                }

                override fun onProgress(currentOffset: Long) {
                    val percent1 = FormatUtils.formatPercent(currentOffset, totalLength)
                    if (percent1 > percent) {
                        percent = percent1
                        handler.post {
                            onProgressCallback?.onApkProgress(currentOffset, totalLength, percent)
                        }
                    }
                }

                override fun onComped() {
                    val apkLists = FsUtils.getDirFilesArray(outputDir, ApkFileFilter())
                    val apkAssetBean = getApksPackName(mContext, apkLists)
                    handler.post {
                        onProgressCallback?.onCompedApk(apkLists, apkAssetBean, outputDir)
                    }
                }

                override fun onError() {
                    handler.post {
                        onProgressCallback?.onError(outputDir)
                    }
                }
            })
        }).start()
    }

    @WorkerThread
    fun getApksPackName(mContext: Context, fileList: ArrayList<File>): ApkAssetBean? {
        fileList.forEach {
            AssetUtils.getSingleApkAssetInfo(mContext,it)?.apply {
                return this
            }
        }
        return null
    }
}