package io.xapk.apkinstaller.utils.asset

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.app.Config
import io.xapk.apkinstaller.model.bean.AppedIconUrl
import io.xapk.apkinstaller.utils.AppLogger
import io.xapk.apkinstaller.utils.JsonUtils
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.bean.xapk.XApkExpansion
import io.xapk.apkinstaller.utils.bean.xapk.XApkManifest
import io.xapk.apkinstaller.utils.bean.xapk.XSplitApks
import io.xapk.apkinstaller.utils.imager.ImageUtils
import io.xapk.apkinstaller.utils.io.AppFolder
import io.xapk.apkinstaller.utils.io.FileWriterUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.ZipUtils
import java.io.File


object XApkOutputUtils {
    private val TAG by lazy { javaClass.simpleName }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    interface XApkOutputProgressCallback {
        @MainThread
        fun onStart()

        @MainThread
        fun onProgress(xApkOutputStatus: XApkOutputStatus)

        @MainThread
        fun onComped(tempDir: File, xApkFile: File)

        @MainThread
        fun onError(tempDir: File? = null, xApkFile: File? = null)
    }

    @WorkerThread
    @Synchronized
    fun compressXApk(mContext: Context, appInfo: AppInfo, xApkOutputProgressCallback: XApkOutputProgressCallback?) {
        if (!appInfo.isExpandXApk) {
            handler.post {
                xApkOutputProgressCallback?.onError()
            }
            return
        }
        handler.post {
            xApkOutputProgressCallback?.onStart()
        }
        val startTime = System.currentTimeMillis()
        val packageOutPutFileDir = File(AppFolder.xApkFolder, appInfo.packageName)
        FsUtils.deleteFileOrDir(packageOutPutFileDir)
        FsUtils.createOnNotFound(packageOutPutFileDir)

        val iconOutFile = File(packageOutPutFileDir, "icon.png")
        ImageUtils.downloadImage(mContext, AppedIconUrl(appInfo.packageName, appInfo.versionCode), iconOutFile)
        if (!FsUtils.exists(iconOutFile) || FsUtils.getFileOrDirLength(iconOutFile) == 0L) {
            handler.post {
                xApkOutputProgressCallback?.onError(packageOutPutFileDir)
            }
            return
        }
        val xSplitApksList = arrayListOf<XSplitApks>()
        if (appInfo.isUpdateFile && appInfo.obbExists) {
            handler.post {
                xApkOutputProgressCallback?.onProgress(XApkOutputStatus.ApkPrePare)
            }
            val apkFile = File(appInfo.sourceDir)
            val outputApkFile = File(packageOutPutFileDir, "${appInfo.packageName}.apk")
            FsUtils.writeFileToFile(outputApkFile, apkFile)
            if (!FsUtils.exists(outputApkFile) || FsUtils.getFileOrDirLength(outputApkFile) == 0L) {
                handler.post {
                    xApkOutputProgressCallback?.onError(packageOutPutFileDir)
                }
                return
            }
            xSplitApksList.add(XSplitApks(apkFile.name, "base"))
        } else if (appInfo.apksFilePath.isNotEmpty()) {
            handler.post {
                xApkOutputProgressCallback?.onProgress(XApkOutputStatus.ApkPrePare)
            }
            appInfo.apksFilePath.forEach {
                val apkFile = File(it)
                val fileName = apkFile.name
                if (fileName.endsWith(".apk")) {
                    var id = fileName.substring(0, fileName.indexOf(".apk"))
                    if (id.startsWith("split_")) {
                        id = id.substring("split_".length, id.length)
                    }
                    xSplitApksList.add(XSplitApks(fileName, id))
                    val outputApkFile = File(packageOutPutFileDir, fileName)
                    FsUtils.writeFileToFile(outputApkFile, apkFile)
                    if (!FsUtils.exists(outputApkFile) || FsUtils.getFileOrDirLength(outputApkFile) == 0L) {
                        handler.post {
                            xApkOutputProgressCallback?.onError(packageOutPutFileDir)
                        }
                        return
                    }
                }
            }
        }
        val xApkExpansionList = arrayListOf<XApkExpansion>()
        if (appInfo.obbExists){
            handler.post {
                xApkOutputProgressCallback?.onProgress(XApkOutputStatus.ObbPrePare)
            }
            val obbOutputFileDir = File(packageOutPutFileDir, "Android/obb/${appInfo.packageName}")
            FsUtils.createOnNotFound(obbOutputFileDir)
            val mainObbFile = File(appInfo.xApkMainObbAbsolutePath)
            val patchObbFile = File(appInfo.xApkPatchObbAbsolutePath)
            if (FsUtils.exists(mainObbFile)) {
                FsUtils.copyFileOrDirToDir(obbOutputFileDir, mainObbFile)
                xApkExpansionList.add(XApkExpansion().apply {
                    val tempPath = "Android/obb/${appInfo.packageName}/${mainObbFile.name}"
                    this.xFile = tempPath
                    this.installPath = tempPath
                    this.installLocation = "EXTERNAL_STORAGE"
                })
            }
            if (FsUtils.exists(patchObbFile)) {
                FsUtils.copyFileOrDirToDir(obbOutputFileDir, patchObbFile)
                xApkExpansionList.add(XApkExpansion().apply {
                    val tempPath = "Android/obb/${appInfo.packageName}/${patchObbFile.name}"
                    this.xFile = tempPath
                    this.installPath = tempPath
                    this.installLocation = "EXTERNAL_STORAGE"
                })
            }
            if (FsUtils.getFileOrDirLength(obbOutputFileDir) == 0L) {
                handler.post {
                    xApkOutputProgressCallback?.onError(packageOutPutFileDir)
                }
                return
            }
        }
        handler.post {
            xApkOutputProgressCallback?.onProgress(XApkOutputStatus.OtherPrePare)
        }
        var splitConfigs1: Array<String?>? = null
        if (xSplitApksList.isNotEmpty()) {
            val splitList = arrayListOf<String>()
            xSplitApksList.forEach {
                if (it._id != "base") {
                    splitList.add(it._id)
                }
            }
            if (splitList.isNotEmpty()) {
                splitConfigs1 = arrayOfNulls(splitList.size)
                splitList.forEachIndexed { index, s ->
                    splitConfigs1[index] = s
                }
            }
        }
        val xApkManifest = XApkManifest().apply {
            this.xApkVersion = Config.XAPK_VERSION
            this.packageName = appInfo.packageName
            this.label = appInfo.label
            this.versionCode = appInfo.versionCode.toString()
            this.versionName = appInfo.versionName
            this.targetSdkVersion = appInfo.targetSdkVersion
            this.minSdkVersion = appInfo.minSdkVersion
            this.permissions = appInfo.permissionsArrays
            this.totalSize = appInfo.appTotalSize
            this.expansionList = xApkExpansionList
            this.XSplitApks = xSplitApksList
            splitConfigs1?.let {
                this.splitConfigs = it.filterNotNull().toTypedArray()
            }
        }
        val jsonFile = File(packageOutPutFileDir, "manifest.json")
        FileWriterUtils.writeStringToFile(jsonFile, JsonUtils.objectToJson(xApkManifest))
        if (!FsUtils.exists(jsonFile) || FsUtils.getFileOrDirLength(jsonFile) == 0L) {
            handler.post {
                xApkOutputProgressCallback?.onError(packageOutPutFileDir)
            }
            return
        }
        handler.post {
            xApkOutputProgressCallback?.onProgress(XApkOutputStatus.ZipIng)
        }
        val xApkOutputFile = File(AppFolder.xApkFolder, "${appInfo.packageName}.${appInfo.versionCode}.${xSplitApksList.size}.xapk")
        ZipUtils.composeFileOrDir(xApkOutputFile, packageOutPutFileDir)
        if (!FsUtils.exists(xApkOutputFile) || FsUtils.getFileOrDirLength(xApkOutputFile) == 0L) {
            handler.post {
                xApkOutputProgressCallback?.onError(packageOutPutFileDir, xApkOutputFile)
            }
            return
        }
        val endTime = System.currentTimeMillis()
        AppLogger.d(TAG, "${appInfo.label}总压缩耗时:${endTime - startTime} ")
        handler.post {
            xApkOutputProgressCallback?.onComped(packageOutPutFileDir, xApkOutputFile)
        }
    }
}