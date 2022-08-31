package com.iutcalendar.calendrier

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.*

open class DateCalendrier() : GregorianCalendar() {

    init {
        set(SECOND, 0)
        set(MILLISECOND, 0)
    }

    constructor(day: Int, month: Int, year: Int, hour: Int, minute: Int) : this() {
        this.day = day
        this.month = month
        this.year = year
        this.hour = hour
        this.minute = minute
    }

    constructor(other: DateCalendrier) : this(
        other.day,
        other.month,
        other.year,
        other.hour,
        other.minute
    )

    fun from(gregorianCalendar: GregorianCalendar): DateCalendrier {
        this.set(YEAR, gregorianCalendar.get(YEAR))
        this.set(DAY_OF_YEAR, gregorianCalendar.get(DAY_OF_YEAR))
        this.set(HOUR_OF_DAY, gregorianCalendar.get(HOUR_OF_DAY))
        this.set(MINUTE, gregorianCalendar.get(MINUTE))
        return this
    }

    /**
     * Le décalage est appliqué à la lecture du fichier uniquement
     * par la suite toutes les dates aurons le bon décalage.
     */
    fun doZoneOffset() {
        val offset = ZonedDateTime.of(
            year,
            month,
            day,
            hour,
            minute,
            0,
            0,
            ZoneId.of(zoneOffset)
        )[ChronoField.OFFSET_SECONDS]
        hour += offset / 3600
    }

    var dayOfYear: Int
        get() = get(DAY_OF_YEAR)
        set(day) {
            set(DAY_OF_YEAR, day)
        }

    var day: Int
        get() = get(DAY_OF_MONTH)
        set(day) {
            set(DAY_OF_MONTH, day)
        }
    var month: Int
        get() = get(MONTH) + 1
        set(month) {
            set(MONTH, month - 1)
        }
    var year: Int
        get() = get(YEAR)
        set(year) {
            set(YEAR, year)
        }
    var hour: Int
        get() = get(HOUR_OF_DAY)
        set(hour) {
            set(HOUR_OF_DAY, hour)
        }
    var minute: Int
        get() = get(MINUTE)
        set(minute) {
            set(MINUTE, minute)
        }

    fun subTime(o: DateCalendrier): DateCalendrier {
        val diff = DateCalendrier(this)
        diff.add(HOUR_OF_DAY, -1 * o.hour)
        diff.add(MINUTE, -1 * o.minute)
        return diff
    }

    fun addTime(o: DateCalendrier?): DateCalendrier {
        val diff = DateCalendrier(this)
        diff.add(HOUR_OF_DAY, o!!.hour)
        diff.add(MINUTE, o.minute)
        return diff
    }

    fun timeToString(): String {
        return hour.toString() + ":" + fillWithZeroBefore(minute)
    }

    val hourInMillis: Long
        get() = getHourInMillis(hour, minute)

    fun setHourWithMillis(millis: Long) {
        var timeInMillis = millis
        timeInMillis /= 1000 //en seconds
        val minuteInSec = (timeInMillis % 3600).toInt()
        timeInMillis -= minuteInSec.toLong()
        val hour = (timeInMillis / 3600).toInt()
        this.hour = hour
        minute = minuteInSec / 60
    }

    override fun toString(): String {
        return getDisplayName(DAY_OF_WEEK, SHORT, Locale.FRANCE)?.toString() + " " +
                fillWithZeroBefore(day) + "/" + fillWithZeroBefore(month) + "/" + year
    }

    fun getNbrDayTo(other: DateCalendrier?): Int {
        val nbrDayInYear = if (isYearBissextile(year)) 366 else 365
        return (other!!.year - year) * nbrDayInYear + other.dayOfYear - dayOfYear
    }

    open fun addDay(days: Int): DateCalendrier {
        val newDate = CurrentDate(this)
        newDate.add(DAY_OF_YEAR, days)
        return newDate
    }

    fun sameDay(obj: Any?): Boolean {
        if (obj == null) return false
        if (this === obj) return true
        if (obj is DateCalendrier) {
            return get(DAY_OF_YEAR) == obj[DAY_OF_YEAR] && year == obj.year
        }
        return false
    }


    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + dayOfYear
        result = 31 * result + year
        result = 31 * result + hour
        result = 31 * result + minute
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DateCalendrier) return false
        if (!super.equals(other)) return false

        if (timeInMillis != other.timeInMillis) return false

        return true
    }

    companion object {
        var zoneOffset = "Europe/Paris"

        fun fillWithZeroBefore(n: Int): String {
            var s = n.toString()
            if (s.length < 2) {
                s = "0$s"
            }
            return s
        }

        fun fillWithZeroAfter(n: Int): String {
            var s = n.toString()
            if (s.length < 2) {
                s += "0"
            }
            return s
        }

        fun isYearBissextile(year: Int): Boolean {
            val d = DateCalendrier(1, 3, year, 12, 0)
            return d.dayOfYear == 61
        }

        fun getHourInMillis(hour: Int, minute: Int): Long {
            return hour * 3600000L + minute * 60000L
        }

        fun timeLongToString(timeMillis: Long): String {
            val t = DateCalendrier()
            t.setHourWithMillis(timeMillis)
            //        t.setTimeInMillis(timeMillis);
            return t.timeToString()
        }
    }
}