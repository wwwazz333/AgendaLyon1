package com.iutcalendar.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.iutcalendar.data.DataGlobal
import com.univlyon1.tools.agenda.R
import java.util.*


object SettingsApp {
    val localFrance = Locale("fr")
    var locale = localFrance
        private set

    fun updateLocale(ctx: Context) {
        DataGlobal.getLanguage(ctx)?.let { tag ->
            locale = Locale(tag)
        }
    }

    fun updateLanguage(ctx: Context) {
        DataGlobal.getLanguage(ctx)?.let { tag ->
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
            updateLocale(ctx)
        }

    }


    /**
     * Besoin de savoir si on change ou non sinon bug pour la main page
     *
     * @param ctx le context
     * @return si le theme avais besoin de changer
     */
    fun adapteTheme(ctx: Context): Boolean {
        val theme = DataGlobal.getTheme(ctx)
        val darkMode: Int
        val hasToChange: Boolean
        when (theme) {
            "black" -> {
                darkMode = AppCompatDelegate.MODE_NIGHT_YES
                hasToChange = !isDarkMode(ctx)
            }
            "light" -> {
                darkMode = AppCompatDelegate.MODE_NIGHT_NO
                hasToChange = isDarkMode(ctx)
            }
            else -> {
                darkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                hasToChange = false
            }
        }
        if (hasToChange) AppCompatDelegate.setDefaultNightMode(darkMode)
        return hasToChange
    }

    /**
     * Manière simplifiée de changer le theme
     *
     * @param theme le nouveau theme
     */
    fun adapteTheme(theme: String) {
        val darkMode: Int = when (theme) {
            "black" -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            "light" -> {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            else -> {
                //FIXME petit bug de couleur qd revien sur main activity
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

    @LayoutRes
    fun getLayoutResWidget(context: Context): Int {
        val t = DataGlobal.getThemeResWidget(context)
        Log.d("Widget", "Theme : $t")
        return when (t) {
            "black" -> {
                R.layout.widget_calendar_black
            }
            "light" -> {
                R.layout.widget_calendar_light
            }
            else -> {
                R.layout.widget_calendar_grey
            }
        }
    }

    fun startDisplayOverOtherApp(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }


    fun isDarkMode(ctx: Context): Boolean {
        return ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    }
}