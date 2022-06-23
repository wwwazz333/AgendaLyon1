package com.iutcalendar.task;

import android.content.Context;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.util.*;


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

    /**
     * supprime les tasks attacher à un evenement
     *
     * @param context     le context
     * @param linkedToUID L'UID de l'event auquelle sont attacher les tasks
     * @param iterator    l'iterator qui permet de parcourir les tasks liée
     */
    public void removeAllLinkedTask(Context context, String linkedToUID, Iterator<String> iterator) {
        for (Task task : getLinkedTask(linkedToUID)) {
            task.destroy(context);
        }
        iterator.remove();
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
        tasks = FileGlobal.loadBinaryFile(FileGlobal.getFilePersonnalTask(context));
        if (tasks == null) {
            tasks = new HashMap<>();
        }
    }

    public void save(Context context) {
        FileGlobal.writeBinaryFile(tasks, FileGlobal.getFilePersonnalTask(context));
    }
}
