package com.iutcalendar.calendrier;

import android.util.Log;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.GregorianCalendar;

public class DateCalendrier extends GregorianCalendar {

    private final boolean zoneOffsetAppliced = false;

    public DateCalendrier() {
        set(GregorianCalendar.SECOND, 0);
        set(GregorianCalendar.MILLISECOND, 0);
    }

    public DateCalendrier(int day, int month, int year, int hour, int minute) {
        this();

        setDay(day);
        setMonth(month);
        setYear(year);
        setHour(hour);
        setMinute(minute);
    }


    public DateCalendrier(DateCalendrier other) {
        this(other.getDay(), other.getMonth(), other.getYear(), other.getHour(), other.getMinute());
    }

    public static String fillWithZeroBefore(int n) {
        String s = String.valueOf(n);
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }

    public static String fillWithZeroAfter(int n) {
        String s = String.valueOf(n);
        if (s.length() < 2) {
            s += "0";
        }
        return s;
    }

    /**
     * le décalage est appliquer à la lecture du fichier uniquement
     * par la suite toutes les date aurons le bons décalage.
     */
    public void doZoneOffset() {
        int offset = ZonedDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), 0, 0, ZoneId.systemDefault()).get(ChronoField.OFFSET_SECONDS);
        setHour(getHour() + offset / 3600);
    }

    public static boolean isYearBisextil(int year) {
        DateCalendrier d = new DateCalendrier(1, 3, year, 12, 0);
        return d.getDayOfYear() == 61;
    }


    public int getDayOfYear() {
        return get(GregorianCalendar.DAY_OF_YEAR);
    }

    public void setDayOfYear(int day) {
        set(GregorianCalendar.DAY_OF_YEAR, day);
    }

    /**
     * @return num of day in month
     */
    public int getDay() {
        return get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * @param day num of day in month
     */
    public void setDay(int day) {
        set(GregorianCalendar.DAY_OF_MONTH, day);
    }

    public int getMonth() {
        return get(GregorianCalendar.MONTH) + 1;
    }

    public void setMonth(int month) {
        set(GregorianCalendar.MONTH, month - 1);
    }

    public int getYear() {
        return get(GregorianCalendar.YEAR);
    }

    public void setYear(int year) {
        set(GregorianCalendar.YEAR, year);
    }

    public int getHour() {
        return get(GregorianCalendar.HOUR_OF_DAY);
    }

    public void setHour(int hour) {
        set(GregorianCalendar.HOUR_OF_DAY, hour);
    }

    public int getMinute() {
        return get(GregorianCalendar.MINUTE);
    }

    public void setMinute(int minute) {
        set(GregorianCalendar.MINUTE, minute);
    }

    public DateCalendrier subTime(DateCalendrier o) {
        DateCalendrier diff = new DateCalendrier(this);

        diff.add(GregorianCalendar.HOUR_OF_DAY, -1 * o.getHour());
        diff.add(GregorianCalendar.MINUTE, -1 * o.getMinute());

        return diff;
    }

    public DateCalendrier addTime(DateCalendrier o) {
        DateCalendrier diff = new DateCalendrier(this);

        diff.add(GregorianCalendar.HOUR_OF_DAY, o.getHour());
        diff.add(GregorianCalendar.MINUTE, o.getMinute());

        return diff;
    }

    public String timeToString() {
        return getHour() + ":" + fillWithZeroBefore(getMinute());
    }

    public long getHourInMillis() {
        return DateCalendrier.getHourInMillis(getHour(), getMinute());
    }

    public static long getHourInMillis(int hour, int minute) {
        return hour * 3_600_000L + minute * 60_000L;
    }

    public void setHourWithMillis(long millis) {
        millis = millis / 1_000;//en seconds
        int minuteInSec = (int) (millis % 3600);
        millis -= minuteInSec;
        int hour = (int) (millis / (3600));
        setHour(hour);
        setMinute(minuteInSec / 60);
    }

    @Override
    public String toString() {
        return getDay() + "/" + getMonth() + "/" + getYear() + " " +
                getHour() + ":" + getMinute();
    }


    public int getNbrDayTo(DateCalendrier other) {
        int nbrDayInYear = (DateCalendrier.isYearBisextil(getYear())) ? 366 : 365;
        return (other.getYear() - getYear()) * nbrDayInYear + other.getDayOfYear() - getDayOfYear();
    }

    public boolean sameDay(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DateCalendrier other = (DateCalendrier) obj;

        return get(GregorianCalendar.DAY_OF_YEAR) == other.get(GregorianCalendar.DAY_OF_YEAR) && getYear() == other.getYear();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DateCalendrier other = (DateCalendrier) obj;
        return this.getTimeInMillis() == other.getTimeInMillis();
    }


    public static String timeLongToString(long timeMillis) {
        DateCalendrier t = new DateCalendrier();
        t.setHourWithMillis(timeMillis);
//        t.setTimeInMillis(timeMillis);
        return t.timeToString();
    }
}
