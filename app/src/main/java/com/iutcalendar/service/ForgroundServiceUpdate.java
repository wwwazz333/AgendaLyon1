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
import com.iutcalendar.MainActivity;
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.data.Tuple;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.notification.Notif;

import java.util.List;

public class ForgroundServiceUpdate extends Service {
    //TODO start on boot
    private static final long INTERVAL_UPDATE = 15 * 60_000;


    public static void start(Context context) {
        Intent intentService = new Intent(context, ForgroundServiceUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getForegroundService(context, 0, intentService, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_UPDATE, INTERVAL_UPDATE, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", "start Service");
        Notif notif = new Notif(this, Notif.UPDATE_BACKGROUND_NOTIFICATION_ID, NotificationManager.IMPORTANCE_LOW,
                "maj.", "mise à jour de l'agenda", R.drawable.ic_update, null);
        startForeground(startId, notif.build());

        new Thread(() -> {
            updateFile();
            setUpAlarm();
            stopForeground(true);
            stopSelf();
        }).start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setUpAlarm() {
        //TODO si je le fais que ici sa s'actualise pas hyper rapidement
        Calendrier cal = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
        List<EventCalendrier> events = cal.getEventsOfDay(new CurrentDate());

        if (!events.isEmpty()) {
            long timeAlarmRing = events.get(0).getDate().getTimeInMillis() - 2 * 3600 * 1000;
            CurrentDate d = new CurrentDate();
            d.setTimeInMillis(timeAlarmRing);
            Log.d("Background", "veux mettre alarm à : " + d.timeToString());
            if (timeAlarmRing > System.currentTimeMillis()) {
                Log.d("Background", "alarm set");
                Alarm.setAlarm(getApplicationContext(), timeAlarmRing);
            } else {
                Log.d("Background", "alarm passer => non mise");
            }

        }
    }

    public void updateFile() {
        //FIXME optimize
        Calendrier prev = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
        FileDownload.updateFichier(FileGlobal.getFileDownload(getApplicationContext()).getAbsolutePath(), getApplicationContext());
        Calendrier nouveau = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));

        List<Tuple<EventCalendrier, Calendrier.InfoChange>> changes = nouveau.getChangedEvent(prev);

        if (!changes.isEmpty()) {
            String changesMsg = Calendrier.changeToString(getApplicationContext(), changes);

            //TODO string: message de notif
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("changes", changesMsg);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            Notif notif = new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                    getString(R.string.event), "changes : all the changes", R.drawable.ic_edit, pendingIntent);
            notif.show();
        }
    }
}
