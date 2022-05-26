package com.iutcalendar.calendrier;

import android.content.Context;
import com.calendar.iutcalendar.R;
import com.iutcalendar.settings.SettingsApp;

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

    public CurrentDate nextWeek() {
        return addDay(7);
    }

    public CurrentDate prevWeek() {
        return addDay(-7);
    }

    public CurrentDate addDay(int days) {
        CurrentDate cal = new CurrentDate(this);
        cal.add(GregorianCalendar.DAY_OF_YEAR, days);
        return cal;
    }

    public void runForDate(DoListener today, DoListener tomorrow, DoListener other) {
        if (this.sameDay(new CurrentDate())) {
            today.run();
        } else if (this.sameDay(new CurrentDate().addDay(1))) {
            tomorrow.run();
        } else {
            other.run();
        }
    }

    public String getRelativeDayName(Context context) {
        if (this.sameDay(new CurrentDate())) {
            return context.getResources().getString(R.string.today);
        } else if (this.sameDay(new CurrentDate().addDay(1))) {
            return context.getResources().getString(R.string.tomorrow);
        } else {
            return this.toString(SettingsApp.getLocale());
        }
    }


    @Override
    public String toString() {
        return getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, Locale.FRANCE) + " " +
                fillWithZeroBefore(getDay()) + "/" + fillWithZeroBefore(getMonth()) + "/" + getYear();
    }

    public String toString(Locale location) {
        return getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, location) + " " +
                fillWithZeroBefore(getDay()) + "/" + fillWithZeroBefore(getMonth()) + "/" + getYear();
    }
}
