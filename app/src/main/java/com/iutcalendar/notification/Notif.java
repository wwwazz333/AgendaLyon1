package com.iutcalendar.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

public class Notif {
    private static String NOTIFICATION_CHANNEL_ID;
    private static NotificationManager mNotificationManager;

    private final NotificationCompat.Builder notif;

    public Notif(Context context, String title, String msg, @DrawableRes int icon, PendingIntent pendingItent) {
        notif = new NotificationCompat.Builder(context, Notif.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(msg);
        notif.setChannelId(Notif.NOTIFICATION_CHANNEL_ID);

        notif.setContentIntent(pendingItent);
    }

    public static void init(Context context) {
        Notif.NOTIFICATION_CHANNEL_ID = "1001";
        NotificationChannel notificationChannel = new
                NotificationChannel(Notif.NOTIFICATION_CHANNEL_ID, "Changement dans l'agenda", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);

        Notif.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(notificationChannel);
    }

    public void show() {
        mNotificationManager.notify((int) System.currentTimeMillis(), notif.build());
    }
}
