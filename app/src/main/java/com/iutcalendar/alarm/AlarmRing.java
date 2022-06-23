package com.iutcalendar.alarm;

import android.app.TimePickerDialog;
import android.content.Context;
import com.iutcalendar.calendrier.CurrentDate;

import java.io.Serializable;

public class AlarmRing implements Serializable, Comparable<AlarmRing> {

    private final long timeInMillis;
    private boolean isActivate;

    public AlarmRing(long timeInMillis, boolean isActivate) {
        this.timeInMillis = timeInMillis;
        this.isActivate = isActivate;
    }

    public static void askTime(Context context, String title, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onTimeSetListener, 0, 0, true);
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    public static void askTime(Context context, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onTimeSetListener, 0, 0, true);
        timePickerDialog.show();
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public boolean isActivate() {
        return isActivate;
    }

    public void setActivate(boolean activate) {
        isActivate = activate;
    }

    private String createAlarmId() {
        return String.valueOf(getTimeInMillis());
    }

    public void setAlarm(Context context) {
        if (getTimeInMillis() >= System.currentTimeMillis()) {
            Alarm.setAlarm(context, getTimeInMillis(), createAlarmId(),
                    (isActivate()) ? Alarm.START : Alarm.NONE);
        }
    }

    public void cancelAlarm(Context context) {
        Alarm.cancelAlarm(context, createAlarmId());
    }

    public CurrentDate getDateTime() {
        CurrentDate dateRing = new CurrentDate();
        dateRing.setTimeInMillis(getTimeInMillis());
        return dateRing;
    }

    @Override
    public String toString() {
        return "AlarmRing{" +
                "timeInMillis=" + timeInMillis +
                ", isActivate=" + isActivate +
                '}';
    }

    @Override
    public int compareTo(AlarmRing alarmRing) {
        return Long.compare(this.getTimeInMillis(), alarmRing.getTimeInMillis());
    }
}
