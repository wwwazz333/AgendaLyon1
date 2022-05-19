package com.iutcalendar.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

public class Notif extends NotificationCompat.Builder {
    public static final String CHANGE_EVENT_NOTIFICATION_ID = "Change event notification";
    public static final String UPDATE_BACKGROUND_NOTIFICATION_ID = "Background update permanent notification";
    private NotificationManager mNotificationManager;

    public Notif(Context context, String chanelId, int importance, String title, String msg, @DrawableRes int icon, PendingIntent pendingItent) {
        super(context, chanelId);
        initChanel(context, chanelId, importance);
        setSmallIcon(icon);
        setContentTitle(title);
        setContentText(msg);
        setContentIntent(pendingItent);
    }

    public void initChanel(Context context, String chanelId, int importance) {
        NotificationChannel notificationChannel = new
                NotificationChannel(chanelId, chanelId, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(true);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(notificationChannel);
    }

    public void show() {
        mNotificationManager.notify((int) System.currentTimeMillis(), build());
    }

}
