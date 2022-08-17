package com.iutcalendar.task

import android.content.Context
import com.iutcalendar.alarm.Alarm
import java.io.Serializable
import java.util.*

class Task @JvmOverloads constructor(//TODO implémenté un rappel
        val txt: String, val linkedToUID: String?, val isAlarm: Boolean = false) : Serializable {
    var isAlarmActivate = true

    fun destroy(context: Context?) {
        Alarm.Companion.cancelAlarm(context, linkedToUID)
    }

    override fun toString(): String {
        return txt
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val task = o as Task
        return txt == task.txt
    }

    override fun hashCode(): Int {
        return Objects.hash(txt)
    }
}