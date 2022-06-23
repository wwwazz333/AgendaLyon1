package com.iutcalendar.event.changement;

import android.content.Context;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;

import java.io.Serializable;

public class EventChangment implements Serializable {
    private final InfoChange infoChange;
    private final EventCalendrier eventChanged;

    private final DateCalendrier dateOfTheChangement;

    public EventChangment(EventCalendrier copyEventChanged, InfoChange infoChange) {
        this.infoChange = infoChange;
        this.eventChanged = copyEventChanged;

        this.dateOfTheChangement = new DateCalendrier();
    }

    public InfoChange getInfoChange() {
        return infoChange;
    }

    public EventCalendrier getEventChanged() {
        return eventChanged;
    }

    public DateCalendrier getDateOfTheChangement() {
        return dateOfTheChangement;
    }

    public enum Change {
        ADD,
        DELETE,
        MOVE,
        NONE;


        public String toString(Context context) {
            if (equals(Change.ADD)) {
                return context.getString(R.string.Added);
            } else if (equals(Change.DELETE)) {
                return context.getString(R.string.Deleted);
            } else if (equals(Change.MOVE)) {
                return context.getString(R.string.Moved);
            }
            return toString();
        }
    }

    public static class InfoChange implements Serializable {
        private final Change changement;
        private final DateCalendrier prevDate;
        private final DateCalendrier newDate;

        public InfoChange(Change changement, DateCalendrier prevDate, DateCalendrier newDate) {
            this.changement = changement;
            this.prevDate = prevDate;
            this.newDate = newDate;
        }

        public Change getChangement() {
            return changement;
        }

        public DateCalendrier getPrevDate() {
            return prevDate;
        }

        public DateCalendrier getNewDate() {
            return newDate;
        }
    }
}
