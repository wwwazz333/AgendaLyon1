package com.iutcalendar.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetCalendar extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, WidgetCalendar.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_calendar);


            CurrentDate currentDate = new CurrentDate();
            Calendrier cal = new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)));

            List<EventCalendrier> eventToday = cal.getEventsOfDay(currentDate);

            boolean isFirst = true;

            for (EventCalendrier e : eventToday) {
                if (e.getDate().getHour() >= currentDate.getHour()) {
                    if (isFirst) {
                        views.setTextViewText(R.id.debut1, e.getDate().timeToString());
                        views.setTextViewText(R.id.fin1, e.getDate().addTime(e.getDuree()).timeToString());
                        views.setTextViewText(R.id.summary1, e.getSummary());
                        views.setTextViewText(R.id.salle1, e.getSalle());
                    } else {
                        views.setTextViewText(R.id.debut2, e.getDate().timeToString());
                        views.setTextViewText(R.id.fin2, e.getDate().addTime(e.getDuree()).timeToString());
                        views.setTextViewText(R.id.summary2, e.getSummary());
                        views.setTextViewText(R.id.salle2, e.getSalle());

                        break;
                    }
                    isFirst = false;
                }
            }
            if (isFirst) {//pas trouvé d'event correspondant
                views.setTextViewText(R.id.summary1, "Aucun evenement");
            }

            Intent intent = new Intent(context, WidgetCalendar.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.listLayout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);
        }

        Toast.makeText(context, "Widget updated", Toast.LENGTH_SHORT).show();
    }
}