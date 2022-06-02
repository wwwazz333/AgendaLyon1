package com.iutcalendar.alarm.condition;

import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel;
import com.iutcalendar.calendrier.EventCalendrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class AlarmCondtion implements Serializable {

    private long beging, end, alarmAt;//les borne sont comprise (hours in millis)

    /**
     * contien les jour ou l'alarm s'active
     *
     * @exmple GregorianCalendar.MONDAY
     */
    private final ArrayList<Integer> daysEnabled;


    public AlarmCondtion(long beging, long end, long alarmAt, ArrayList<Integer> daysEnabled) {
        this.beging = beging;
        this.end = end;
        this.alarmAt = alarmAt;
        this.daysEnabled = daysEnabled;
    }


    public AlarmCondtion(long beging, long end, long alarmAt) {
        this(beging, end, alarmAt, new ArrayList<>());
        daysEnabled.add(GregorianCalendar.MONDAY);
        daysEnabled.add(GregorianCalendar.TUESDAY);
        daysEnabled.add(GregorianCalendar.WEDNESDAY);
        daysEnabled.add(GregorianCalendar.THURSDAY);
        daysEnabled.add(GregorianCalendar.FRIDAY);
    }

    public long getAlarmAt() {
        return alarmAt;
    }

    public long getBeging() {
        return beging;
    }

    public long getEnd() {
        return end;
    }

    public void setBeging(long beging) {
        this.beging = beging;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setAlarmAt(long alarmAt) {
        this.alarmAt = alarmAt;
    }

    public ArrayList<Integer> getDaysEnabled() {
        return daysEnabled;
    }


    public boolean isApplicableTo(EventCalendrier event) {
        long dateEventMillis = event.getDate().getHourInMillis();
        return beging <= dateEventMillis && dateEventMillis <= end &&
                daysEnabled.contains(event.getDate().get(GregorianCalendar.DAY_OF_WEEK));
    }
}
