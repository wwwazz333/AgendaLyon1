package com.iutcalendar.task;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class PersonnalCalendrier {

    private static PersonnalCalendrier instance;
    public HashMap<String, List<Task>> tasks;

    public PersonnalCalendrier() {
        this.tasks = new HashMap<>();
    }

    public static PersonnalCalendrier getInstance(Context context) {
        if (instance == null) {
            instance = new PersonnalCalendrier();
            instance.load(context);
        }
        return instance;
    }

    public boolean hasAlarm(String linkedToUID) {
        for (Task task : getLinkedTask(linkedToUID)) {
            if (task.isAlarm()) {
                return true;
            }
        }
        return false;
    }

    public int getCountTaskOf(String linkedToUID) {
        return getLinkedTask(linkedToUID).size() - (hasAlarm(linkedToUID) ? 1 : 0);
    }

    public int getCountTaskOf(EventCalendrier linkedTo) {
        return getCountTaskOf(linkedTo.getUID());
    }

    public void addLinkedTask(Task task, EventCalendrier linkedTo) {
        // si aucune tache n'a déjà été ajouté à cette event alors crès la liste pour cette event
        tasks.computeIfAbsent(linkedTo.getUID(), k -> new LinkedList<>());

        getLinkedTask(linkedTo).add(task);
    }

    public List<Task> getLinkedTask(EventCalendrier linkedTo) {
        return this.tasks.getOrDefault(linkedTo.getUID(), new LinkedList<>());
    }

    public List<Task> getLinkedTask(String linkedToUID) {
        return this.tasks.getOrDefault(linkedToUID, new LinkedList<>());
    }

    public void removeAllLinkedTask(Context context, String linkedToUID) {
        for (Task task : getLinkedTask(linkedToUID)) {
            task.destroy(context);
        }
        this.tasks.remove(linkedToUID);
    }

    public void removeAllAlarmOf(Context context, String linkedToUID) {
        java.util.Iterator<Task> it = getLinkedTask(linkedToUID).iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.isAlarm()) {
                t.destroy(context);
                it.remove();
            }
        }
        for (Task task : getLinkedTask(linkedToUID)) {
            task.destroy(context);
        }
    }

    public Task getAlarmOf(String linkedToUID) {
        for (Task task : getLinkedTask(linkedToUID)) {
            if (task.isAlarm()) {
                return task;
            }
        }
        return null;
    }

    public void remove(Context context, Task task) {
        List<Task> taskList = this.tasks.get(task.getLinkedToUID());
        if (taskList != null && !taskList.isEmpty()) {
            task.destroy(context);
            taskList.remove(task);
        }
    }

    public Set<String> getKeys() {
        return this.tasks.keySet();
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
