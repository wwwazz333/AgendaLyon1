package com.iutcalendar.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatDelegate;
import com.iutcalendar.data.DataGlobal;
import com.univlyon1.tools.agenda.R;

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


    /**
     * besoin de savoir si on change ou non sinon bug pour la main page
     *
     * @param context le context
     * @return si le theme à besoin de changer
     */
    public static boolean adapteTheme(Context context) {
        String theme = DataGlobal.getTheme(context);
        int darkMode;
        boolean hasToChange;
        if (theme.equals("black")) {
            darkMode = AppCompatDelegate.MODE_NIGHT_YES;
            hasToChange = !isDarkMode((Activity) context);
        } else if (theme.equals("light")) {
            darkMode = AppCompatDelegate.MODE_NIGHT_NO;
            hasToChange = isDarkMode((Activity) context);
        } else {
            darkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            hasToChange = false;
        }
        if (hasToChange) AppCompatDelegate.setDefaultNightMode(darkMode);
        return hasToChange;
    }

    /**
     * manière simplifiée de changer le theme
     *
     * @param theme le nouveau theme
     */
    public static void adapteTheme(String theme) {
        int darkMode;
        if (theme.equals("black")) {
            darkMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if (theme.equals("light")) {
            darkMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            darkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(darkMode);
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

    public static void startDisplayOverOtherApp(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static boolean isDarkMode(Activity activity) {
        return (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
