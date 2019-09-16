package io.xapk.apkinstaller.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import io.xapk.apkinstaller.utils.AppLogger

class BootCompletedBroadcastReceiver : BaseBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            intent?.action?.let {
                AppLogger.d(logTag, it)
                if (TextUtils.equals(Intent.ACTION_BOOT_COMPLETED, it)) {
                    //LaunchServiceUtils.startInitAssistJonIntentService(context)
                }
            }
        }
    }

    override fun register(mContext: Context) {
        mContext.registerReceiver(this, IntentFilter().apply {
            this.addAction(Intent.ACTION_BOOT_COMPLETED)
        })
    }

    override fun unregister(mContext: Context) {
        mContext.unregisterReceiver(this)
    }
}