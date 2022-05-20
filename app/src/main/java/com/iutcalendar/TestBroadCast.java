package com.iutcalendar;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.calendar.iutcalendar.R;
import com.iutcalendar.notification.Notif;

public class TestBroadCast extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", "test start");


        Notif notif = new Notif(this, Notif.UPDATE_BACKGROUND_NOTIFICATION_ID, NotificationManager.IMPORTANCE_LOW,
                "proute", "gougougaga", R.drawable.ic_update, null);
        startForeground(startId, notif.build());
//        stopForeground(flags);
        stopForeground(true);
        stopSelf();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
