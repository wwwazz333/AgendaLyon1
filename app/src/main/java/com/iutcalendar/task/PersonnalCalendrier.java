package com.iutcalendar.task;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PersonnalCalendrier {

    private static PersonnalCalendrier instance;
    public HashMap<String, List<Task>> tasks;

    public PersonnalCalendrier() {
        this.tasks = new HashMap<>();
    }

    public static PersonnalCalendrier getInstance() {
        if (instance == null) {
            instance = new PersonnalCalendrier();
        }
        return instance;
    }

    public void addLinkedTask(Task task, EventCalendrier linkedTo) {
        if (tasks.get(linkedTo.getUID()) == null) {
            tasks.put(linkedTo.getUID(), new LinkedList<>());
        }
        getLinkedTask(linkedTo).add(task);
    }

    public List<Task> getLinkedTask(EventCalendrier linkedTo) {
        return this.tasks.getOrDefault(linkedTo.getUID(), new LinkedList<>());
    }

    public void remove(Task task) {
        this.tasks.get(task.getLinkedToUID()).remove(task);
    }


    public void load(Context context) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(FileGlobal.getFilePersonnalTask(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            tasks = (HashMap) in.readObject();
            Log.d("File", "file task loaded");
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("File", "class non trouvé pour personnalCalendrier : " + e.getMessage());
        }

    }

    public void save(Context context) {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(FileGlobal.getFilePersonnalTask(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(tasks);
            out.close();
            stream.close();
            Log.d("File", "fileTask saved");
        } catch (IOException e) {
            Log.e("File", "couldn't write in fileTask");
        }
    }
}
