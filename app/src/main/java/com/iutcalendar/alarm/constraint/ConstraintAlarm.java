package com.iutcalendar.alarm.constraint;

import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class ConstraintAlarm implements Serializable {

    private long beging, end, alarmAt;//les borne sont comprise

    /**
     * contien les jour ou l'alarm s'active
     *
     * @exmple GregorianCalendar.MONDAY
     */
    private ArrayList<Integer> daysEnabled;

    private ArrayList<ConstraintContentAlarm> constraintLabels;

    public ConstraintAlarm(long beging, long end, long alarmAt, ArrayList<Integer> daysEnabled, ArrayList<ConstraintContentAlarm> constraintLabels) {
        this.beging = beging;
        this.end = end;
        this.alarmAt = alarmAt;
        this.daysEnabled = daysEnabled;
        this.constraintLabels = constraintLabels;
    }

    public ConstraintAlarm(long beging, long end, long alarmAt, ArrayList<Integer> daysEnabled) {
        this(beging, end, alarmAt, daysEnabled, new ArrayList<>());
    }

    public void addConstraint(ConstraintContentAlarm constraint) {
        constraintLabels.add(constraint);
    }

    public void removeConstraint(int index) {
        Iterator<ConstraintContentAlarm> it = constraintLabels.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            if (i == index) {
                it.remove();
                return;
            }
            i++;
        }
    }

    public boolean isApplicableTo(EventCalendrier event) {
        long dateEventMillis = event.getDate().getTimeInMillis();
        if (beging <= dateEventMillis && dateEventMillis <= end &&
                daysEnabled.contains(event.getDate().get(GregorianCalendar.DAY_OF_WEEK))) {
            //check si respect contrainte label
            for (ConstraintContentAlarm constraint : constraintLabels) {
                switch (constraint.getTypeDeContraint()) {
                    case MUST_CONTAIN:
                        if (!event.getSummary().matches(constraint.getContraintRegex())) {
                            return false;
                        }
                        break;
                    case MUST_NOT_CONTAIN:
                        if (event.getSummary().matches(constraint.getContraintRegex())) {
                            return false;
                        }
                        break;
                    case MUST_BE_EXACTLY:
                        if (!event.getSummary().equals(constraint.getContraintRegex())) {
                            return false;
                        }
                        break;
                    case MUST_NOT_BE_EXACTLY:
                        if (event.getSummary().equals(constraint.getContraintRegex())) {
                            return false;
                        }
                        break;
                }
            }
            return true;
        }
        return false;
    }
}
