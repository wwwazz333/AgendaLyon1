package com.iutcalendar.alarm;

import android.content.Context;

import java.io.Serializable;

public class AlarmRing implements Serializable {

    private final long timeInMillis;
    private final boolean isActivate;

    public AlarmRing(long timeInMillis, boolean isActivate) {
        this.timeInMillis = timeInMillis;
        this.isActivate = isActivate;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public boolean isActivate() {
        return isActivate;
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

    @Override
    public String toString() {
        return "AlarmRing{" +
                "timeInMillis=" + timeInMillis +
                ", isActivate=" + isActivate +
                '}';
    }
}
