package com.iutcalendar.data;

import android.content.Context;
import android.util.ArraySet;
import androidx.preference.PreferenceManager;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DataGlobal {

    public final static int[] DAYS_OF_WEEK = new int[]{GregorianCalendar.MONDAY, GregorianCalendar.TUESDAY, GregorianCalendar.WEDNESDAY,
            GregorianCalendar.THURSDAY, GregorianCalendar.FRIDAY, GregorianCalendar.SATURDAY, GregorianCalendar.SUNDAY};
    public final static String URL = "url_path";
    public final static String LANGUAGUE = "language_selected";
    public final static String THEME = "theme_selected";
    public final static String THEME_RES_WIDGET = "theme_widget_selected";
    public final static String ALARM_ENABELED = "alarme_enable";
    public final static String FERIER_DAY_ENABELED = "jour_ferier_enabled";
    public final static String COMPLEX_ALARM_SETTINGS = "complex_alarm_settings";
    public final static String TIME_BEFORE_RING = "time_before_ring";

    public final static String ACTIVATED_DAYS = "activated_days";
    public final static String NOTIFICATION_ENABLED = "notification_enabled";
    public final static String NOMBRE_CHANGE_TO_DISPLAY = "nombre_change_to_display";


    public final static String DEBUGING = "debuging";

    public static String getTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(THEME, "default");
    }

    public static String getThemeResWidget(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(THEME_RES_WIDGET, "grey");
    }


    public static String getLanguage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGUE, "fr");
    }

    //String
    public static void save(Context context, String key, String data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, data).commit();
    }

    public static String getSavedString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    //boolean
    public static void save(Context context, String key, boolean data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, data).commit();
    }

    public static boolean getSavedBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static void setDebugings(Context context, boolean data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(DEBUGING, data).commit();
    }

    public static boolean isDebug(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DEBUGING, false);
    }

    //boolean
    public static void save(Context context, String key, int data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, data).commit();
    }

    public static int getSavedInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }


    public static void savePath(Context context, String data) {
        save(context, URL, data);
    }

    public static String getSavedPath(Context context) {
        return getSavedString(context, URL);
    }


    public static List<Integer> getActivatedDays(Context context) {
        Set<String> acitvatedSet = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(ACTIVATED_DAYS, new ArraySet<>());
        List<Integer> activatedDays = new LinkedList<>();
        for (String day : acitvatedSet) {
            int dayToAdd = -1;
            switch (day) {
                case "monday":
                    dayToAdd = GregorianCalendar.MONDAY;
                    break;
                case "tuesday":
                    dayToAdd = GregorianCalendar.TUESDAY;
                    break;
                case "wednesday":
                    dayToAdd = GregorianCalendar.WEDNESDAY;
                    break;
                case "thursday":
                    dayToAdd = GregorianCalendar.THURSDAY;
                    break;
                case "friday":
                    dayToAdd = GregorianCalendar.FRIDAY;
                    break;
                case "saturday":
                    dayToAdd = GregorianCalendar.SATURDAY;
                    break;
                case "sunday":
                    dayToAdd = GregorianCalendar.SUNDAY;
                    break;
            }
            activatedDays.add(dayToAdd);

        }
        return activatedDays;

    }
}
