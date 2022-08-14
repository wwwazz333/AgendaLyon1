package com.iutcalendar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import androidx.work.Configuration;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.notification.Notif;
import com.univlyon1.tools.agenda.R;

public class UpdateBackgroundJobServices extends JobService {
    private static final String TAG = "UpdateBackgroundJobServices";
    private static final long INTERVAL_UPDATE = 20 * 60_000;
    public static int jobID = 5453;

    public UpdateBackgroundJobServices() {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(0, 10000);
    }

    public static void startScheduleJobBackground(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler.getPendingJob(jobID) != null) {
            Log.d(TAG, "planification des updates");


            ComponentName componentName = new ComponentName(context, UpdateBackgroundJobServices.class);

            JobInfo info = new JobInfo.Builder(jobID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(INTERVAL_UPDATE)
                    .build();


            int resCode = scheduler.schedule(info);
            if (resCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "startScheduleJobBackground: success");
            } else {
                Log.d(TAG, "startScheduleJobBackground: failed");
            }
        } else {
            Log.d(TAG, "planification des updates déjà faites");
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        doUpdate(params);
        return true;
    }

    private void doUpdate(JobParameters params) {
        Log.d(TAG, "start Service at " + new CurrentDate().timeToString());


        new Thread(() -> {
            try {
                long timerCount = System.currentTimeMillis();

                updateFile(getApplicationContext());

                if (DataGlobal.isDebug(getApplicationContext())) {//Debugging
                    String txt = "";
                    txt += "background process : " + (System.currentTimeMillis() - timerCount) / 1000 + "s";
                    new Notif(this, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT,
                            "Tps background", txt, R.drawable.ic_update, null).show();
                }
            } catch (Exception ignored) {
            }

            jobFinished(params, false);
            Log.d(TAG, "end Service");
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: job canceled");
        return false;
    }


    private void updateFile(Context context) {
        //TODO string: message de notif FR & AN
        FileGlobal.updateAndGetChange(context, null, ((context1, intent) -> {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            Notif notif = new Notif(context, Notif.CHANGE_EVENT_NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT, context.getResources().getString(R.string.event), context.getString(R.string.change_in_schedul), R.drawable.ic_edit, pendingIntent);
            notif.show();
        }));
    }


}
