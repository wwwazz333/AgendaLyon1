package com.iutcalendar.service;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.*;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.notification.Notif;
import com.iutcalendar.notification.NotificationChannels;
import com.univlyon1.tools.agenda.R;

import java.util.concurrent.TimeUnit;

public class WorkUpdate extends Worker {
    private static final String TAG = "WorkUpdate";

    public static final String workName = "UpdateWorkBackground";

    public static void startWork(Context ctx){
        WorkManager workManager = WorkManager.getInstance(ctx);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkUpdate.class, 20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        workManager.enqueueUniquePeriodicWork(WorkUpdate.workName, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    public WorkUpdate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d(TAG, "start Service at " + new CurrentDate().timeToString());


        new Thread(() -> {
            try {
                long timerCount = System.currentTimeMillis();

                updateFile(getApplicationContext());

                if (DataGlobal.isDebug(getApplicationContext())) {//Debugging
                    String txt = "";
                    txt += "background process : " + (System.currentTimeMillis() - timerCount) / 1000 + "s";
                    new Notif(getApplicationContext(), NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID,
                            "Tps background", txt, R.drawable.ic_update, null).show();
                }
            } catch (Exception ignored) {
            }

            Log.d(TAG, "end Service");
        }).start();

        return Result.success();
    }

    private void updateFile(Context context) {
        //TODO string: message de notif FR & AN
        FileGlobal.updateAndGetChange(context, null, ((context1, intent) -> {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            Notif notif = new Notif(context, NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID, context.getResources().getString(R.string.event), context.getString(R.string.change_in_schedul), R.drawable.ic_edit, pendingIntent);
            notif.show();
        }));
    }
}
