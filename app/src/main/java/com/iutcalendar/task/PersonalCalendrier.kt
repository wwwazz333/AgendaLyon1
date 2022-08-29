package com.iutcalendar.task

import android.content.Context
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.FileGlobal
import java.util.*

class PersonalCalendrier {
    private var tasks: HashMap<String?, MutableList<Task>>

    init {
        tasks = HashMap()
    }

    private fun getCountTaskOf(linkedToUID: String?): Int {
        return getLinkedTask(linkedToUID).size
    }

    fun getCountTaskOf(linkedTo: EventCalendrier?): Int {
        return getCountTaskOf(linkedTo?.uid)
    }

    fun addLinkedTask(task: Task, linkedTo: EventCalendrier?) {
        // si aucune tache n'a déjà été ajouté à cette event alors créée la liste pour cette event
        tasks.computeIfAbsent(linkedTo?.uid) { LinkedList() }
        getLinkedTask(linkedTo).add(task)
    }

    fun getLinkedTask(linkedTo: EventCalendrier?): MutableList<Task> {
        return tasks.getOrDefault(linkedTo?.uid, LinkedList())
    }

    private fun getLinkedTask(linkedToUID: String?): MutableList<Task> {
        return tasks.getOrDefault(linkedToUID, LinkedList())
    }

    /**
     * Supprime-les tasks attacher à un event
     *
     * @param context     le context
     * @param linkedToUID L'UID de l'event à laquelle sont attaché les tasks
     * @param iterator    iterator qui permet de parcourir les tasks liée
     */
    fun removeAllLinkedTask(
        context: Context?,
        linkedToUID: String?,
        iterator: MutableIterator<String?>
    ) {
        for (task in getLinkedTask(linkedToUID)) {
            task.destroy(context)
        }
        iterator.remove()
    }


    fun remove(context: Context?, task: Task) {
        val taskList = tasks[task.linkedToUID]
        if (!taskList.isNullOrEmpty()) {
            task.destroy(context)
            taskList.remove(task)
        }
    }

    val keys: MutableSet<String?>
        get() = tasks.keys

    fun load(context: Context?) {
        tasks = HashMap()

        (FileGlobal.loadBinaryFile(FileGlobal.getFilePersonalTask(context)) as HashMap<String?, MutableList<Task>>?)?.let {
            tasks = it
        }
    }

    fun save(context: Context?) {
        FileGlobal.writeBinaryFile(tasks, FileGlobal.getFilePersonalTask(context))
    }

    companion object {
        private var instance: PersonalCalendrier? = null
        fun getInstance(context: Context?): PersonalCalendrier {
            if (instance == null) {
                instance = PersonalCalendrier().apply { load(context) }
            }
            return instance!!
        }
    }
}