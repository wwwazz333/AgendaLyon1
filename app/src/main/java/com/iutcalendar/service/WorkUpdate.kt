package com.iutcalendar.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.notification.Notif
import com.iutcalendar.notification.NotificationChannels
import com.univlyon1.tools.agenda.R
import java.util.concurrent.TimeUnit

class WorkUpdate(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(TAG, "start Service at " + CurrentDate().timeToString())
        Thread {
            try {
                val timerCount = System.currentTimeMillis()
                updateFile(applicationContext)
                if (DataGlobal.isDebug(applicationContext)) { //Debugging
                    var txt = ""
                    txt += "background process : " + (System.currentTimeMillis() - timerCount) / 1000 + "s"
                    Notif(
                        applicationContext, NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID,
                        "Tps background", txt, R.drawable.ic_update, null
                    ).show()
                }
            } catch (ignored: Exception) {
            }
            Log.d(TAG, "end Service")
        }.start()
        return Result.success()
    }

    private fun updateFile(cxt: Context) {
        //TODO string: message de notif FR & AN
        FileGlobal.updateAndGetChange(cxt, null) { context: Context?, intent: Intent? ->
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            if (DataGlobal.getSavedBoolean(context, DataGlobal.NOTIFICATION_ENABLED)) {
                Notif(
                    cxt, NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID, cxt.resources.getString(R.string.event), cxt.getString(R.string.change_in_schedul),
                    R.drawable.ic_edit, pendingIntent
                )
                    .show()
            }

        }
    }

    companion object {
        private const val workName = "UpdateWorkBackground"
        private const val TAG = "WorkUpdate"
        fun startBackgroundWork(ctx: Context?) {
            val workManager = WorkManager.getInstance(ctx!!)
            workManager.cancelUniqueWork(workName)
            if (DataGlobal.getSavedBoolean(ctx, DataGlobal.NOTIFICATION_ENABLED)) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val periodicWorkRequest = PeriodicWorkRequest.Builder(WorkUpdate::class.java, 1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setInitialDelay(1, TimeUnit.HOURS)
                    .build()
                workManager.enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest)
                Log.d(TAG, "startBackgroundWork: periodic work enqueued")
            } else {
                workManager.cancelUniqueWork(workName)
                Log.d(TAG, "startBackgroundWork: periodic work canceled")
            }
        }
    }
}