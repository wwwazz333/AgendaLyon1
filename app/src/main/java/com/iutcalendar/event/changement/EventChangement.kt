package com.iutcalendar.event.changement

import android.content.Context
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.calendrier.EventCalendrier
import com.univlyon1.tools.agenda.R
import java.io.Serializable

class EventChangement(val eventChanged: EventCalendrier, val infoChange: InfoChange) : Serializable {
    val dateOfTheChangement: DateCalendrier = DateCalendrier()

    enum class Change {
        ADD, DELETE, MOVE;

        fun toString(context: Context?): String {
            if (equals(ADD)) {
                return context!!.getString(R.string.Added)
            } else if (equals(DELETE)) {
                return context!!.getString(R.string.Deleted)
            } else if (equals(MOVE)) {
                return context!!.getString(R.string.Moved)
            }
            return toString()
        }
    }

    class InfoChange(val changement: Change, val prevDate: DateCalendrier?, val newDate: DateCalendrier?) : Serializable
}