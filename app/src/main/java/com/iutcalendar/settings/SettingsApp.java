package com.iutcalendar.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatDelegate;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.DataGlobal;

import java.util.Locale;

public class SettingsApp {

    private static Locale currLocale = new Locale("FR", "FRANCE");

    public static void setLocale(Resources resources, String language) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language));
        currLocale = new Locale(language);
        //Update configuration
        Log.d("Language", configuration.getLocales().toString());
        resources.updateConfiguration(configuration, metrics);
    }

    public static Locale getLocale() {
        return currLocale;
    }


    public static void adapteTheme(Context context) {
        String t = DataGlobal.getTheme(context);
        adapteTheme(t);
    }

    public static void adapteTheme(String theme) {
        Log.d("Theme", theme);
        if (theme.equals("black")) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else if (theme.equals("light")) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static @LayoutRes int getLayoutResWidget(Context context) {
        String t = DataGlobal.getThemeResWidget(context);
        Log.d("Widget", "Theme : " + t);
        if (t.equals("black")) {
            return R.layout.widget_calendar_black;
        } else if (t.equals("light")) {
            return R.layout.widget_calendar_light;
        } else {
            return R.layout.widget_calendar_grey;
        }
    }


    public static @ColorInt int getColor(int id, Activity activity) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(id, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    public static void startDisplayOverOtherApp(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
