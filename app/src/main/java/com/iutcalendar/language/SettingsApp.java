package com.iutcalendar.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.DataGlobal;

import java.util.Locale;

public class SettingsApp {
    public static Configuration setLocale(Resources resources, String language) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        //Update configuration
        resources.updateConfiguration(configuration, metrics);
        return configuration;
    }

    public static Locale getLocale(Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        return configuration.locale;
    }

    /*public static void adapteTheme(Context context) {

        if (DataGlobal.getTheme(context).equals("default")) {
            Log.d("Theme", "default");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        context.setTheme(R.style.Theme_IUTCalendarNight);
                        Log.d("Theme", "black");
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        context.setTheme(R.style.Theme_IUTCalendar);
                        Log.d("Theme", "white");
                        break;
                }

            }
        } else {
            Log.d("Theme", "not default");
            String t = DataGlobal.getTheme(context);
            Log.d("Theme", t);
            if (t.equals("black")) {
                context.setTheme(R.style.Theme_IUTCalendarNight);
            } else if (t.equals("light")) {
                context.setTheme(R.style.Theme_IUTCalendar);
            }
        }
    }*/
}
