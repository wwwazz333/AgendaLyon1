package com.iutcalendar.calendrier;


import android.content.Context;
import android.util.Log;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.Tuple;

import java.util.*;

public class Calendrier {
    public enum Change {
        ADD,
        DELETE,
        MOVE,
        NONE;


        public String toString(Context context) {
            if (equals(Calendrier.Change.ADD)) {
                return context.getString(R.string.Added);
            } else if (equals(Calendrier.Change.DELETE)) {
                return context.getString(R.string.Deleted);
            } else if (equals(Calendrier.Change.MOVE)) {
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

    static private final String DELIMITER_LINE = "\n(?=[A-Z])";
    private List<EventCalendrier> events;

    public List<EventCalendrier> getEvents() {
        return events;
    }

    public Calendrier clone() {
        List<EventCalendrier> n = new ArrayList<>();
        n.addAll(events);
        return new Calendrier(n);
    }

    public Calendrier(List<EventCalendrier> events) {
        setEvent(events);
    }

    public Calendrier(String txtIcs) {
        events = new ArrayList<>();
        String[] lines = txtIcs.split(DELIMITER_LINE);

        State stateCal = State.CLOSE;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            if (lines[i].equals("BEGIN:VCALENDAR")) {
                stateCal = State.OPEN;
            } else if (stateCal == State.OPEN) {
                if (lines[i].equals("END:VCALENDAR")) {
                    stateCal = State.CLOSE;
                    break;
                } else if (lines[i].equals("BEGIN:VEVENT")) {
                    events.add(new EventCalendrier());
                    stateCal = State.EVENT;
                }
            } else if (stateCal == State.EVENT) {
                if (lines[i].equals("END:VEVENT")) {
                    stateCal = State.OPEN;
                } else {
                    events.get(events.size() - 1).parseLine(lines[i]);
                }
            }
        }
        setEvent(events);
    }

    private void setEvent(List<EventCalendrier> events) {
        this.events = events;
        Collections.sort(events);
    }


    public Calendrier getCalOfWeek(int weekNum) {
        LinkedList<EventCalendrier> eventsOfWeek = new LinkedList<>();

        for (EventCalendrier ev : events) {
            if (ev.getDate().get(GregorianCalendar.WEEK_OF_YEAR) == weekNum) {
                eventsOfWeek.add(ev);
            }
        }

        return new Calendrier(eventsOfWeek);
    }

    public List<EventCalendrier> getEventsOfDay(DateCalendrier date) {
        LinkedList<EventCalendrier> eventsOfDay = new LinkedList<>();
        if (date != null) {
            for (EventCalendrier ev : events) {
                if (ev.getDate().getDay() == date.getDay() && ev.getDate().getMonth() == date.getMonth() && ev.getDate().getYear() == date.getYear()) {
                    eventsOfDay.add(ev);
                }
            }
        }

        return eventsOfDay;
    }

    public EventCalendrier getLastEvent() {
        if (events.isEmpty()) return null;
        return events.get(events.size() - 1);
    }

    public EventCalendrier[] getNext2EventAfter(DateCalendrier date) {
        EventCalendrier[] nexts = new EventCalendrier[2];
        int i = 0;
        for (EventCalendrier e : getEvents()) {
            if (i >= 2) {
                break;
            } else if (e.getDate().getTimeInMillis() >= date.getTimeInMillis()) {
                nexts[i++] = e;
            } else {
                Log.d("Widget", e.getDate().timeToString());
            }
        }
        Log.d("Widget", date.timeToString());
        return nexts;
    }

    public static String changeToString(Context context, List<Tuple<EventCalendrier, Calendrier.InfoChange>> changes) {
        StringBuilder msg = new StringBuilder();

        for (Tuple<EventCalendrier, Calendrier.InfoChange> tuple : changes) {
            String action = tuple.second.getChangement().toString(context);
            msg.append(action).append(" : ").append(tuple.first.getSummary()).append(" : ");
            if (tuple.second.getPrevDate() != null) {
                msg.append(tuple.second.getPrevDate().toString());
            }
            if (tuple.second.getNewDate() != null) {
                msg.append(" -> ").append(tuple.second.getNewDate().toString());
            }
            msg.append('\n');
        }
        return msg.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (EventCalendrier ev : events) {
            builder.append(ev.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendrier that = (Calendrier) o;
        return Objects.equals(events, that.events);
    }

    public List<Tuple<EventCalendrier, InfoChange>> getChangedEvent(Calendrier prevCal) {
        //FIXME optimize
        List<Tuple<EventCalendrier, InfoChange>> changed = new ArrayList<>();
        EventCalendrier ev;
        for (EventCalendrier e : this.getEvents()) {
            int index = prevCal.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new Tuple<>(e, new InfoChange(Change.ADD, null, e.getDate())));
            } else {
                ev = prevCal.getEvents().get(index);
                if (!e.getDate().equals(ev.getDate())) {
                    changed.add(new Tuple<>(e, new InfoChange(Change.MOVE, ev.getDate(), e.getDate())));
                }
            }
        }
        for (EventCalendrier e : prevCal.getEvents()) {
            int index = this.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new Tuple<>(e, new InfoChange(Change.DELETE, e.getDate(), null)));
            }
        }
        return changed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(events);
    }

    private enum State {
        CLOSE, OPEN, EVENT
    }
}
