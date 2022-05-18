package com.iutcalendar.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.iutcalendar.calendrier.CurrentDate;

public class BackgroundNotification extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "This is a Service running in Background", Toast.LENGTH_SHORT).show();
        Log.d("Background", "start");

        Intent notifIntent = new Intent(getApplicationContext(), NotificationLauncher.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, notifIntent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, new CurrentDate().getTimeInMillis(), 60000, pintent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
        Log.d("Background", "destroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
