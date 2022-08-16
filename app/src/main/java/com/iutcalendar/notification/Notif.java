package com.iutcalendar.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import com.iutcalendar.data.DataGlobal;

public class Notif extends NotificationCompat.Builder {
    private final String chanelId;
    private final NotificationManager mNotificationManager;

    public Notif(Context context, String chanelId, String title, String msg, @DrawableRes int icon, PendingIntent pendingItent) {
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
        if (chanelId.equals(NotificationChannels.ALARM_NOTIFICATION_ID)) {
            mNotificationManager.notify(0, build());
        } else {
            mNotificationManager.notify((int) System.currentTimeMillis(), build());
        }

    }
}
