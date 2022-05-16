package com.iutcalendar.calendrier;


import com.iutcalendar.data.Tuple;

import java.util.*;

public class Calendrier {
    public enum Change {
        ADD,
        DELETE,
        MOVE,
        NONE
    }

    static private final String DELIMITER_LINE = "\n(?=[A-Z])";
    private final List<EventCalendrier> events;

    public List<EventCalendrier> getEvents() {
        return events;
    }

    public Calendrier clone() {
        List<EventCalendrier> n = new ArrayList<>();
        for (EventCalendrier e : events) {
            n.add(e);
        }
        return new Calendrier(n);
    }

    public Calendrier(List<EventCalendrier> events) {
        this.events = events;
        Collections.sort(events);
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
        for (EventCalendrier ev : events) {
            if (ev.getDate().getDay() == date.getDay() && ev.getDate().getMonth() == date.getMonth() && ev.getDate().getYear() == date.getYear()) {
                eventsOfDay.add(ev);
            }
        }

        return eventsOfDay;
    }

    public EventCalendrier getLastEvent() {
        if (events.isEmpty()) return null;
        return events.get(events.size() - 1);
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

    public List<Tuple<EventCalendrier, Change>> getChangedEvent(Calendrier prevCal) {
        List<Tuple<EventCalendrier, Change>> changed = new ArrayList<>();
        EventCalendrier ev;
        for (EventCalendrier e : this.getEvents()) {
            int index = prevCal.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new Tuple<>(e, Change.ADD));
            } else {
                ev = prevCal.getEvents().get(index);
                if (!e.getDate().equals(ev.getDate())) {
                    changed.add(new Tuple<>(e, Change.MOVE));
                }
            }
        }
        for (EventCalendrier e : prevCal.getEvents()) {
            int index = this.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new Tuple<>(e, Change.DELETE));
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
