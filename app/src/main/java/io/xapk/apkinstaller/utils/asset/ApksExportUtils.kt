package io.xapk.apkinstaller.utils.asset

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.AppLogger
import io.xapk.apkinstaller.utils.io.AppFolder
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.ZipUtils
import java.io.File

object ApksExportUtils {
    private val TAG by lazy { javaClass.simpleName }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    interface ApksOutputProgressCallback {
        @MainThread
        fun onProgress()

        @MainThread
        fun onComped(apksFile: File)

        @MainThread
        fun onError()
    }

    @WorkerThread
    @Synchronized
    fun exportApks(appInfo: AppInfo, apksOutputProgressCallback: ApksOutputProgressCallback?) {
        if (!appInfo.isExpandApks) {
            handler.post {
                apksOutputProgressCallback?.onError()
            }
            return
        }
        handler.post {
            apksOutputProgressCallback?.onProgress()
        }
        val startTime = System.currentTimeMillis()
        val tempApksFolders = File(AppFolder.tempFolder, appInfo.packageName)
        val apksList = ArrayList<File>()
        appInfo.apksFilePath.forEach {
            val apkFile = File(it)
            val outputApkFile = File(tempApksFolders, apkFile.name)
            FsUtils.writeFileToFile(outputApkFile, apkFile)
            apksList.add(outputApkFile)
        }
        val zipApksFile=File(AppFolder.apksFolder!!,"${appInfo.packageName}_${appInfo.versionCode}_${appInfo.apksFilePath.size}.apks")
        ZipUtils.composeFileOrDir(zipApksFile, tempApksFolders)
        FsUtils.deleteFileOrDir(tempApksFolders)
        val endTime = System.currentTimeMillis()
        AppLogger.d(TAG, "${appInfo.label}总压缩耗时:${endTime - startTime} ")
        handler.post {
            apksOutputProgressCallback?.onComped(zipApksFile)
        }
    }
}