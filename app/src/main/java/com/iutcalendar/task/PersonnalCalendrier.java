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
        // si aucune tache n'a déjà été ajouté à cette event alors crès la liste pour cette event
        tasks.computeIfAbsent(linkedTo.getUID(), k -> new LinkedList<>());

        getLinkedTask(linkedTo).add(task);
    }

    public List<Task> getLinkedTask(EventCalendrier linkedTo) {
        return this.tasks.getOrDefault(linkedTo.getUID(), new LinkedList<>());
    }

    public void remove(Task task) {
        List<Task> taskList = this.tasks.get(task.getLinkedToUID());
        if (taskList != null && !taskList.isEmpty())
            taskList.remove(task);
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
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                tasks = (HashMap<String, List<Task>>) obj;
            } else {
                Log.e("File", "personnal task error : wrong type, please delete your personnal task file");
            }

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
