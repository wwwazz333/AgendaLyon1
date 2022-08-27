package com.iutcalendar.data

import android.content.Context
import android.util.ArraySet
import androidx.preference.PreferenceManager
import java.util.*

object DataGlobal {
    val DAYS_OF_WEEK = intArrayOf(
        GregorianCalendar.MONDAY, GregorianCalendar.TUESDAY, GregorianCalendar.WEDNESDAY,
        GregorianCalendar.THURSDAY, GregorianCalendar.FRIDAY, GregorianCalendar.SATURDAY, GregorianCalendar.SUNDAY
    )
    const val URL = "url_path"
    const val URL_ROOMS = "url_rooms_path"
    const val LANGUAGE = "language_selected"
    const val THEME = "theme_selected"
    const val THEME_RES_WIDGET = "theme_widget_selected"
    const val ALARM_ENABLED = "alarme_enable"
    const val FERIER_DAY_ENABLED = "jour_ferier_enabled"
    const val COMPLEX_ALARM_SETTINGS = "complex_alarm_settings"
    const val TIME_BEFORE_RING = "time_before_ring"
    const val ACTIVATED_DAYS = "activated_days"
    const val NOTIFICATION_ENABLED = "notification_enabled"
    const val NOMBRE_CHANGE_TO_DISPLAY = "nombre_change_to_display"
    const val DEBUGGING = "debugging"
    fun getTheme(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getString(THEME, "default")
    }

    fun getThemeResWidget(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getString(THEME_RES_WIDGET, "grey")
    }

    fun getLanguage(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getString(LANGUAGE, "fr")
    }

    //String
    fun save(context: Context?, key: String?, data: String?) {
        PreferenceManager.getDefaultSharedPreferences(context!!).edit().putString(key, data).commit()
    }

    fun getSavedString(context: Context?, key: String?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getString(key, "")
    }

    //boolean
    fun save(context: Context?, key: String?, data: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context!!).edit().putBoolean(key, data).commit()
    }

    fun getSavedBoolean(context: Context?, key: String?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getBoolean(key, false)
    }

    fun setDebugging(context: Context?, data: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context!!).edit().putBoolean(DEBUGGING, data).commit()
    }

    fun isDebug(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getBoolean(DEBUGGING, false)
    }

    //boolean
    fun save(context: Context?, key: String?, data: Int) {
        PreferenceManager.getDefaultSharedPreferences(context!!).edit().putInt(key, data).commit()
    }

    fun getSavedInt(context: Context?, key: String?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context!!).getInt(key, 0)
    }

    fun savePath(context: Context?, data: String?) {
        save(context, URL, data)
    }

    fun getSavedPath(context: Context?): String? {
        return getSavedString(context, URL)
    }

    fun saveRoomsPath(context: Context?, data: String?) {
        save(context, URL_ROOMS, data)
    }

    fun getSavedRoomsPath(context: Context?): String? {
        return getSavedString(context, URL_ROOMS)
    }

    fun getActivatedDays(context: Context?): List<Int> {
        val activatedSet = PreferenceManager.getDefaultSharedPreferences(context!!).getStringSet(ACTIVATED_DAYS, ArraySet())
        val activatedDays: MutableList<Int> = LinkedList()
        for (day in activatedSet!!) {
            var dayToAdd = -1
            when (day) {
                "monday" -> dayToAdd = GregorianCalendar.MONDAY
                "tuesday" -> dayToAdd = GregorianCalendar.TUESDAY
                "wednesday" -> dayToAdd = GregorianCalendar.WEDNESDAY
                "thursday" -> dayToAdd = GregorianCalendar.THURSDAY
                "friday" -> dayToAdd = GregorianCalendar.FRIDAY
                "saturday" -> dayToAdd = GregorianCalendar.SATURDAY
                "sunday" -> dayToAdd = GregorianCalendar.SUNDAY
            }
            activatedDays.add(dayToAdd)
        }
        return activatedDays
    }
}