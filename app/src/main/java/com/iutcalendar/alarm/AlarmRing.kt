package com.iutcalendar.alarm

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.settings.SettingsApp
import java.io.Serializable

class AlarmRing(val timeInMillis: Long, var isActivate: Boolean = true, var isDeletable: Boolean = false) : Serializable, Comparable<AlarmRing> {

    fun createAlarmId(): String {
        return timeInMillis.toString()
    }

    fun setAlarm(context: Context?) {
        if (timeInMillis >= System.currentTimeMillis()) {
            Alarm.setAlarm(
                context, timeInMillis, createAlarmId(),
                if (isActivate) Alarm.START else Alarm.NONE
            )
        }
    }

    fun cancelAlarm(context: Context?) {
        Alarm.cancelAlarm(context, createAlarmId())
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

    override fun compareTo(other: AlarmRing): Int {
        return timeInMillis.compareTo(other.timeInMillis)
    }

    companion object {
        fun askTime(context: Context?, title: String?, onTimeSetListener: OnTimeSetListener?) {
            val timePickerDialog = TimePickerDialog(context, onTimeSetListener, 0, 0, SettingsApp.locale == SettingsApp.localFrance)
            timePickerDialog.setTitle(title)
            timePickerDialog.show()
        }
        fun askTime(context: Context?, title: String?, onTimeSetListener: OnTimeSetListener?, hourOfDay : Int = 0, minute : Int = 0) {
            val timePickerDialog = TimePickerDialog(context, onTimeSetListener, hourOfDay, minute, SettingsApp.locale == SettingsApp.localFrance)
            timePickerDialog.setTitle(title)
            timePickerDialog.show()
        }

        fun askTime(context: Context?, hourOfDay : Int = 0, minute : Int = 0, onTimeSetListener: OnTimeSetListener?) {
            val timePickerDialog = TimePickerDialog(context, onTimeSetListener, hourOfDay, minute, SettingsApp.locale == SettingsApp.localFrance)
            timePickerDialog.show()
        }
    }
}