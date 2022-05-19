package com.iutcalendar.notification;

import android.app.ActivityManager;
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
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.data.Tuple;
import com.iutcalendar.filedownload.FileDownload;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundNotification extends Service {
    //TODO start on boot

    public static boolean foregroundServiceRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundNotification.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Background", "start");

//        Intent notifIntent = new Intent(getApplicationContext(), NotificationLauncher.class);
//        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, notifIntent, 0);
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, new CurrentDate().getTimeInMillis(), 60000, pintent);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("Background", "start update");
                repeatedTask();
                Log.d("Background", "end update");
            }
        }, 0, 15 * 60_000);


        Notif notif = new Notif(this, Notif.UPDATE_BACKGROUND_NOTIFICATION_ID, NotificationManager.IMPORTANCE_LOW,
                "maj.", "mise à jour de l'agenda", R.drawable.ic_update, null);
        startForeground(startId, notif.build());

        return Service.START_STICKY;
    }

    public void repeatedTask() {
        Calendrier prev = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));
        FileDownload.updateFichier(FileGlobal.getFileDownload(getApplicationContext()).getAbsolutePath(), getApplicationContext());
        Calendrier nouveau = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getApplicationContext())));

        List<Tuple<EventCalendrier, Calendrier.InfoChange>> changes = nouveau.getChangedEvent(prev);

        if (!changes.isEmpty()) {
            String changesMsg = Calendrier.changeToString(getApplicationContext(), changes);

            //TODO string: message de notif
            //TODO clear notif on click
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("changes", changesMsg);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notif notif = new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                    getString(R.string.event), "changes : all the changes", R.drawable.ic_edit, pendingIntent);
            notif.show();
        }
    }


    @Override
    public void onDestroy() {
        Log.d("Background", "destroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
