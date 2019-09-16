package io.xapk.apkinstaller.ui.service.presenter

import android.content.Context
import androidx.core.app.NotificationCompat
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.model.event.ApkExportCompleteEvent
import io.xapk.apkinstaller.model.event.ApksExportCompleteEvent
import io.xapk.apkinstaller.model.event.EventManager
import io.xapk.apkinstaller.model.event.XApkExportCompleteEvent
import io.xapk.apkinstaller.ui.base.IBasePresenter
import io.xapk.apkinstaller.ui.service.contract.AssistSerContract
import io.xapk.apkinstaller.utils.asset.ApkExportUtils
import io.xapk.apkinstaller.utils.asset.ApksExportUtils
import io.xapk.apkinstaller.utils.asset.XApkOutputStatus
import io.xapk.apkinstaller.utils.asset.XApkOutputUtils
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.notfiy.IdentifierUtils
import io.xapk.apkinstaller.utils.notfiy.NotifyHelper
import java.io.File

class AssistSerPresenter : IBasePresenter<AssistSerContract.StepView>(),
    AssistSerContract.Presenter {

    private var notifyHelper: NotifyHelper? = null
    override fun apkExport(mContext: Context, appInfo: AppInfo) {
        if (mRootView == null) {
            return
        }
        ApkExportUtils.exportApk(appInfo, object : ApkExportUtils.ApkOutputProgressCallback {
            override fun onProgress() {
                apkExportNotify(mContext,appInfo,mContext.getString(R.string.apk_export_ing),false)
            }

            override fun onComped(apkFile: File) {
                apkExportNotify(mContext,appInfo,mContext.getString(R.string.apk_export_complete), isCancel = true, isComplete = true)
                EventManager.post(ApkExportCompleteEvent(appInfo))
            }

            override fun onError(apkFile: File?) {
                apkExportNotify(mContext,appInfo,mContext.getString(R.string.apk_export_failed), isCancel = true, isComplete = true)
                FsUtils.deleteFileOrDir(apkFile)
            }
        })
    }

    override fun xApkOutputZip(mContext: Context, appInfo: AppInfo) {
        if (mRootView == null) {
            return
        }
        XApkOutputUtils.compressXApk(mContext, appInfo, object : XApkOutputUtils.XApkOutputProgressCallback {
            override fun onStart() {
                xApkOutZipNotify(mContext, appInfo, mContext.getString(R.string.xapk_start_ing), false)
            }

            override fun onProgress(xApkOutputStatus: XApkOutputStatus) {
                val hintText = when (xApkOutputStatus) {
                    XApkOutputStatus.ZipIng -> {
                        mContext.getString(R.string.xapk_output_ing)
                    }
                    else -> {
                        mContext.getString(R.string.xapk_start_ing)
                    }
                }
                xApkOutZipNotify(mContext, appInfo, hintText, false)
            }

            override fun onComped(tempDir: File, xApkFile: File) {
                xApkOutZipNotify(mContext, appInfo, mContext.getString(R.string.xapk_output_complete), isCancel = true, isComplete = true)
                FsUtils.deleteFileOrDir(tempDir)
                EventManager.post(XApkExportCompleteEvent(appInfo))
            }

            override fun onError(tempDir: File?, xApkFile: File?) {
                xApkOutZipNotify(mContext, appInfo, mContext.getString(R.string.xapk_output_failed), isCancel = true, isComplete = true)
                FsUtils.deleteFileOrDir(tempDir)
                FsUtils.deleteFileOrDir(xApkFile)
            }
        })
    }

    override fun apksExport(mContext: Context, appInfo: AppInfo) {
        if (mRootView == null) {
            return
        }
        ApksExportUtils.exportApks(appInfo, object : ApksExportUtils.ApksOutputProgressCallback {
            override fun onProgress() {
                apksExportNotify(mContext,appInfo,mContext.getString(R.string.apks_export_ing),false)
            }

            override fun onComped(apksFile: File) {
                apksExportNotify(mContext,appInfo,mContext.getString(R.string.apks_export_complete), isCancel = true, isComplete = true)
                EventManager.post(ApksExportCompleteEvent(appInfo))
            }

            override fun onError() {
                apksExportNotify(mContext,appInfo,mContext.getString(R.string.apks_export_failed), isCancel = true, isComplete = true)
            }
        })
    }

    private fun xApkOutZipNotify(mContext: Context, appInfo: AppInfo, hintInfo: String, isCancel: Boolean, isComplete: Boolean = false) {
        val notificationId = IdentifierUtils.getXApkOutputZipNotifyId(appInfo)
        notifyHelper = notifyHelper ?: NotifyHelper(mContext)
        val smallIcon = if (isComplete) {
            R.drawable.ic_apk_status_complete
        } else {
            R.drawable.ic_compress
        }
        notifyHelper?.apply {
            val notificationCompat = NotificationCompat.Builder(mContext, IdentifierUtils.notificationChannelId)
                .setSmallIcon(smallIcon)
                .setLargeIcon(this.notificationIcon)
                .setOngoing(!isCancel)
                .setAutoCancel(isCancel)
                .setContentTitle("${appInfo.label} $hintInfo")
            this.notificationManager.notify(notificationId, notificationCompat.build())
        }
    }

    private fun apkExportNotify(mContext: Context, appInfo: AppInfo, hintInfo: String, isCancel: Boolean, isComplete: Boolean = false) {
        val notificationId = IdentifierUtils.getApkExportNotifyId(appInfo)
        notifyHelper = notifyHelper ?: NotifyHelper(mContext)
        val smallIcon = if (isComplete) {
            R.drawable.ic_apk_status_complete
        } else {
            R.drawable.ic_compress
        }
        notifyHelper?.apply {
            val notificationCompat = NotificationCompat.Builder(mContext, IdentifierUtils.notificationChannelId)
                .setSmallIcon(smallIcon)
                .setLargeIcon(this.notificationIcon)
                .setOngoing(!isCancel)
                .setAutoCancel(isCancel)
                .setContentTitle("${appInfo.label} $hintInfo")
            this.notificationManager.notify(notificationId, notificationCompat.build())
        }
    }

    private fun apksExportNotify(mContext: Context, appInfo: AppInfo, hintInfo: String, isCancel: Boolean, isComplete: Boolean = false) {
        val notificationId = IdentifierUtils.getApksExportNotifyId(appInfo)
        notifyHelper = notifyHelper ?: NotifyHelper(mContext)
        val smallIcon = if (isComplete) {
            R.drawable.ic_apk_status_complete
        } else {
            R.drawable.ic_compress
        }
        notifyHelper?.apply {
            val notificationCompat = NotificationCompat.Builder(mContext, IdentifierUtils.notificationChannelId)
                .setSmallIcon(smallIcon)
                .setLargeIcon(this.notificationIcon)
                .setOngoing(!isCancel)
                .setAutoCancel(isCancel)
                .setContentTitle("${appInfo.label} $hintInfo")
            this.notificationManager.notify(notificationId, notificationCompat.build())
        }
    }
}