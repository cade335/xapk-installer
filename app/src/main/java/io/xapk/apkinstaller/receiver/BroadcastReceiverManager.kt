package io.xapk.apkinstaller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

import androidx.localbroadcastmanager.content.LocalBroadcastManager

object BroadcastReceiverManager {
    fun register(context: Context, receiver: BroadcastReceiver, filter: IntentFilter) {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)
    }

    fun unregister(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }
}
