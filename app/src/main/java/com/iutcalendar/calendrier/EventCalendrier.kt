package com.iutcalendar.calendrier

import android.util.Log
import java.io.Serializable
import java.util.*

class EventCalendrier : Comparable<EventCalendrier>, Serializable, Cloneable {
    var nameEvent: String
        private set
    var salle: String
        private set
    var description: String
        private set
    var date: DateCalendrier?
        private set
    private var dureeH: Int
    private var dureeM: Int
    var uid: String
        private set

    constructor() {
        date = null
        dureeH = 0
        dureeM = 0
        nameEvent = ""
        salle = ""
        description = ""
        uid = ""
    }

    constructor(debut: DateCalendrier?, dureeH: Int, dureeM: Int, summary: String, salle: String, description: String, UID: String) {
        date = debut
        this.dureeH = dureeH
        this.dureeM = dureeM
        nameEvent = summary
        this.salle = salle
        this.description = description
        uid = UID
    }

    val duree: DateCalendrier
        get() = DateCalendrier(0, 0, 0, dureeH, dureeM)

    @Throws(CloneNotSupportedException::class)
    override fun clone(): EventCalendrier {
        super.clone()
        return EventCalendrier(date, dureeH, dureeM, nameEvent, salle, description, uid)
    }

    fun parseLine(str: String) {
        val splited = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (splited.size < 2) {
            return
        }
        val title = splited[0]
        if (title == "DTSTART") { //20220318T090000Z
            date = getDateTime(splited[1])
        } else if (title == "DTEND" && date != null) {
            var fin: DateCalendrier? = getDateTime(splited[1])
            fin = fin!!.subTime(date!!)
            dureeH = fin[GregorianCalendar.HOUR_OF_DAY]
            dureeM = fin[GregorianCalendar.MINUTE]
        } else if (title == "SUMMARY") {
            nameEvent = splited[1]
        } else if (title == "LOCATION") {
            salle = splited[1]
        } else if (title == "DESCRIPTION") {
            description = str.substring(str.indexOf(':') + 1).trim { it <= ' ' }.replace("\\n", "\n").replace("^\n*|\n*$".toRegex(), "")
        } else if (title == "UID") {
            uid = splited[1]
        }
    }

    private fun getDateTime(str: String): DateCalendrier {
        val dateTime = str.substring(0, str.indexOf('Z')).split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val date = dateTime[0]
        val time = dateTime[1]
        val year = date.substring(0, 4).toInt()
        val month = date.substring(4, 6).toInt()
        val day = date.substring(6, 8).toInt()
        val hour = time.substring(0, 2).toInt()
        val minute = time.substring(2, 4).toInt()
        val dateCalendrier = DateCalendrier(day, month, year, hour, minute)
        dateCalendrier.doZoneOffset()
        return dateCalendrier
    }

    override fun compareTo(other: EventCalendrier): Int {
        if (other.date == null) {
            Log.e("Event", "other date null")
            return 1
        } else if (date == null) {
            Log.e("Event", "date null")
            return -1
        }
        return if(other.date != null)
            date!!.compareTo(other.date!!)
        else -1
    }

    override fun toString(): String {
        return date.toString() + " " + dureeH + ":" + dureeM + " : " + nameEvent
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as EventCalendrier
        return uid == that.uid
    }

    override fun hashCode(): Int {
        return Objects.hash(uid)
    }
}