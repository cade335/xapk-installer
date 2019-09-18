package io.xapk.apkinstaller.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.text.TextUtils
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.File

object AppUtils {
    fun getAppIcon(packageManager: PackageManager, applicationInfo: ApplicationInfo): Bitmap? {
        try {
            packageManager.getApplicationIcon(applicationInfo)?.apply {
                if (this is BitmapDrawable) {
                    return this.bitmap
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this is AdaptiveIconDrawable) {
                    val newBitmap = Bitmap.createBitmap(this.intrinsicWidth,
                        this.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(newBitmap)
                    this.setBounds(0, 0, canvas.width, canvas.height)
                    this.draw(canvas)
                    return newBitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @WorkerThread
    @Synchronized
    fun getInstalledAppsInfo(mContext: Context): List<AppInfo> {
        return arrayListOf<AppInfo>().apply {
            mContext.packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
                .forEach {
                    this.add(getSingleInstalledAppInfo(mContext, it))
                }
        }
    }

    @WorkerThread
    fun getSingleInstalledAppInfo(mContext: Context, packName: String): AppInfo? {
        var appInfo: AppInfo? = null
        try {
            appInfo = getSingleInstalledAppInfo(mContext, mContext.packageManager
                .getPackageInfo(packName, PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS))
        } finally {
            return appInfo
        }
    }

    @WorkerThread
    private fun getSingleInstalledAppInfo(mContext: Context, packageInfo: PackageInfo): AppInfo {
        return AppInfo().apply {
            val applicationInfo = packageInfo.applicationInfo
            this.label = mContext.packageManager.getApplicationLabel(applicationInfo).toString()
            packageInfo.versionName?.let {
                this.versionName = it
            }
            this.versionCode = packageInfo.versionCode
            val packageName1 = packageInfo.packageName ?: String()
            this.packageName = packageName1
            this.firstInstallTime = packageInfo.firstInstallTime
            this.lastUpdateTime = packageInfo.lastUpdateTime
            this.isSystemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 || applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
            this.isEnabled = applicationInfo.enabled
            this.sourceDir = applicationInfo.sourceDir
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.minSdkVersion = applicationInfo.minSdkVersion.toString()
            }
            this.targetSdkVersion = applicationInfo.targetSdkVersion.toString()
            val updateFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                applicationInfo.splitSourceDirs?.forEach {
                    this.splitSourceDirs.add(it)
                }
                applicationInfo.splitSourceDirs == null || applicationInfo.splitSourceDirs.isEmpty()
            } else {
                true
            }
            val isUpdateApkFile1 = updateFlag && !isApkInDebug(applicationInfo)
            this.isUpdateFile = isUpdateApkFile1
            this.apkSize = getAppInfoSourceDirLength(this)
            val apksFilePath1 = arrayListOf<String>()
            if (applicationInfo.publicSourceDir != null) {
                apksFilePath1.add(applicationInfo.publicSourceDir)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    applicationInfo.splitPublicSourceDirs?.forEach {
                        apksFilePath1.add(it)
                    }
                }
                this.isExpandApks = true
            }
            this.apksFilePath = apksFilePath1

            val storageDirPath = FsUtils.getStorageDir()?.absolutePath
            var obbExists1 = false
            if (!TextUtils.isEmpty(packageName1) && !TextUtils.isEmpty(storageDirPath)) {
                val mainObbFileName = "main.${packageInfo.versionCode}.$packageName1.obb"
                val xApkMainPath1 = "Android${File.separator}obb${File.separator}$packageName1" +
                        "${File.separator}$mainObbFileName"
                val xApkMainAbsolutePath1 = "$storageDirPath${File.separator}$xApkMainPath1"
                val patchObbFileName = "patch.${packageInfo.versionCode}.$packageName1.obb"
                val xApkPatchPath1 = "Android${File.separator}obb${File.separator}$packageName1" +
                        "${File.separator}$patchObbFileName"
                val xApkPatchAbsolutePath1 = "$storageDirPath${File.separator}$xApkPatchPath1"
                val obbSize = FsUtils.getFileOrDirLength(xApkMainAbsolutePath1) + FsUtils.getFileOrDirLength(xApkPatchAbsolutePath1)

                if (obbSize > 0L) {
                    obbExists1 = true
                    this.xApkMainObbPath = xApkMainPath1
                    this.xApkMainObbAbsolutePath = xApkMainAbsolutePath1
                    this.xApkPatchObbPath = xApkPatchPath1
                    this.xApkPatchObbAbsolutePath = xApkPatchAbsolutePath1
                    this.xApkObbSize = obbSize
                }
            }
            this.obbExists = obbExists1
            this.isExpandXApk = obbExists1 && isUpdateApkFile1 || apksFilePath1.isNotEmpty()
            this.appTotalSize = this.apkSize + this.xApkObbSize
            packageInfo.requestedPermissions?.forEach {
                this.permissionsArrays.add(it)
            }
        }
    }

    private fun getAppInfoSourceDirLength(appInfo: AppInfo): Long {
        var length = 0L
        if (appInfo.splitSourceDirs.isNotEmpty()) {
            appInfo.splitSourceDirs.forEach {
                length += FsUtils.getFileOrDirLength(it)
            }
        } else {
            length = FsUtils.getFileOrDirLength(appInfo.sourceDir)
        }
        return length
    }

    private fun isApkInDebug(applicationInfo: ApplicationInfo): Boolean {
        return try {
            applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            false
        }
    }
}