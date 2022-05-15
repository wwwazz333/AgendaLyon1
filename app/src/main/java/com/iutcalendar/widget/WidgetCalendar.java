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
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.language.SettingsApp;
import com.iutcalendar.task.PersonnalCalendrier;

import java.util.List;


public class WidgetCalendar extends AppWidgetProvider {
    public static final String SHOW_TOAST_ACTION = "showing_toast";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Load some Data needed
        if (DataGlobal.getSavedBoolean(context, "summer_offset")) {
            DateCalendrier.setSummerOffset(1);
        } else {
            DateCalendrier.setSummerOffset(0);
        }
        PersonnalCalendrier.getInstance().load(context);
        SettingsApp.setLocale(context.getResources(), DataGlobal.getLanguage(context));

        //update Widget
        ComponentName thisWidget = new ComponentName(context, WidgetCalendar.class);
        Log.d("Widget", "start updated");
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calendar);


            CurrentDate currentDate = new CurrentDate();
            Calendrier cal = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)));

            boolean isFirst = true;
            if (cal.getLastEvent() != null) {

                List<EventCalendrier> eventToday = cal.getEventsOfDay(currentDate);
                for (EventCalendrier e : eventToday) {
                    if (e.getDate().getHour() >= currentDate.getHour()) {
                        updateBothEvent(views, isFirst, e);
                        if (!isFirst) {
                            break;
                        }
                        isFirst = false;
                    }
                }
                DateCalendrier dernierDateCal = cal.getLastEvent().getDate();
                while (isFirst && currentDate.compareTo(dernierDateCal) <= 0) {
                    currentDate = currentDate.addDay(1);
                    eventToday = cal.getEventsOfDay(currentDate);

                    for (EventCalendrier e : eventToday) {
                        updateBothEvent(views, isFirst, e);
                        if (!isFirst) {
                            break;
                        }
                        isFirst = false;
                    }
                }
            }

            if (isFirst) {//pas trouvé d'event correspondant
                views.setTextViewText(R.id.summary1, "Aucun evenement");
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
            intentActvity.putExtra("day_to_launche", currentDate.getTimeInMillis());
            views.setOnClickPendingIntent(R.id.openBtn,
                    PendingIntent.getActivity(context, 0, intentActvity, PendingIntent.FLAG_MUTABLE));


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

    private void updateBothEvent(RemoteViews views, boolean first, EventCalendrier event) {
        int numberTask = PersonnalCalendrier.getInstance().getLinkedTask(event).size();
        if (first) {
            views.setTextViewText(R.id.debut1, event.getDate().timeToString());
            views.setTextViewText(R.id.fin1, event.getDate().addTime(event.getDuree()).timeToString());
            views.setTextViewText(R.id.summary1, event.getSummary());
            views.setTextViewText(R.id.salle1, event.getSalle());


            setNumTask(views, R.id.countTask1, numberTask);

        } else {
            views.setTextViewText(R.id.debut2, event.getDate().timeToString());
            views.setTextViewText(R.id.fin2, event.getDate().addTime(event.getDuree()).timeToString());
            views.setTextViewText(R.id.summary2, event.getSummary());
            views.setTextViewText(R.id.salle2, event.getSalle());

            setNumTask(views, R.id.countTask2, numberTask);
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