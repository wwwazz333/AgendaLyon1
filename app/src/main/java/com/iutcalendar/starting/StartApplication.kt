package com.iutcalendar.starting

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.notification.NotificationChannels
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R

class StartApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        SettingsApp.adapteTheme(applicationContext)
    }

    private fun initDateOffset() {
        DateCalendrier.zoneOffset = DataGlobal.getSavedString(applicationContext, "offset_time") ?: DateCalendrier.zoneOffset
    }


    private fun createNotificationChannels() {
        val ctx = applicationContext
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channelChangeEvent = NotificationChannel(
            NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID,
            ctx.getString(R.string.change_schedule),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channelChangeEvent.enableLights(true)
        channelChangeEvent.lightColor = Color.BLUE
        channelChangeEvent.enableVibration(true)
        channelChangeEvent.description = ctx.getString(R.string.notify_change)
        val channelAlarmNotif = NotificationChannel(
            NotificationChannels.ALARM_NOTIFICATION_ID,
            ctx.getString(R.string.notif_for_alarm),
            NotificationManager.IMPORTANCE_HIGH
        )
        channelAlarmNotif.enableLights(true)
        channelAlarmNotif.lightColor = Color.BLUE
        channelAlarmNotif.enableVibration(true)
        channelAlarmNotif.description = ctx.getString(R.string.notify_for_alarm)
        notificationManager.createNotificationChannel(channelChangeEvent)
        notificationManager.createNotificationChannel(channelAlarmNotif)
    }
}