package com.iutcalendar.calendrier;

import android.content.Context;
import com.calendar.iutcalendar.R;

import java.io.Serializable;

public class EventChangment implements Serializable {
    private InfoChange infoChange;
    private EventCalendrier copyEventChanged;

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

    public static class InfoChange {
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

    public EventChangment(EventCalendrier copyEventChanged, InfoChange infoChange) {
        this.infoChange = infoChange;
        this.copyEventChanged = copyEventChanged;
    }

    public InfoChange getInfoChange() {
        return infoChange;
    }

    public EventCalendrier getCopyEventChanged() {
        return copyEventChanged;
    }
}
