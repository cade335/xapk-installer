package io.xapk.apkinstaller.utils.io

import android.os.Environment

import io.xapk.apkinstaller.BuildConfig
import io.xapk.apkinstaller.app.App
import io.xapk.apkinstaller.utils.unit.JodaTimeUtils

import java.io.File
import java.util.*

object AppFolder {
    private val APP_FOLDER_NAME: String
        get() {
            return if (BuildConfig.IS_RELEASE) {
                "XAPK Installer"
            } else {
                "XAPK Installer-${BuildConfig.BUILD_TYPE}"
            }
        }
    private const val CRASH_FOLDER_NAME = "crash"
    private const val TEMP_FOLDER_NAME = "temp"
    private const val APK_FOLDER_NAME = "apk"
    private const val XAPK_FOLDER_NAME = "xapk"
    private const val APKS_FOLDER_NAME = "apks"

    val crashFolder: File?
        get() = createAppFolderDirectory(CRASH_FOLDER_NAME)

    val tempFolder: File?
        get() = createAppFolderDirectory(TEMP_FOLDER_NAME)

    val apkFolder: File?
        get() = createAppFolderDirectory(APK_FOLDER_NAME)

    val apksFolder: File?
        get() = createAppFolderDirectory(APKS_FOLDER_NAME)

    val xApkFolder: File?
        get() = createAppFolderDirectory(XAPK_FOLDER_NAME)

    fun getXApkInstallTempFolder(packageName: String): File {
        val tempFile = File(tempFolder, packageName)
        FsUtils.createOnNotFound(tempFile)
        return tempFile
    }

    val crashFile: File?
        get() {
            return if (BuildConfig.DEBUG) {
                File(crashFolder, "crash_${JodaTimeUtils.formatDataToShortDateInfo(Date())}.txt")
            } else {
                File(crashFolder, "crash_${JodaTimeUtils.formatDataToShortDateInfo(Date())}.crash")
            }
        }

    private val appFolder: File?
        get() {
            return if (FsUtils.isSdUsable) {
                val appFolder = File(Environment.getExternalStorageDirectory(), APP_FOLDER_NAME)
                FsUtils.createOnNotFound(appFolder)
            } else {
                null
            }
        }

    private fun createAppFolderDirectory(directoryName: String): File? {
        return FsUtils.createOnNotFound(File(appFolder, directoryName))
    }

    private fun createAppCacheDirectory(directoryName: String): File? {
        return FsUtils.createOnNotFound(File(App.mContext.cacheDir, directoryName))
    }
}
