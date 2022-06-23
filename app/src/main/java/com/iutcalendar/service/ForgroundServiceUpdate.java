package com.iutcalendar.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.Nullable;
import com.univlyon1.tools.agenda.R;
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.notification.Notif;

public class ForgroundServiceUpdate extends Service/*BroadcastReceiver*/ {
    private static final long INTERVAL_UPDATE = 20 * 60_000;

    private static final String TAG_WAKE_LOCK = "IUTCalendar::majAgenda";

    private Calendrier calendrier;

    public static void start(Context context) {
        Log.d("Background", "start alarm repeating");
        Intent intentService = new Intent(context, ForgroundServiceUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getForegroundService(context, 0, intentService, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_UPDATE, INTERVAL_UPDATE, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", "start Service at " + new CurrentDate().timeToString());


        new Thread(() -> {
            //reveil le CPU
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK);
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);


            if (wakeLock.isHeld()) {
                long timerCount = System.currentTimeMillis();

                long read, setal, upmaj;
                calendrier = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
                read = (System.currentTimeMillis() - timerCount) / 1000;
                timerCount = System.currentTimeMillis();

                Alarm.setUpAlarm(getApplicationContext(), calendrier);
                setal = (System.currentTimeMillis() - timerCount) / 1000;
                timerCount = System.currentTimeMillis();


                updateFile(getApplicationContext());
                upmaj = (System.currentTimeMillis() - timerCount) / 1000;

                if (DataGlobal.isDebug(getApplicationContext())) {
                    String txt = "";
                    txt += "read cal : " + read + "s\n";
                    txt += "maj : " + setal + "s\n";
                    txt += "alarm : " + upmaj + "s\n";
                    new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                            "Tps background", txt, R.drawable.ic_update, null).show();
                }
            } else {
                new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                        "background", "wake cpu doesn't work", R.drawable.ic_update, null).show();
            }


            //Plus besoin du CPU
            wakeLock.release();

            Log.d("Background", "end Service");
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateFile(Context context) {
        FileGlobal.updateAndGetChange(context, calendrier, ((context1, intent) -> {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            Notif notif = new Notif(context, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT, context.getResources().getString(R.string.event), "changes : all the changes", R.drawable.ic_edit, pendingIntent);
            notif.show();
        }));
    }
}
