package com.iutcalendar.calendrier;


import android.content.Context;
import android.util.Log;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.event.changement.EventChangment;
import com.iutcalendar.task.PersonnalCalendrier;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Calendrier implements Cloneable {

    static private final String DELIMITER_LINE = "\n(?=[A-Z])";
    private List<EventCalendrier> events;

    public Calendrier(List<EventCalendrier> events) {
        setEvent(events);
    }

    public Calendrier(String txtIcs) {
        loadFromString(txtIcs);
    }

    public static String changeToString(Context context, List<EventChangment> changes) {
        StringBuilder msg = new StringBuilder();

        for (EventChangment tuple : changes) {
            String action = tuple.getInfoChange().getChangement().toString(context);
            msg.append(action).append(" : ").append(tuple.getEventChanged().getNameEvent()).append(" : ");
            if (tuple.getInfoChange().getPrevDate() != null) {
                msg.append(tuple.getInfoChange().getPrevDate().toString());
            }
            if (tuple.getInfoChange().getNewDate() != null) {
                msg.append(" -> ").append(tuple.getInfoChange().getNewDate().toString());
            }
            msg.append('\n');
        }
        return msg.toString();
    }

    public static boolean isValideFormat(String str) {
        final String beging = "BEGIN:VCALENDAR";
        return str.length() >= beging.length() && str.startsWith(beging);
    }

    public static boolean writeCalendarFile(String conentFile, File fileToWrite) throws IOException, InvalideFormatException {
        if (isValideFormat(conentFile)) {
            return FileGlobal.writeFile(conentFile, fileToWrite);
        } else {
            throw new InvalideFormatException("Le format du calendrier n'est pas valide");
        }
    }

    public List<EventCalendrier> getEvents() {
        return events;
    }


    public DateCalendrier getFirstDay() {
        if (getEvents().isEmpty()) return null;
        return getEvents().get(0).getDate();
    }

    public DateCalendrier getLastDay() {
        if (getEvents().isEmpty()) return null;
        return getEvents().get(getEvents().size() - 1).getDate();
    }

    public void loadFromString(String txtIcs) {
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
                    if (!events.isEmpty() && events.get(events.size() - 1).getDate() == null) {
                        Log.e("Event", "date is null");
                    }
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

    public List<DateCalendrier> getDateDays() {
        List<DateCalendrier> dateDays = new LinkedList<>();

        for (EventCalendrier e : getEvents()) {
            if (dateDays.isEmpty() || !dateDays.get(dateDays.size() - 1).sameDay(e.getDate())) {
                dateDays.add(e.getDate());
            }
        }
        return dateDays;
    }

    public List<EventCalendrier> getEventsOfDay(DateCalendrier date) {
        LinkedList<EventCalendrier> eventsOfDay = new LinkedList<>();
        if (date != null && events != null) {
            for (EventCalendrier ev : events) {
                if (ev != null && ev.getDate() != null && ev.getDate().getDay() == date.getDay() && ev.getDate().getMonth() == date.getMonth() && ev.getDate().getYear() == date.getYear()) {
                    eventsOfDay.add(ev);
                } else if (ev == null) {
                    Log.e("Event", "ev is null");
                } else if (ev.getDate() == null) {
                    Log.e("Event", "ev date is null");
                }
            }
//            for (EventCalendrier ev : events) {
//                if (ev.getDate().getDay() == date.getDay() && ev.getDate().getMonth() == date.getMonth() && ev.getDate().getYear() == date.getYear()) {
//                    eventsOfDay.add(ev);
//                }
//            }
        } else {
            Log.e("Event", "is null");
        }
        if (events.isEmpty()) {
            Log.e("Event", "is empty");

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
            }
        }
        return nexts;
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

    public List<EventChangment> getChangedEvent(Calendrier prevCal) {
        //FIXME optimize
        List<EventChangment> changed = new ArrayList<>();
        EventCalendrier ev;
        for (EventCalendrier e : this.getEvents()) {
            int index = prevCal.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new EventChangment(e, new EventChangment.InfoChange(EventChangment.Change.ADD, null, e.getDate())));
            } else {
                ev = prevCal.getEvents().get(index);
                if (!e.getDate().equals(ev.getDate())) {
                    changed.add(new EventChangment(e, new EventChangment.InfoChange(EventChangment.Change.MOVE, ev.getDate(), e.getDate())));
                }
            }
        }
        for (EventCalendrier e : prevCal.getEvents()) {
            int index = this.getEvents().indexOf(e);
            if (index == -1) {
                changed.add(new EventChangment(e, new EventChangment.InfoChange(EventChangment.Change.DELETE, e.getDate(), null)));
            }
        }
        return changed;
    }

    public void deleteUselessTask(Context context) {
        LinkedList<String> UIDs = new LinkedList<>();

        for (EventCalendrier event : getEvents()) {
            UIDs.add(event.getUID());
        }
        Iterator<String> it = PersonnalCalendrier.getInstance(context).getKeys().iterator();
        while (it.hasNext()) {
            String UID = it.next();
            if (!UIDs.contains(UID)) {//alors on doit suprrimé les tache lié à cette UID (vieux UID)
                PersonnalCalendrier.getInstance(context).removeAllLinkedTask(context, UID, it);
            }
        }

        PersonnalCalendrier.getInstance(context).save(context);

    }

    @Override
    public int hashCode() {
        return Objects.hash(events);
    }

    @Override
    public Calendrier clone() {
        try {
            Calendrier clone = (Calendrier) super.clone();
            clone.events = new ArrayList<>(events);
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private enum State {
        CLOSE, OPEN, EVENT
    }
}
