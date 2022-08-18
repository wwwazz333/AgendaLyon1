package com.iutcalendar.task

import android.content.Context
import com.iutcalendar.alarm.Alarm
import java.io.Serializable
import java.util.*

class Task constructor(
    val txt: String, val linkedToUID: String?
) : Serializable {

    fun destroy(context: Context?) {
        Alarm.cancelAlarm(context, linkedToUID)
    }

    override fun toString(): String {
        return txt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val task = other as Task
        return txt == task.txt
    }

    override fun hashCode(): Int {
        return Objects.hash(txt)
    }
}