package com.iutcalendar.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.notification.Notif;

public class ForgroundServiceUpdate extends Service {
    private static final long INTERVAL_UPDATE = 20 * 60_000;

    private Calendrier calendrier;


    public static void start(Context context) {
        Intent intentService = new Intent(context, ForgroundServiceUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getForegroundService(context, 0, intentService, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_UPDATE, INTERVAL_UPDATE, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", "start Service at " + new CurrentDate().timeToString());
        Notif notif = new Notif(this, Notif.UPDATE_BACKGROUND_NOTIFICATION_ID, NotificationManager.IMPORTANCE_LOW, "maj.", "maj. agenda en arrière plan", R.drawable.ic_update, null);
        startForeground(startId, notif.build());


        new Thread(() -> {
            long timerCount = System.currentTimeMillis();

            long read, setal, upmaj;
            calendrier = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
            read = (System.currentTimeMillis() - timerCount) / 1000;
            timerCount = System.currentTimeMillis();

            Alarm.setUpAlarm(getApplicationContext(), calendrier);
            setal = (System.currentTimeMillis() - timerCount) / 1000;
            timerCount = System.currentTimeMillis();


            updateFile();
            upmaj = (System.currentTimeMillis() - timerCount) / 1000;

            if (DataGlobal.isDebug(getApplicationContext())) {
//            if (read != 0 || setal != 0 || upmaj != 0) {
                String txt = "";
                txt += "read cal : " + read + "s\n";
                txt += "maj : " + setal + "s\n";
                txt += "alarm : " + upmaj + "s\n";
                new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                        "Tps background", txt, R.drawable.ic_update, null).show();
//            }
            }


            stopForeground(true);
            stopSelf();
            Log.d("Background", "end Service");
        }).start();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long getAlarmRingTimeBefore() {
        return DataGlobal.getSavedInt(getApplicationContext(), DataGlobal.ALARM_RING_TIME_BEFOR) * 60_000L;
    }

    private void updateFile() {
        FileGlobal.updateAndGetChange(getApplicationContext(), calendrier, ((context, intent) -> {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            Notif notif = new Notif(context, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT, context.getResources().getString(R.string.event), "changes : all the changes", R.drawable.ic_edit, pendingIntent);
            notif.show();
        }));


    }
}
