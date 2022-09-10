package com.iutcalendar.data

import android.content.Context
import android.graphics.Color
import com.iutcalendar.settings.SettingsApp
import java.io.Serializable

object ColorEvent : Serializable {
    private var count = 0

    @Transient
    private val defaultDarkColor = Color.parseColor("#1C1E20")

    @Transient
    private val defaultLightColor = Color.parseColor("#EDEDED")

//34404c
    @Transient
    private val darkColors: List<Int> = listOf(
        Color.parseColor("#632727"),
        Color.parseColor("#636327"),
        Color.parseColor("#127C8A"),
        Color.parseColor("#272763"),

        Color.parseColor("#635027"),
        Color.parseColor("#3a6327"),
        Color.parseColor("#273a63"),
        Color.parseColor("#502763"),

        Color.parseColor("#462763"),
        Color.parseColor("#632744"),


        Color.parseColor("#352763"),
        Color.parseColor("#632755"),
        Color.parseColor("#556327"),
        Color.parseColor("#276335"),

        Color.parseColor("#63273c"),
        Color.parseColor("#634e27"),
        Color.parseColor("#34404c"),
        Color.parseColor("#273c63")
    )


    @Transient
    private val lightColors: List<Int> = listOf(
        Color.parseColor("#cc4f4f"),
        Color.parseColor("#cccc4f"),
        Color.parseColor("#4fcccc"),

        Color.parseColor("#cca44f"),
        Color.parseColor("#76cc4f"),
        Color.parseColor("#4f76cc"),
        Color.parseColor("#a44fcc"),

        Color.parseColor("#8bcc4f"),
        Color.parseColor("#8f4fcc"),
        Color.parseColor("#cc4f8b"),


        Color.parseColor("#6c4fcc"),
        Color.parseColor("#cc4fae"),
        Color.parseColor("#aecc4f"),
        Color.parseColor("#4fcc6c"),

        Color.parseColor("#cc4f7b"),
        Color.parseColor("#cca04f"),
        Color.parseColor("#4fcca0"),
        Color.parseColor("#4f7bcc")
    )

    private var colors: HashMap<String, Int> = hashMapOf()
    fun clear(ctx: Context) {
        colors.clear()
        count = 0
        save(ctx)

    }

    fun generateLightColorsForCalendar(ctx: Context) {
        CachedData.calendrier.events.forEach { ev ->
            colors[ev.nameEvent] = lightColors[count++ % lightColors.size]
        }
        save(ctx)
    }

    fun generateDarkColorsForCalendar(ctx: Context) {
        CachedData.calendrier.events.forEach { ev ->
            colors[ev.nameEvent] = darkColors[count++ % darkColors.size]
        }
        save(ctx)
    }

    fun generateDarkDefaultColorsForCalendar(ctx: Context) {
        CachedData.calendrier.events.forEach { ev ->
            colors[ev.nameEvent] = defaultDarkColor
        }
        save(ctx)
    }

    fun generateLightDefaultColorsForCalendar(ctx: Context) {
        CachedData.calendrier.events.forEach { ev ->
            colors[ev.nameEvent] = defaultLightColor
        }
        save(ctx)
    }

    //    darkColors[count++ % darkColors.size]
    fun getOrCreate(ctx: Context, name: String): Int {
        return if (SettingsApp.isDarkMode(ctx)) {
            if (!colors.containsKey(name)) {
                colors[name] = defaultDarkColor
                save(ctx)
            }
            colors[name]!!
        } else {
            if (!colors.containsKey(name)) {
                colors[name] = defaultLightColor
                save(ctx)
            }
            colors[name]!!
        }
    }

    fun save(ctx: Context, nameEvent: String, value: Int) {
        colors[nameEvent] = value
        save(ctx)
    }

    fun load(context: Context?) {
        colors = java.util.HashMap()
        (FileGlobal.loadBinaryFile(FileGlobal.getFilePersonalColorEvent(context)) as HashMap<String, Int>?)?.let {
            colors = it
        }
    }

    fun save(context: Context?) {
        FileGlobal.writeBinaryFile(colors, FileGlobal.getFilePersonalColorEvent(context))
    }
}