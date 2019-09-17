package io.xapk.apkinstaller.utils

import android.content.Context
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.utils.bean.xapk.ApksBean
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.activity.InstallSplitApksActivity
import io.xapk.apkinstaller.ui.activity.WebViewActivity
import io.xapk.apkinstaller.ui.service.XAPKIntentService

object LaunchUtils {

    fun startXApkOutputZipService(mContext: Context, appInfo: AppInfo) {
        XAPKIntentService.startXApkOutPutZip(mContext, appInfo).apply {
            XAPKIntentService.enqueueWorkService(mContext, this)
        }
    }

    fun startApkExportService(mContext: Context, appInfo: AppInfo) {
        XAPKIntentService.startApkExport(mContext, appInfo).apply {
            XAPKIntentService.enqueueWorkService(mContext, this)
        }
    }

    fun startApksExportService(mContext: Context, appInfo: AppInfo) {
        XAPKIntentService.startApksExport(mContext, appInfo).apply {
            XAPKIntentService.enqueueWorkService(mContext, this)
        }
    }

    fun startInstallSplitApksActivity(mContext: Context, apksBean: ApksBean) {
        mContext.startActivity(InstallSplitApksActivity.newInstanceIntent(mContext, apksBean))
    }

    fun startPrivacyPolicyActivity(mContext: Context) {
        mContext.startActivity(WebViewActivity.newInstanceIntent(mContext, "https://xapk.io/privacy-policy.html", mContext.getString(R.string.privacy_policy)))
    }

    fun startDevelopmentCodeActivity(mContext: Context) {
        mContext.startActivity(WebViewActivity.newInstanceIntent(mContext, "https://xapk.io/open-source-license.html", mContext.getString(R.string.development_code)))
    }

    fun startAboutActivity(mContext: Context) {
        mContext.startActivity(WebViewActivity.newInstanceIntent(mContext, "https://xapk.io/", mContext.getString(R.string.about)))
    }
}