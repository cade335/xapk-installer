package io.xapk.apkinstaller.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import io.xapk.apkinstaller.utils.AppLogger

class AppInstallBroadcastReceiver : BaseBroadcastReceiver() {
    private var appStatusChangeLister: AppStatusChangeLister? = null

    interface AppStatusChangeLister {
        fun onInstall(packName: String)
        fun onRemove(packName: String)
    }

    fun setAppStatusChangeLister(appStatusChangeLister: AppStatusChangeLister) {
        this.appStatusChangeLister = appStatusChangeLister
    }

    override fun register(mContext: Context) {
        mContext.registerReceiver(this, IntentFilter().apply {
            this.addAction(Intent.ACTION_PACKAGE_ADDED)
            this.addAction(Intent.ACTION_PACKAGE_REPLACED)
            this.addAction(Intent.ACTION_PACKAGE_REMOVED)
            this.addDataScheme("package")
        })
    }

    override fun unregister(mContext: Context) {
        mContext.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let {
            intent.data?.schemeSpecificPart?.apply {
                AppLogger.d(logTag, "$it-->$this")
                when {
                    TextUtils.equals(Intent.ACTION_PACKAGE_ADDED, it) -> {
                        appStatusChangeLister?.onInstall(this)
                    }
                    TextUtils.equals(Intent.ACTION_PACKAGE_REPLACED, it) -> {
                    }
                    TextUtils.equals(Intent.ACTION_PACKAGE_REMOVED, it) -> {
                        appStatusChangeLister?.onRemove(this)
                    }
                }
            }
        }
    }
}