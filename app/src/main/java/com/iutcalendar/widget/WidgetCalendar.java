package com.iutcalendar.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.calendar.iutcalendar.R;
import com.iutcalendar.MainActivity;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.task.PersonnalCalendrier;

import java.util.GregorianCalendar;


public class WidgetCalendar extends AppWidgetProvider {
    public static final String SHOW_TOAST_ACTION = "showing_toast";
    public static final int DELAY_AFTER_EVENT_PASSED = -30;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //TODO set Background
        //Load some Data needed
        PersonnalCalendrier.getInstance().load(context);
        SettingsApp.setLocale(context.getResources(), DataGlobal.getLanguage(context));

        //update Widget
        ComponentName thisWidget = new ComponentName(context, WidgetCalendar.class);
        Log.d("Widget", "start updated");
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), SettingsApp.getLayoutResWidget(context));


            CurrentDate currentDate = new CurrentDate();
            currentDate.add(GregorianCalendar.MINUTE, WidgetCalendar.DELAY_AFTER_EVENT_PASSED);//pour que l'event s'affiche tjrs au bout de 30min


            Calendrier cal = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)));

            EventCalendrier[] events = cal.getNext2EventAfter(currentDate);
            if (events.length > 0 && events[0] != null) {
                currentDate = new CurrentDate(events[0].getDate());
                if (events.length == 2 && events[1] != null && events[0].getDate().sameDay(events[1].getDate())) {
                    updateBothEvent(views, events[0], events[1]);
                } else {
                    updateBothEvent(views, events[0], null);
                }
            } else {
                views.setTextViewText(R.id.summary1, context.getString(R.string.No_events));
            }

            //Label date
            if (currentDate.sameDay(new CurrentDate())) {
                views.setTextViewText(R.id.dateLabel, context.getString(R.string.today));
            } else if (currentDate.sameDay(new CurrentDate().addDay(1))) {
                views.setTextViewText(R.id.dateLabel, context.getString(R.string.tomorrow));
            } else {
                views.setTextViewText(R.id.dateLabel, currentDate.toString());
            }


            //open MainActivity via Btn
            Intent intentActvity = new Intent(context, MainActivity.class);
            intentActvity.putExtra("launche_next_event", true);
            views.setOnClickPendingIntent(R.id.openBtn,
                    PendingIntent.getActivity(context, 0, intentActvity, PendingIntent.FLAG_IMMUTABLE));


            Intent intent = new Intent(context, WidgetCalendar.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.putExtra(SHOW_TOAST_ACTION, "showToast");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.listLayout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);


        }

        Log.d("Widget", "updated");
    }

    private void updateBothEvent(RemoteViews views, EventCalendrier event1, EventCalendrier event2) {

        if (event1 != null) {
            views.setTextViewText(R.id.debut1, event1.getDate().timeToString());
            views.setTextViewText(R.id.fin1, event1.getDate().addTime(event1.getDuree()).timeToString());
            views.setTextViewText(R.id.summary1, event1.getSummary());
            views.setTextViewText(R.id.salle1, event1.getSalle());

            int numberTask = PersonnalCalendrier.getInstance().getLinkedTask(event1).size();
            setNumTask(views, R.id.countTask1, numberTask);

        } else {
            views.setTextViewText(R.id.debut1, "");
            views.setTextViewText(R.id.fin1, "");
            views.setTextViewText(R.id.summary1, "");
            views.setTextViewText(R.id.salle1, "");
            setNumTask(views, R.id.countTask1, 0);
        }
        if (event2 != null) {
            views.setTextViewText(R.id.debut2, event2.getDate().timeToString());
            views.setTextViewText(R.id.fin2, event2.getDate().addTime(event2.getDuree()).timeToString());
            views.setTextViewText(R.id.summary2, event2.getSummary());
            views.setTextViewText(R.id.salle2, event2.getSalle());

            int numberTask = PersonnalCalendrier.getInstance().getLinkedTask(event2).size();
            setNumTask(views, R.id.countTask2, numberTask);
        } else {
            views.setTextViewText(R.id.debut2, "");
            views.setTextViewText(R.id.fin2, "");
            views.setTextViewText(R.id.summary2, "");
            views.setTextViewText(R.id.salle2, "");
            setNumTask(views, R.id.countTask2, 0);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getStringExtra(SHOW_TOAST_ACTION) != null) {
            Log.d("Widget", "Toast");
            Toast.makeText(context, "Widget updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNumTask(RemoteViews views, int id, int numberTask) {
        if (numberTask == 0) {
            views.setViewVisibility(id, View.INVISIBLE);
        } else {
            views.setViewVisibility(id, View.VISIBLE);
            views.setTextViewText(id, String.valueOf(numberTask));
        }
    }
}