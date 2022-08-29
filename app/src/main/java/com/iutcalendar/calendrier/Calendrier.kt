package com.iutcalendar.calendrier

import android.content.Context
import android.util.Log
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.event.changement.EventChangement
import com.iutcalendar.event.changement.EventChangement.Change
import com.iutcalendar.event.changement.EventChangement.InfoChange
import com.iutcalendar.task.PersonalCalendrier
import java.io.File
import java.io.IOException
import java.util.*

open class Calendrier : Cloneable {
    var events: MutableList<EventCalendrier> = mutableListOf()
        private set

    constructor(events: MutableList<EventCalendrier>) {
        setEvent(events)
    }

    constructor(txtIcs: String?) {
        loadFromString(txtIcs)
    }


    val firstDay: DateCalendrier?
        get() = if (events.isEmpty()) null else events[0].date
    val lastDay: DateCalendrier?
        get() = if (events.isEmpty()) null else events[events.size - 1].date

    fun loadFromString(txtIcs: String?) {
        events = ArrayList()
        val lines = txtIcs!!.split(DELIMITER_LINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var stateCal = State.CLOSE
        for (i in lines.indices) {
            lines[i] = lines[i].trim { it <= ' ' }
            if (lines[i] == "BEGIN:VCALENDAR") {
                stateCal = State.OPEN
            } else if (stateCal == State.OPEN) {
                if (lines[i] == "END:VCALENDAR") {
                    stateCal = State.CLOSE
                    break
                } else if (lines[i] == "BEGIN:VEVENT") {
                    if (events.isNotEmpty() && events[events.size - 1].date == null) {
                        Log.e("Event", "date is null")
                    }
                    events.add(EventCalendrier())
                    stateCal = State.EVENT
                }
            } else if (stateCal == State.EVENT) {
                if (lines[i] == "END:VEVENT") {
                    stateCal = State.OPEN
                } else {
                    events[events.size - 1].parseLine(lines[i])
                }
            }
        }
        setEvent(events)
    }

    private fun setEvent(events: MutableList<EventCalendrier>) {
        this.events = events
        events.sort()
    }

    fun getCalOfWeek(weekNum: Int): Calendrier {
        val eventsOfWeek = LinkedList<EventCalendrier>()
        for (ev in events) {
            if (ev.date != null && ev.date!![GregorianCalendar.WEEK_OF_YEAR] == weekNum) {
                eventsOfWeek.add(ev)
            }
        }
        return Calendrier(eventsOfWeek)
    }

    val dateDays: List<DateCalendrier?>
        get() {
            val dateDays: MutableList<DateCalendrier?> = LinkedList()
            for (e in events) {
                if (dateDays.isEmpty() || !dateDays[dateDays.size - 1]!!.sameDay(e.date)) {
                    dateDays.add(e.date)
                }
            }
            return dateDays
        }

    fun getEventsOfDay(date: DateCalendrier): List<EventCalendrier> {
        val eventsOfDay = LinkedList<EventCalendrier>()
        for (ev in events) {
            if (ev.date != null && ev.date!!.sameDay(date)) {
                eventsOfDay.add(ev)
            } else if (ev.date == null) {
                Log.e("Event", "ev date is null")
            }
        }
        if (events.isEmpty()) {
            Log.e("Event", "is empty")
        }
        return eventsOfDay
    }

    fun getEventsDuring(date: DateCalendrier): List<EventCalendrier> {
        val eventsDuring = LinkedList<EventCalendrier>()
        for (ev in events) {
            if (ev.date != null && date in ev.date!!..ev.date!!.addTime(ev.dure)) {
                eventsDuring.add(ev)
            } else if (ev.date == null) {
                Log.e("Event", "ev date is null")
            }
        }
        if (events.isEmpty()) {
            Log.e("Event", "is empty")
        }
        return eventsDuring
    }

    val lastEvent: EventCalendrier?
        get() = if (events.isEmpty()) null else events[events.size - 1]

    fun getNext2EventAfter(date: DateCalendrier): Array<EventCalendrier?> {
        val next2 = arrayOfNulls<EventCalendrier>(2)
        var i = 0
        for (e in events) {
            if (i >= 2) {
                break
            } else if (e.date != null && e.date!!.timeInMillis >= date.timeInMillis) {
                next2[i++] = e
            }
        }
        return next2
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (ev in events) {
            builder.append(ev.toString()).append("\n")
        }
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Calendrier

        if (events != other.events) return false

        return true
    }

    fun getChangedEvent(prevCal: Calendrier?): List<EventChangement> {
        //FIXME optimize
        val changed: MutableList<EventChangement> = ArrayList()
        var ev: EventCalendrier
        for (e in events) {
            val index = prevCal!!.events.indexOf(e)
            if (index == -1) {
                changed.add(EventChangement(e, InfoChange(Change.ADD, null, e.date)))
            } else {
                ev = prevCal.events[index]
                if (e.date != ev.date) {
                    changed.add(EventChangement(e, InfoChange(Change.MOVE, ev.date, e.date)))
                }
            }
        }
        for (e in prevCal!!.events) {
            val index = events.indexOf(e)
            if (index == -1) {
                changed.add(EventChangement(e, InfoChange(Change.DELETE, e.date, null)))
            }
        }
        return changed
    }

    fun deleteUselessTask(context: Context?) {
        val uids = LinkedList<String?>()
        for (event in events) {
            uids.add(event.uid)
        }
        val it: MutableIterator<String?> = PersonalCalendrier.getInstance(context).keys.iterator()
        while (it.hasNext()) {
            val uid = it.next()
            if (!uids.contains(uid)) { //alors on doit supprimer les taches liées à cette UID (vieux UID).
                PersonalCalendrier.getInstance(context).removeAllLinkedTask(context, uid, it)
            }
        }
        PersonalCalendrier.getInstance(context).save(context)
    }

    override fun hashCode(): Int {
        return Objects.hash(events)
    }

    public override fun clone(): Calendrier {
        return try {
            val clone = super.clone() as Calendrier
            clone.events = ArrayList(events)
            clone
        } catch (e: CloneNotSupportedException) {
            throw AssertionError()
        }
    }

    private enum class State {
        CLOSE, OPEN, EVENT
    }

    companion object {
        private const val DELIMITER_LINE = "\n(?=[A-Z])"

        private fun isValideFormat(str: String): Boolean {
            val begin = "BEGIN:VCALENDAR"
            return str.length >= begin.length && str.startsWith(begin)
        }

        @Throws(IOException::class, InvalideFormatException::class)
        fun writeCalendarFile(contentFile: String, fileToWrite: File): Boolean {
            return if (isValideFormat(contentFile)) {
                FileGlobal.writeFile(contentFile, fileToWrite)
            } else {
                throw InvalideFormatException("Le format du calendrier n'est pas valide")
            }
        }
    }
}