package com.iutcalendar.alarm

import android.content.Context
import android.util.Log
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.data.FileGlobal
import java.io.Serializable
import java.util.*

class PersonalAlarmManager : Serializable {
    private var alarms: HashMap<String, MutableList<AlarmRing>>?

    init {
        alarms = HashMap()
    }

    fun add(day: DateCalendrier, alarmRing: AlarmRing): Boolean {
        Log.d("Alarm", alarmRing.toString())
        if (alarmRing.timeInMillis > System.currentTimeMillis()) {
            val alarmOfDay = alarms!!.getOrDefault(createDayId(day), LinkedList())
            if (alarmOfDay.isEmpty()) {
                alarms!![createDayId(day)] = alarmOfDay
            }
            alarmOfDay.add(alarmRing)
            return true
        }
        return false
    }

    fun setUpAlarms(context: Context?) {
        for ((_, value) in alarms!!) {
            for (alarm in value) {
                alarm.setAlarm(context)
            }
        }
    }

    /**
     * @param day le jour où doivent sonner les alarms qu'on retourne
     * @return le timeMillis de l'alarme correspondant à event ou -1 si pas d'alarme
     */
    operator fun get(day: DateCalendrier): MutableList<AlarmRing> {
        return alarms!!.getOrDefault(createDayId(day), LinkedList())
    }

    private fun removeAlarm(context: Context?, alarmRing: AlarmRing, it: MutableIterator<AlarmRing>) {
        alarmRing.cancelAlarm(context)
        it.remove()
    }

    fun remove(context: Context?, alarmRing: AlarmRing) {
        for (list in alarms!!.values) {
            val it = list.iterator()
            while (it.hasNext()) {
                val alarm = it.next()
                if (alarm == alarmRing) {
                    removeAlarm(context, alarm, it)
                }
            }
        }
    }

    fun removeForDay(context: Context?, day: DateCalendrier) {
        val it = get(day).iterator()
        while (it.hasNext()) {
            removeAlarm(context, it.next(), it)
        }
    }

    fun removeUseless(context: Context?) {
        for ((_, value) in alarms!!) {
            val it = value.iterator()
            while (it.hasNext()) {
                val alarmRing = it.next()
                if (alarmRing.timeInMillis < 0 || alarmRing.timeInMillis < System.currentTimeMillis()) {
                    removeAlarm(context, alarmRing, it)
                }
            }
        }
    }

    val allAlarmToList: ArrayList<AlarmRing>
        get() {
            val liste = ArrayList<AlarmRing>()
            for (listAlarm in alarms!!.values) {
                liste.addAll(listAlarm)
            }
            liste.sort()
            return liste
        }

    fun load(context: Context?) {
        alarms = FileGlobal.loadBinaryFile(FileGlobal.getFilePersonalAlarm(context))
        if (alarms == null) {
            alarms = HashMap()
        }
    }

    fun save(context: Context?) {
        FileGlobal.writeBinaryFile(alarms, FileGlobal.getFilePersonalAlarm(context))
    }

    companion object {
        private var instance: PersonalAlarmManager? = null
        fun getInstance(context: Context?): PersonalAlarmManager {
            if (instance == null) {
                instance = PersonalAlarmManager()
                instance!!.load(context)
            }
            return instance!!
        }

        fun createDayId(day: DateCalendrier): String {
            return day.year.toString() + ":" + day[GregorianCalendar.DAY_OF_YEAR]
        }
    }
}