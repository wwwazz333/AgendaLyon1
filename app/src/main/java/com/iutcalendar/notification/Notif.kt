package com.iutcalendar.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

class Notif(context: Context, private val chanelId: String, title: String?, msg: String?, @DrawableRes icon: Int, pendingIntent: PendingIntent?) : NotificationCompat.Builder(context, chanelId) {
    private val mNotificationManager: NotificationManager

    init {
        setSmallIcon(icon)
        setContentTitle(title)
        setContentText(msg)
        setAutoCancel(true)
        setContentIntent(pendingIntent)
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun show() {
        if (chanelId == NotificationChannels.ALARM_NOTIFICATION_ID) {
            mNotificationManager.notify(0, build())
        } else {
            mNotificationManager.notify(System.currentTimeMillis().toInt(), build())
        }
    }

    companion object {
        fun cancelAlarmNotif(context: Context) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(0)
        }
    }
}