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
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.data.Tuple;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.notification.Notif;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.task.Task;

import java.util.List;

public class ForgroundServiceUpdate extends Service {
    private static final long INTERVAL_UPDATE = 15 * 60_000;

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
        Notif notif = new Notif(this, Notif.UPDATE_BACKGROUND_NOTIFICATION_ID, NotificationManager.IMPORTANCE_LOW,
                "maj.", "maj. agenda en arrière plan", R.drawable.ic_update, null);
        startForeground(startId, notif.build());

        new Thread(() -> {

            calendrier = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
            setUpAlarm();

            updateFile();
            Calendrier cal = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
            cal.deleteUselessTask(getApplicationContext());

            new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                    "Fin maj.", "la maj est fini, permantent notif devrais disparaitre", R.drawable.ic_update, null).show();

            stopForeground(true);
            stopSelf();
            Log.d("Background", "end Service");
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


        for (int dayAfter = 0; dayAfter < 7; dayAfter++) {
            List<EventCalendrier> events = calendrier.getEventsOfDay(new CurrentDate().addDay(dayAfter));

            if (!events.isEmpty()) {
                EventCalendrier currEvent = events.get(0);
                long timeAlarmRing = currEvent.getDate().getTimeInMillis() - getAlarmRingTimeBefore();
                CurrentDate d = new CurrentDate();
                d.setTimeInMillis(timeAlarmRing);
                Log.d("Background", "veux mettre alarm à : " + d + " " + d.timeToString());


                //sauvgrade si alarme désactivé pour se jour
                boolean prevWasActivate = true;
                Task alarmTaskOfCurrDay = PersonnalCalendrier.getInstance(getApplicationContext()).getAlarmOf(currEvent.getUID());
                if (alarmTaskOfCurrDay != null) {
                    prevWasActivate = alarmTaskOfCurrDay.isAlarmActivate();
                }
                //supprimer toutes les alarmes (Tash) pour se jour
                for (EventCalendrier event : events) {
                    //del tt les alarm du jour (si cours après qui étais premier et qu'il y en à un qui c'est déplacer devant
                    //del la tache
                    PersonnalCalendrier.getInstance(getApplicationContext()).removeAllAlarmOf(
                            getApplicationContext(), event.getUID());
                    //del l'alarm
                    Alarm.cancelAlarm(getApplicationContext(), currEvent.getUID());
                }

                if (timeAlarmRing > System.currentTimeMillis()) {
                    Log.d("Background", "alarm set");
                    //remet ou met l'alarm si besoin
                    Task taskAlarmToAdd = new Task("Alarm auto", currEvent.getUID(), true);
                    taskAlarmToAdd.setAlarmActivate(prevWasActivate);
                    PersonnalCalendrier.getInstance(getApplicationContext()).addLinkedTask(taskAlarmToAdd, currEvent);

                    if (prevWasActivate) {
                        Alarm.setAlarm(getApplicationContext(), timeAlarmRing, currEvent.getUID(), Alarm.START);
                    }

                } else {
                    Log.d("Background", "alarm passer => non mise");
                }
            }
        }
        PersonnalCalendrier.getInstance(getApplicationContext()).save(getApplicationContext());
    }

    private long getAlarmRingTimeBefore() {
        return DataGlobal.getSavedInt(getApplicationContext(), DataGlobal.ALARM_RING_TIME_BEFOR) * 60_000L;
    }

    private void updateFile() {
        //FIXME optimize
        Calendrier prev = calendrier;
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
