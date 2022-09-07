package com.iutcalendar.data

import android.content.Context
import android.graphics.Color
import java.io.Serializable

object ColorEvent : Serializable {
    var isDark = false
    private var count = 0


    @Transient
    private val darkColors: List<Int> = listOf(
        Color.parseColor("#211F11"),
        Color.parseColor("#1C1E20"),
        Color.parseColor("#1A0A1C"),
        Color.parseColor("#1C1133"),
        Color.parseColor("#13171D"),
        Color.parseColor("#192610"),
        Color.parseColor("#193241"),
        Color.parseColor("#31013A"),
        Color.parseColor("#380E0D"),
        Color.parseColor("#071928"),

        Color.parseColor("#153243"),
        Color.parseColor("#1D263B"),
        Color.parseColor("#0D1821"),
        Color.parseColor("#071013"),
        Color.parseColor("#3E92CC"),
        Color.parseColor("#235170"),
        Color.parseColor("#E85D75"),
        Color.parseColor("#783744"),
        Color.parseColor("#62C370"),
        Color.parseColor("#356A42")
    )


    @Transient
    private val lightColors: List<Int> = listOf(
        Color.parseColor("#989899"),
        Color.parseColor("#E7E7E7"),
        Color.parseColor("#D1AF87"),
        Color.parseColor("#CEC5B7"),
        Color.parseColor("#CBDAE6"),
        Color.parseColor("#AFCDE4"),
        Color.parseColor("#75A9D1"),

        Color.parseColor("#FFFBF2"),
        Color.parseColor("#D9D4D1"),
        Color.parseColor("#B2B0BE"),
        Color.parseColor("#325b6d"),
        Color.parseColor("#3B5B81"),
        Color.parseColor("#566487"),
        Color.parseColor("#4A87A4"),
        Color.parseColor("#6EA0B9"),
        Color.parseColor("#5D99B3"),
        Color.parseColor("#B4C3D8")
    )

    private var colors: HashMap<String, Int> = hashMapOf()
    fun clear(ctx: Context) {
        colors.clear()
        count = 0
        save(ctx)

    }

    fun getOrCreate(ctx: Context, name: String): Int {
        return if (isDark) {
            if (!colors.containsKey(name)) {
                colors[name] = darkColors[count++ % darkColors.size]
                save(ctx)
            }
            colors[name]!!
        } else {
            if (!colors.containsKey(name)) {
                colors[name] = lightColors[count++ % lightColors.size]
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