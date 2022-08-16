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
    public static final String workName = "UpdateWorkBackground";
    private static final String TAG = "WorkUpdate";

    public WorkUpdate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void startBackgroundWork(Context ctx) {
        WorkManager workManager = WorkManager.getInstance(ctx);
        workManager.cancelUniqueWork(WorkUpdate.workName);
        if (DataGlobal.getSavedBoolean(ctx, DataGlobal.NOTIFICATION_ENABLED)) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkUpdate.class, 1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setInitialDelay(1, TimeUnit.HOURS)
                    .build();
            workManager.enqueueUniquePeriodicWork(WorkUpdate.workName, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
            Log.d(TAG, "startBackgroundWork: periodic work enqueued");
        } else {
            workManager.cancelUniqueWork(WorkUpdate.workName);
            Log.d(TAG, "startBackgroundWork: periodic work canceled");
        }

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

            if (DataGlobal.getSavedBoolean(context, DataGlobal.NOTIFICATION_ENABLED))
                new Notif(context, NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID, context.getResources().getString(R.string.event), context.getString(R.string.change_in_schedul), R.drawable.ic_edit, pendingIntent).show();
        }));
    }
}
