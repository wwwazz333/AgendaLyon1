package com.iutcalendar.alarm.condition

import com.iutcalendar.calendrier.EventCalendrier
import java.io.Serializable
import java.util.*

class AlarmCondtion(
    var beging: Long, var end: Long, //les bornes sont comprises (hours in millis).
    var alarmAt: Long,
    /**
     * Contient les jours où l'alarme s'active
     *
     * @exmple GregorianCalendar.MONDAY
     */
    val daysEnabled: ArrayList<Int?>
) : Serializable {

    constructor(beging: Long, end: Long, alarmAt: Long) : this(beging, end, alarmAt, ArrayList<Int?>()) {
        daysEnabled.add(GregorianCalendar.MONDAY)
        daysEnabled.add(GregorianCalendar.TUESDAY)
        daysEnabled.add(GregorianCalendar.WEDNESDAY)
        daysEnabled.add(GregorianCalendar.THURSDAY)
        daysEnabled.add(GregorianCalendar.FRIDAY)
    }

    fun isApplicableTo(event: EventCalendrier?): Boolean {
        return if (event?.date?.hourInMillis != null) {
            val dateEventMillis = event.date!!.hourInMillis
            dateEventMillis in beging..end &&
                    daysEnabled.contains(event.date!![GregorianCalendar.DAY_OF_WEEK])
        } else {
            false
        }
    }
}