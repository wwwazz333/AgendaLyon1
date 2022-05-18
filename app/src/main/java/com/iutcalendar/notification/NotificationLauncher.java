package com.iutcalendar.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class NotificationLauncher extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", " NotificationLauncher extends Service:  start");
        stopService(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Background", " NotificationLauncher extends Service:  end");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}