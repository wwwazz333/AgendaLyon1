package com.iutcalendar.alarm

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import com.iutcalendar.calendrier.CurrentDate
import java.io.Serializable

class AlarmRing(val timeInMillis: Long, var isActivate: Boolean) : Serializable, Comparable<AlarmRing> {

    private fun createAlarmId(): String {
        return timeInMillis.toString()
    }

    fun setAlarm(context: Context?) {
        if (timeInMillis >= System.currentTimeMillis()) {
            Alarm.Companion.setAlarm(context, timeInMillis, createAlarmId(),
                    if (isActivate) Alarm.Companion.START else Alarm.Companion.NONE)
        }
    }

    fun cancelAlarm(context: Context?) {
        Alarm.Companion.cancelAlarm(context, createAlarmId())
    }

    val dateTime: CurrentDate
        get() {
            val dateRing = CurrentDate()
            dateRing.timeInMillis = timeInMillis
            return dateRing
        }

    override fun toString(): String {
        return "AlarmRing{" +
                "timeInMillis=" + timeInMillis +
                ", isActivate=" + isActivate +
                '}'
    }

    override fun compareTo(alarmRing: AlarmRing): Int {
        return java.lang.Long.compare(timeInMillis, alarmRing.timeInMillis)
    }

    companion object {
        fun askTime(context: Context?, title: String?, onTimeSetListener: OnTimeSetListener?) {
            val timePickerDialog = TimePickerDialog(context, onTimeSetListener, 0, 0, true)
            timePickerDialog.setTitle(title)
            timePickerDialog.show()
        }

        fun askTime(context: Context?, onTimeSetListener: OnTimeSetListener?) {
            val timePickerDialog = TimePickerDialog(context, onTimeSetListener, 0, 0, true)
            timePickerDialog.show()
        }
    }
}