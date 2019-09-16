package io.xapk.apkinstaller.utils

import android.content.Context
import io.xapk.apkinstaller.utils.bean.xapk.ApksBean
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.activity.InstallSplitApksActivity
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
}