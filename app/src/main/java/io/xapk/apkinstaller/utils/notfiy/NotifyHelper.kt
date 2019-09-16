package io.xapk.apkinstaller.utils.notfiy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import io.xapk.apkinstaller.utils.AppUtils

class NotifyHelper(val mContext: Context) {
    val notificationManager by lazy { mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    val notificationIcon by lazy { AppUtils.getAppIcon(mContext.packageManager,mContext.applicationInfo) }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(IdentifierUtils.notificationChannelId, IdentifierUtils.notificationChannelName, NotificationManager.IMPORTANCE_LOW).apply {
                this.setShowBadge(true)
                this.enableLights(true)
                this.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                this.setBypassDnd(true)
                notificationManager.createNotificationChannel(this)
            }
        }
    }
}