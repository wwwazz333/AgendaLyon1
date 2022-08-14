package com.iutcalendar.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import com.univlyon1.tools.agenda.R;

public class NotificationChannels extends Application {

    public static final String CHANGE_EVENT_NOTIFICATION_ID = "Change event notification";
    public static final String ALARM_NOTIFICATION_ID = "Alarm notification";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        Context ctx = getApplicationContext();

        NotificationChannel channelChangeEvent = new NotificationChannel(CHANGE_EVENT_NOTIFICATION_ID, ctx.getString(R.string.change_schedule), NotificationManager.IMPORTANCE_DEFAULT);
        channelChangeEvent.enableLights(true);
        channelChangeEvent.setLightColor(Color.BLUE);
        channelChangeEvent.enableVibration(true);
        channelChangeEvent.setDescription(ctx.getString(R.string.notify_change));

        NotificationChannel channelAlarmNotif = new NotificationChannel(ALARM_NOTIFICATION_ID, ctx.getString(R.string.notif_for_alarm), NotificationManager.IMPORTANCE_HIGH);
        channelAlarmNotif.enableLights(true);
        channelAlarmNotif.setLightColor(Color.BLUE);
        channelAlarmNotif.enableVibration(true);
        channelAlarmNotif.setDescription(ctx.getString(R.string.notify_for_alarm));

        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        notificationManager.createNotificationChannel(channelChangeEvent);
        notificationManager.createNotificationChannel(channelAlarmNotif);
    }
}
