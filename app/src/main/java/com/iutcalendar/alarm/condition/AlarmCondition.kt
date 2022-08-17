package com.iutcalendar.alarm.condition

import com.iutcalendar.calendrier.EventCalendrier
import java.io.Serializable
import java.util.*

class AlarmCondition(
    var begin: Long, var end: Long, //les bornes sont comprises (hours in millis).
    var alarmAt: Long,
    /**
     * Contient les jours où l'alarme s'active
     *
     * @example GregorianCalendar.MONDAY
     */
    val daysEnabled: ArrayList<Int?>
) : Serializable {

    constructor(begin: Long, end: Long, alarmAt: Long) : this(begin, end, alarmAt, ArrayList<Int?>()) {
        daysEnabled.add(GregorianCalendar.MONDAY)
        daysEnabled.add(GregorianCalendar.TUESDAY)
        daysEnabled.add(GregorianCalendar.WEDNESDAY)
        daysEnabled.add(GregorianCalendar.THURSDAY)
        daysEnabled.add(GregorianCalendar.FRIDAY)
    }

    fun isApplicableTo(event: EventCalendrier?): Boolean {
        return if (event?.date?.hourInMillis != null) {
            val dateEventMillis = event.date!!.hourInMillis
            dateEventMillis in begin..end &&
                    daysEnabled.contains(event.date!![GregorianCalendar.DAY_OF_WEEK])
        } else {
            false
        }
    }
}