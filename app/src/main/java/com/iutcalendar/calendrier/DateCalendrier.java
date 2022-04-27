package com.iutcalendar.calendrier;

import java.util.GregorianCalendar;
import java.util.Locale;

public class DateCalendrier extends GregorianCalendar {

    private boolean zoneOffsetAppliced = false;
    private int summerOffset = 1;


    public DateCalendrier(){
        super(Locale.FRANCE);
        set(GregorianCalendar.SECOND, 0);
    }
    public DateCalendrier(int day, int month, int year, int hour, int minute) {
        super(Locale.FRANCE);

        setDay(day);
        setMonth(month);
        setYear(year);
        setHour(hour);
        setMinute(minute);
        set(GregorianCalendar.SECOND, 0);
    }

    public void doZoneOffset() {
        if (!zoneOffsetAppliced) {
            setHour(getHour() + getZoneOffset() + summerOffset);
        }
        zoneOffsetAppliced = true;
    }

    public DateCalendrier(DateCalendrier other) {
        super(Locale.FRANCE);

        setDay(other.getDay());
        setMonth(other.getMonth());
        setYear(other.getYear());
        setHour(other.getHour());
        setMinute(other.getMinute());
        set(GregorianCalendar.SECOND, 0);

        this.zoneOffsetAppliced = other.zoneOffsetAppliced;
    }


    public int getZoneOffset() {
        return get(GregorianCalendar.ZONE_OFFSET) / (60 * 60 * 1000);
    }

    /**
     *
     * @return num of day in month
     */
    public int getDay() {
        return get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     *
     * @param day num of day in month
     */
    public void setDay(int day) {
        set(GregorianCalendar.DAY_OF_MONTH, day);
    }

    public int getMonth() {
        return get(GregorianCalendar.MONTH);
    }

    public void setMonth(int month) {
        set(GregorianCalendar.MONTH, month);
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
    public DateCalendrier addTime(DateCalendrier o){
        DateCalendrier diff = new DateCalendrier(this);

        diff.add(GregorianCalendar.HOUR_OF_DAY, 1 * o.getHour());
        diff.add(GregorianCalendar.MINUTE, 1 * o.getMinute());

        return diff;
    }

    public static String fillWithZeroBefor(int n){
        String s = String.valueOf(n);
        if(s.length() < 2){
            s = "0" + s;
        }
        return s;
    }

    @Override
    public String toString() {
        return getDay() + "/" + String.valueOf(getMonth()+1) + "/" + getYear() + " " +
                getHour() + ":" + getMinute();
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
        return getDay() == other.getDay() && getMonth() == other.getMonth() && getYear() == other.getYear()
                && getHour() == other.getHour() && getMinute() == other.getMinute();
    }
}
