package com.iutcalendar.task

import android.content.Context
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.FileGlobal
import java.util.*

class PersonalCalendrier {
    private var tasks: HashMap<String?, MutableList<Task>>?

    init {
        tasks = HashMap()
    }

    private fun hasAlarm(linkedToUID: String?): Boolean {
        for (task in getLinkedTask(linkedToUID)) {
            if (task.isAlarm) {
                return true
            }
        }
        return false
    }

    private fun getCountTaskOf(linkedToUID: String?): Int {
        return getLinkedTask(linkedToUID).size - if (hasAlarm(linkedToUID)) 1 else 0
    }

    fun getCountTaskOf(linkedTo: EventCalendrier?): Int {
        return getCountTaskOf(linkedTo?.uid)
    }

    fun addLinkedTask(task: Task, linkedTo: EventCalendrier?) {
        // si aucune tache n'a déjà été ajouté à cette event alors créée la liste pour cette event
        tasks!!.computeIfAbsent(linkedTo?.uid) { LinkedList() }
        getLinkedTask(linkedTo).add(task)
    }

    fun getLinkedTask(linkedTo: EventCalendrier?): MutableList<Task> {
        return tasks!!.getOrDefault(linkedTo?.uid, LinkedList())
    }

    private fun getLinkedTask(linkedToUID: String?): MutableList<Task> {
        return tasks!!.getOrDefault(linkedToUID, LinkedList())
    }

    /**
     * Supprime-les tasks attacher à un event
     *
     * @param context     le context
     * @param linkedToUID L'UID de l'event auquelle sont attaché les tasks
     * @param iterator    iterator qui permet de parcourir les tasks liée
     */
    fun removeAllLinkedTask(context: Context?, linkedToUID: String?, iterator: MutableIterator<String?>) {
        for (task in getLinkedTask(linkedToUID)) {
            task.destroy(context)
        }
        iterator.remove()
    }

    fun removeAllAlarmOf(context: Context?, linkedToUID: String?) {
        val it = getLinkedTask(linkedToUID).iterator()
        while (it.hasNext()) {
            val t = it.next()
            if (t.isAlarm) {
                t.destroy(context)
                it.remove()
            }
        }
        for (task in getLinkedTask(linkedToUID)) {
            task.destroy(context)
        }
    }

    fun getAlarmOf(linkedToUID: String?): Task? {
        for (task in getLinkedTask(linkedToUID)) {
            if (task.isAlarm) {
                return task
            }
        }
        return null
    }

    fun remove(context: Context?, task: Task) {
        val taskList = tasks!![task.linkedToUID]
        if (!taskList.isNullOrEmpty()) {
            task.destroy(context)
            taskList.remove(task)
        }
    }

    val keys: MutableSet<String?>
        get() = tasks!!.keys

    fun load(context: Context?) {
        tasks = FileGlobal.loadBinaryFile(FileGlobal.getFilePersonalTask(context))
        if (tasks == null) {
            tasks = HashMap()
        }
    }

    fun save(context: Context?) {
        FileGlobal.writeBinaryFile(tasks, FileGlobal.getFilePersonalTask(context))
    }

    companion object {
        private var instance: PersonalCalendrier? = null
        fun getInstance(context: Context?): PersonalCalendrier? {
            if (instance == null) {
                instance = PersonalCalendrier()
                instance!!.load(context)
            }
            return instance
        }
    }
}