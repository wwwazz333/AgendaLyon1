package com.iutcalendar.calendrier;

import android.content.Context;
import com.univlyon1.tools.agenda.R;
import com.iutcalendar.data.DataGlobal;
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

    public void set(DateCalendrier other) {
        setYear(other.getYear());
        setDay(other.getDay());
        setMonth(other.getMonth());
        setHour(other.getHour());
        setMinute(other.getMinute());
    }

    /**
     * @param day lun 0, mar 1, ..., dim 6
     * @return la date du joure de la semaine
     */
    public CurrentDate getDateOfDayOfWeek(int day) {
        return addDay(day - getPosDayOfWeek());
    }

    public int getPosDayOfWeek() {
        for (int i = 0; i < DataGlobal.DAYS_OF_WEEK.length; i++) {
            if (get(GregorianCalendar.DAY_OF_WEEK) == DataGlobal.DAYS_OF_WEEK[i]) {
                return i;
            }
        }
        return -1;//normalement impossible
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


    public boolean sameWeek(CurrentDate other) {
        if (getYear() != other.getYear()) {
            return false;
        }
        return other.getDayOfYear() >= getDateOfDayOfWeek(0).getDayOfYear() && other.getDayOfYear() <= getDateOfDayOfWeek(6).getDayOfYear();
    }


    public String toString(Locale location) {
        if (location.equals(new Locale("en"))) {
            return getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, location) + " " +
                    fillWithZeroBefore(getMonth()) + "/" + fillWithZeroBefore(getDay()) + "/" + getYear();
        }
        return getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, location) + " " +
                fillWithZeroBefore(getDay()) + "/" + fillWithZeroBefore(getMonth()) + "/" + getYear();

    }
}
