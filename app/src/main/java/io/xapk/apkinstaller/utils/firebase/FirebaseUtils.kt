package io.xapk.apkinstaller.utils.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.xapk.apkinstaller.app.App
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.unit.FormatUtils

/**
 * @author xiongke
 * @date 2019-09-24
 */
object FirebaseUtils {
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(App.mContext) }

    private fun logEvent(category: String, params: Bundle) {
        try {
            firebaseAnalytics.logEvent(category, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickInstallXApkOrApk(apkAssetBean: ApkAssetBean) {
        var category: String? = null
        val bundle = Bundle()
        val apkInfo = apkAssetBean.apkInfo
        val apksInfo = apkAssetBean.apksInfo
        val xApkInfo = apkAssetBean.xApkInfo
        if (apkAssetBean.apkAssetType == ApkAssetType.Apk && apkInfo != null) {
            category = "installAPK"
            bundle.putString("label", apkInfo.label)
            bundle.putString("packageName", apkInfo.packageName)
            bundle.putInt("versionCode", apkInfo.versionCode)
            bundle.putString("versionName", apkInfo.versionName)
            bundle.putString("appSize", FormatUtils.formatFileLength(apkInfo.appSize))
        } else if (apkAssetBean.apkAssetType == ApkAssetType.XAPK && xApkInfo != null) {
            category = "installXAPK"
            bundle.putString("label", xApkInfo.label)
            bundle.putString("packageName", xApkInfo.packageName)
            bundle.putInt("versionCode", xApkInfo.versionCode)
            bundle.putString("versionName", xApkInfo.versionName)
            bundle.putString("appSize", FormatUtils.formatFileLength(xApkInfo.appSize))
        } else if (apkAssetBean.apkAssetType == ApkAssetType.Apks && apksInfo != null) {
            category = "installAPKS"
            bundle.putString("fileName", apksInfo.fileName)
        }
        category?.let {
            logEvent(it, bundle)
        }
    }

    fun clickUnInstallApk(appInfo: AppInfo) {
        val category = "unInstall"
        val bundle = Bundle().apply {
            this.putString("label", appInfo.label)
            this.putString("packageName", appInfo.packageName)
            this.putInt("versionCode", appInfo.versionCode)
            this.putString("versionName", appInfo.versionName)
            this.putString("appSize", FormatUtils.formatFileLength(appInfo.appTotalSize))
        }
        logEvent(category, bundle)
    }

    fun clickExportApk(appInfo: AppInfo) {
        val category = "exportApk"
        val bundle = Bundle().apply {
            this.putString("label", appInfo.label)
            this.putString("packageName", appInfo.packageName)
            this.putInt("versionCode", appInfo.versionCode)
            this.putString("versionName", appInfo.versionName)
            this.putString("appSize", FormatUtils.formatFileLength(appInfo.appTotalSize))
        }
        logEvent(category, bundle)
    }

    fun clickExportXApk(appInfo: AppInfo) {
        val category = "exportXApk"
        val bundle = Bundle().apply {
            this.putString("label", appInfo.label)
            this.putString("packageName", appInfo.packageName)
            this.putInt("versionCode", appInfo.versionCode)
            this.putString("versionName", appInfo.versionName)
            this.putString("appSize", FormatUtils.formatFileLength(appInfo.appTotalSize))
            this.putString("isExpandApks", appInfo.isExpandApks.toString())
            this.putString("obbExists", appInfo.obbExists.toString())
        }
        logEvent(category, bundle)
    }

    fun clickShare() {
        val category = "share"
        logEvent(category, Bundle())
    }
}