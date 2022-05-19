package com.iutcalendar.task;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    //TODO implémenté un rappel
    private final String txt;

    private final String linkedToUID;


    public Task(String name, String linkedToUID) {
        this.txt = name;
        this.linkedToUID = linkedToUID;
    }

    public String getLinkedToUID() {
        return linkedToUID;
    }

    public String getTxt() {
        return txt;
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
