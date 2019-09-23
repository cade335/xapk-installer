package io.xapk.apkinstaller.utils.asset

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.AppLogger
import io.xapk.apkinstaller.utils.io.AppFolder
import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.File

object ApkExportUtils {
    private val TAG by lazy { javaClass.simpleName }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    interface ApkOutputProgressCallback {
        @MainThread
        fun onProgress()

        @MainThread
        fun onComped(apkFile: File)

        @MainThread
        fun onError(apkFile: File? = null)
    }

    @WorkerThread
    @Synchronized
    fun exportApk(appInfo: AppInfo, apkOutputProgressCallback: ApkOutputProgressCallback?) {
        if (!appInfo.isUpdateFile) {
            handler.post {
                apkOutputProgressCallback?.onError()
            }
            return
        }
        handler.post {
            apkOutputProgressCallback?.onProgress()
        }
        val startTime = System.currentTimeMillis()
        val apkFile = File(appInfo.sourceDir)
        val outputApkFile = File(AppFolder.apkFolder, "${appInfo.packageName}.${appInfo.versionCode}.apk")
        FsUtils.writeFileToFile(outputApkFile, apkFile)
        if (!FsUtils.exists(outputApkFile) || FsUtils.getFileOrDirLength(outputApkFile) == 0L) {
            handler.post {
                apkOutputProgressCallback?.onError(outputApkFile)
            }
            return
        }
        val endTime = System.currentTimeMillis()
        AppLogger.d(TAG, "${appInfo.label} Zip times:${endTime - startTime} ")
        handler.post {
            apkOutputProgressCallback?.onComped(outputApkFile)
        }
    }
}