package io.xapk.apkinstaller.receiver

import android.content.BroadcastReceiver
import android.content.Context

abstract class BaseBroadcastReceiver : BroadcastReceiver() {
    protected val logTag: String by lazy { javaClass.simpleName }
    abstract fun register(mContext: Context)
    abstract fun unregister(mContext: Context)
}