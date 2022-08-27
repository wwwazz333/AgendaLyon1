package com.iutcalendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.notification.Notif
import com.iutcalendar.notification.NotificationChannels
import com.iutcalendar.settings.SettingsApp
import com.iutcalendar.task.PersonalCalendrier
import com.univlyon1.tools.agenda.R
import java.util.*

class WidgetCalendar : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        //Load some Data needed
        PersonalCalendrier.getInstance(context)
        SettingsApp.setLocale(context.resources, DataGlobal.getLanguage(context))

        //update Widget
        val thisWidget = ComponentName(context, WidgetCalendar::class.java)
        Log.d("Widget", "start updated")
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (widgetId in allWidgetIds) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, SettingsApp.getLayoutResWidget(context))
            var currentDate = CurrentDate()
            currentDate.add(GregorianCalendar.MINUTE, DELAY_AFTER_EVENT_PASSED) //pour que l'event s'affiche tjrs au bout de 30min
            val cal = Calendrier(FileGlobal.readFile(FileGlobal.getFileCalendar(context)))
            val events = cal.getNext2EventAfter(currentDate)
            if (events.isNotEmpty() && events[0] != null && events[0]!!.date != null) {
                currentDate = CurrentDate(events[0]!!.date)
                if (events.size == 2 && events[1] != null && events[0]!!.date!!.sameDay(events[1]!!.date)) {
                    updateBothEvent(context, views, events[0], events[1])
                } else {
                    updateBothEvent(context, views, events[0], null)
                }
            } else {
                updateBothEvent(context, views, null, null)
                views.setTextViewText(R.id.summary1, context.getString(R.string.No_events))
            }

            //Label date
            views.setTextViewText(R.id.dateLabel, currentDate.getRelativeDayName(context))

            //open MainActivity via Btn
            views.setTextViewText(R.id.openBtn, context.getString(R.string.open))
            val intentActivity = Intent(context, PageEventActivity::class.java)
            intentActivity.putExtra("launch_next_event", true)
            views.setOnClickPendingIntent(
                R.id.openBtn,
                PendingIntent.getActivity(context, 0, intentActivity, PendingIntent.FLAG_IMMUTABLE)
            )
            val intent = Intent(context, WidgetCalendar::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            intent.putExtra(SHOW_TOAST_ACTION, "showToast")
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.listLayout, pendingIntent)
            appWidgetManager.updateAppWidget(widgetId, views)
            if (DataGlobal.isDebug(context)) {
                Notif(
                    context, NotificationChannels.CHANGE_EVENT_NOTIFICATION_ID,
                    "widget", "update widget", R.drawable.ic_update, null
                ).show()
            }
        }
        Log.d("Widget", "updated")
    }

    private fun updateBothEvent(context: Context, views: RemoteViews, event1: EventCalendrier?, event2: EventCalendrier?) {
        if (event1?.date != null) {
            views.setTextViewText(R.id.debut1, event1.date!!.timeToString())
            views.setTextViewText(R.id.fin1, event1.date!!.addTime(event1.dure).timeToString())
            views.setTextViewText(R.id.summary1, event1.nameEvent)
            views.setTextViewText(R.id.salle1, event1.salle.replace("\\,", " "))
            val numberTask: Int = PersonalCalendrier.getInstance(context)!!.getCountTaskOf(event1)
            setNumTask(views, R.id.countTask1, numberTask)
        } else {
            views.setTextViewText(R.id.debut1, "")
            views.setTextViewText(R.id.fin1, "")
            views.setTextViewText(R.id.summary1, "")
            views.setTextViewText(R.id.salle1, "")
            setNumTask(views, R.id.countTask1, 0)
        }
        if (event2?.date != null) {
            views.setTextViewText(R.id.debut2, event2.date!!.timeToString())
            views.setTextViewText(R.id.fin2, event2.date!!.addTime(event2.dure).timeToString())
            views.setTextViewText(R.id.summary2, event2.nameEvent)
            views.setTextViewText(R.id.salle2, event2.salle.replace("\\,", " "))
            val numberTask: Int = PersonalCalendrier.getInstance(context)!!.getCountTaskOf(event2)
            setNumTask(views, R.id.countTask2, numberTask)
        } else {
            views.setTextViewText(R.id.debut2, "")
            views.setTextViewText(R.id.fin2, "")
            views.setTextViewText(R.id.summary2, "")
            views.setTextViewText(R.id.salle2, "")
            setNumTask(views, R.id.countTask2, 0)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.getStringExtra(SHOW_TOAST_ACTION) != null) {
            Log.d("Widget", "Toast")
            Toast.makeText(context, "Widget updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setNumTask(views: RemoteViews, id: Int, numberTask: Int) {
        if (numberTask == 0) {
            views.setViewVisibility(id, View.INVISIBLE)
        } else {
            views.setViewVisibility(id, View.VISIBLE)
            views.setTextViewText(id, numberTask.toString())
        }
    }

    companion object {
        const val SHOW_TOAST_ACTION = "showing_toast"
        const val DELAY_AFTER_EVENT_PASSED = -30
    }
}