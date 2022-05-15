package com.iutcalendar.language;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LanguageApp {
    public static Configuration setLocale(Resources resources, String language) {
        //Initialize metrics
        DisplayMetrics metrics = resources.getDisplayMetrics();
        //Initialize configuration
        Configuration configuration = resources.getConfiguration();
        Log.d("Language", configuration.getLocales().toString());
        //Initialize locale
        configuration.locale = new Locale(language);
        //Update configuration
        resources.updateConfiguration(configuration, metrics);
        //Notify configuration
        return configuration;
    }

    public static Locale getLocale(Resources resources) {
        //Initialize metrics
        DisplayMetrics metrics = resources.getDisplayMetrics();
        //Initialize configuration
        Configuration configuration = resources.getConfiguration();
        //Notify configuration
        return configuration.locale;
    }
}
