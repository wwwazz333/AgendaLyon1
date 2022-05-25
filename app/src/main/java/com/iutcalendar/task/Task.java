package com.iutcalendar.task;

import android.content.Context;
import com.iutcalendar.alarm.Alarm;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    //TODO implémenté un rappel
    private final String txt;

    private final String linkedToUID;

    private final boolean isAlarm;


    public Task(String name, String linkedToUID, boolean isAlarm) {
        this.txt = name;
        this.linkedToUID = linkedToUID;
        this.isAlarm = isAlarm;
    }

    public Task(String name, String linkedToUID) {
        this(name, linkedToUID, false);
    }

    public String getLinkedToUID() {
        return linkedToUID;
    }

    public String getTxt() {
        return txt;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public void destroy(Context context) {
        Alarm.cancelAlarm(context, getLinkedToUID());
    }

    @Override
    public String toString() {
        return txt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(txt, task.txt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txt);
    }
}
