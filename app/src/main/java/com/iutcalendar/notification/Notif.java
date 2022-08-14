package com.iutcalendar.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

public class Notif extends NotificationCompat.Builder {
    public static final String CHANGE_EVENT_NOTIFICATION_ID = "Change event notification";
    public static final String UPDATE_BACKGROUND_NOTIFICATION_ID = "Background update permanent notification";
    public static final String ALARM_NOTIFICATION_ID = "Alarm notification";
    private final String chanelId;
    private final NotificationManager mNotificationManager;

    public Notif(Context context, String chanelId, int importance, String title, String msg, @DrawableRes int icon, PendingIntent pendingItent) {
        super(context, chanelId);
        this.chanelId = chanelId;
        setSmallIcon(icon);
        setContentTitle(title);
        setContentText(msg);
        setAutoCancel(true);
        setContentIntent(pendingItent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void cancelAlarmNotif(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }

    public void show() {
        if (chanelId.equals(ALARM_NOTIFICATION_ID)) {
            mNotificationManager.notify(0, build());
        } else {
            mNotificationManager.notify((int) System.currentTimeMillis(), build());
        }

    }
}
