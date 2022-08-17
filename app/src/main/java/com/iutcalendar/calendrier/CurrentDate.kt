package com.iutcalendar.calendrier

import android.content.Context
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R
import java.util.*

class CurrentDate : DateCalendrier {
    constructor() : super()
    constructor(other: DateCalendrier?) : super(other)
    constructor(day: Int, month: Int, year: Int, hour: Int, minute: Int) : super(day, month, year, hour, minute)

    fun set(other: DateCalendrier) {
        year = other.year
        day = other.day
        month = other.month
        hour = other.hour
        minute = other.minute
    }

    /**
     * @param day lun 0, mar 1, ..., dim 6
     * @return la date du jour de la semaine
     */
    fun getDateOfDayOfWeek(day: Int): CurrentDate {
        return addDay(day - posDayOfWeek)
    }

    //normalement impossible
    private val posDayOfWeek: Int
        get() {
            for (i in DataGlobal.DAYS_OF_WEEK.indices) {
                if (get(DAY_OF_WEEK) == DataGlobal.DAYS_OF_WEEK[i]) {
                    return i
                }
            }
            return -1 //normalement impossible
        }

    fun nextWeek(): CurrentDate {
        return addDay(7)
    }

    fun prevWeek(): CurrentDate {
        return addDay(-7)
    }

    fun addDay(days: Int): CurrentDate {
        val cal = CurrentDate(this)
        cal.add(DAY_OF_YEAR, days)
        return cal
    }

    fun runForDate(today: () -> Unit, tomorrow: () -> Unit, other: () -> Unit) {
        if (sameDay(CurrentDate())) {
            today()
        } else if (sameDay(CurrentDate().addDay(1))) {
            tomorrow()
        } else {
            other()
        }
    }

    fun getRelativeDayName(context: Context): String {
        return if (sameDay(CurrentDate())) {
            context.resources.getString(R.string.today)
        } else if (sameDay(CurrentDate().addDay(1))) {
            context.resources.getString(R.string.tomorrow)
        } else {
            this.toString(SettingsApp.locale)
        }
    }

    fun sameWeek(other: CurrentDate): Boolean {
        return if (year != other.year) {
            false
        } else other.dayOfYear >= getDateOfDayOfWeek(0).dayOfYear && other.dayOfYear <= getDateOfDayOfWeek(6).dayOfYear
    }

    fun toString(location: Locale): String {
        return if (location == Locale("en")) {
            getDisplayName(DAY_OF_WEEK, SHORT, location)?.toString() + " " +
                    fillWithZeroBefore(month) + "/" + fillWithZeroBefore(day) + "/" + year
        } else getDisplayName(DAY_OF_WEEK, SHORT, location)?.toString() + " " +
                fillWithZeroBefore(day) + "/" + fillWithZeroBefore(month) + "/" + year
    }
}