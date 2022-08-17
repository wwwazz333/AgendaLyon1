package com.iutcalendar.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import com.iutcalendar.data.DataGlobal
import com.univlyon1.tools.agenda.R
import java.util.*

object SettingsApp {
    var locale = Locale("FR", "FRANCE")
        private set

    fun setLocale(resources: Resources, language: String?) {
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(Locale(language))
        locale = Locale(language)
        //Update configuration
        Log.d("Language", configuration.locales.toString())
        resources.updateConfiguration(configuration, metrics)
    }

    /**
     * besoin de savoir si on change ou non sinon bug pour la main page
     *
     * @param context le context
     * @return si le theme à besoin de changer
     */
    fun adapteTheme(context: Context): Boolean {
        val theme = DataGlobal.getTheme(context)
        val darkMode: Int
        val hasToChange: Boolean
        if (theme == "black") {
            darkMode = AppCompatDelegate.MODE_NIGHT_YES
            hasToChange = !isDarkMode(context as Activity)
        } else if (theme == "light") {
            darkMode = AppCompatDelegate.MODE_NIGHT_NO
            hasToChange = isDarkMode(context as Activity)
        } else {
            darkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            hasToChange = false
        }
        if (hasToChange) AppCompatDelegate.setDefaultNightMode(darkMode)
        return hasToChange
    }

    /**
     * manière simplifiée de changer le theme
     *
     * @param theme le nouveau theme
     */
    fun adapteTheme(theme: String) {
        val darkMode: Int
        darkMode = if (theme == "black") {
            AppCompatDelegate.MODE_NIGHT_YES
        } else if (theme == "light") {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

    @LayoutRes
    fun getLayoutResWidget(context: Context?): Int {
        val t = DataGlobal.getThemeResWidget(context)
        Log.d("Widget", "Theme : $t")
        return if (t == "black") {
            R.layout.widget_calendar_black
        } else if (t == "light") {
            R.layout.widget_calendar_light
        } else {
            R.layout.widget_calendar_grey
        }
    }

    fun startDisplayOverOtherApp(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun isDarkMode(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}