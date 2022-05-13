package com.iutcalendar.task;

import java.io.Serializable;

public class Task implements Serializable {
    private final String name;
    private final String description;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " : " + description;
    }
}
