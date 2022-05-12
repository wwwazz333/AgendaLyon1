package com.iutcalendar.calendrier;


import java.util.*;

public class Calendrier {
    static private final String DELIMITER_LINE = "\n(?=[A-Z])";
    private final List<EventCalendrier> events;

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
            if (ev.getDate().getDay() == date.getDay() &&
                    ev.getDate().getMonth() == date.getMonth() &&
                    ev.getDate().getYear() == date.getYear()) {
                eventsOfDay.add(ev);
            }
        }

        return eventsOfDay;
    }

    public EventCalendrier getLastEvent() {
        if (events.isEmpty())
            return null;
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

    private enum State {
        CLOSE, OPEN, EVENT
    }
}
