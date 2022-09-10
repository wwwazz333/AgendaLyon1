package com.iutcalendar.event.changement

import android.content.Context
import android.util.Log
import com.iutcalendar.data.FileGlobal

class EventChangementManager {
    var changementList: MutableList<EventChangement?> = mutableListOf()
        private set

    fun load(context: Context) {
        val tempList = FileGlobal.loadBinaryFile<MutableList<EventChangement?>>(FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT))
        changementList = tempList ?: mutableListOf()
        Log.d("History", "Load : $changementList")
    }

    fun save(context: Context) {
        Log.d("History", "Save : $changementList")
        Log.d("History", "success : " + FileGlobal.writeBinaryFile(changementList, FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT)))
        load(context)
    }

    companion object {
        private var instance: EventChangementManager? = null
        fun getInstance(context: Context): EventChangementManager {
            if (instance == null) {
                instance = EventChangementManager()
                instance!!.load(context)
            }
            return instance as EventChangementManager
        }
    }
}