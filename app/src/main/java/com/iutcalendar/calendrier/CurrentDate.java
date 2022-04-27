package com.iutcalendar.calendrier;

import java.util.GregorianCalendar;
import java.util.Locale;

public class CurrentDate extends DateCalendrier {


    public CurrentDate() {
        super();
    }

    public CurrentDate(DateCalendrier other) {
        super(other);
    }

    public CurrentDate(int day, int month, int year, int hour, int minute) {
        super(day, month, year, hour, minute);
    }

    public CurrentDate getDateOfDayOfWeek(int day) {
        int currDayOfWeek = get(GregorianCalendar.DAY_OF_WEEK);

        set(GregorianCalendar.DAY_OF_WEEK, day);

        CurrentDate date = new CurrentDate(this);

        set(GregorianCalendar.DAY_OF_WEEK, currDayOfWeek);
        return date;
    }

    public CurrentDate nextWeek(){
        CurrentDate cal = new CurrentDate(this);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 7);
        return cal;
    }
    public CurrentDate prevWeek(){
        CurrentDate cal = new CurrentDate(this);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -7);
        return cal;
    }

    @Override
    public String toString() {
        return getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, Locale.FRANCE) + " " +
                fillWithZeroBefor(getDay()) + "/" + fillWithZeroBefor(getMonth()+1) + "/" + getYear();
    }
}
