package io.xapk.apkinstaller.utils.notfiy

import io.xapk.apkinstaller.utils.bean.apk.AppInfo

object IdentifierUtils {
    val notificationChannelId by lazy { "Notification-Id" }
    val notificationChannelName by lazy { "Notification-Name" }

    fun getXApkOutputZipNotifyId(appInfo: AppInfo): Int {
        return "xApk-${appInfo.packageName}-${appInfo.versionCode}".hashCode()
    }

    fun getApkExportNotifyId(appInfo: AppInfo): Int {
        return "Apk-${appInfo.packageName}-${appInfo.versionCode}".hashCode()
    }

    fun getApksExportNotifyId(appInfo: AppInfo): Int {
        return "Apks-${appInfo.packageName}-${appInfo.versionCode}".hashCode()
    }
}