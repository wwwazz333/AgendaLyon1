package com.iutcalendar.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import com.univlyon1.tools.agenda.R

class NotificationChannels : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val ctx = applicationContext
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channelChangeEvent = NotificationChannel(CHANGE_EVENT_NOTIFICATION_ID, ctx.getString(R.string.change_schedule), NotificationManager.IMPORTANCE_DEFAULT)
        channelChangeEvent.enableLights(true)
        channelChangeEvent.lightColor = Color.BLUE
        channelChangeEvent.enableVibration(true)
        channelChangeEvent.description = ctx.getString(R.string.notify_change)
        val channelAlarmNotif = NotificationChannel(ALARM_NOTIFICATION_ID, ctx.getString(R.string.notif_for_alarm), NotificationManager.IMPORTANCE_HIGH)
        channelAlarmNotif.enableLights(true)
        channelAlarmNotif.lightColor = Color.BLUE
        channelAlarmNotif.enableVibration(true)
        channelAlarmNotif.description = ctx.getString(R.string.notify_for_alarm)
        notificationManager.createNotificationChannel(channelChangeEvent)
        notificationManager.createNotificationChannel(channelAlarmNotif)
    }

    companion object {
        const val CHANGE_EVENT_NOTIFICATION_ID = "Change event notification"
        const val ALARM_NOTIFICATION_ID = "Alarm notification"
    }
}