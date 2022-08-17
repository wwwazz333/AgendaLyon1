package com.iutcalendar.alarm.condition

import android.content.Context
import android.util.Log
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.FileGlobal
import java.io.Serializable
import java.util.*

class AlarmConditionManager : Serializable {
    var allConditions: LinkedList<AlarmCondition>?
        private set
    var allConstraints: LinkedList<AlarmConstraintLabel>?
        private set

    init {
        allConditions = LinkedList()
        allConstraints = LinkedList()
    }

    fun addCondition(constraintAlarm: AlarmCondition) {
        allConditions!!.add(constraintAlarm)
    }

    fun removeCondition(index: Int) {
        allConditions!!.removeAt(index)
    }

    fun addConstraint(alarmConstraintLabel: AlarmConstraintLabel) {
        allConstraints!!.add(alarmConstraintLabel)
    }

    fun removeConstraint(index: Int) {
        allConstraints!!.removeAt(index)
    }

    fun removeConstraint(alarmConstraintLabel: AlarmConstraintLabel) {
        allConstraints!!.remove(alarmConstraintLabel)
    }

    fun matchConstraints(event: EventCalendrier?): Boolean {
        //check si respect toutes les contraintes label
        for (constraint in allConstraints!!) {
            if (event != null && !constraint.matchWith(event)) {
                return false
            }
        }
        return true
    }

    fun load(context: Context?) {
        allConditions = FileGlobal.loadBinaryFile(FileGlobal.getFileConditions(context))
        if (allConditions == null) {
            Log.i("Condition", "listCondition is null")
            allConditions = LinkedList()
        }
        allConstraints = FileGlobal.loadBinaryFile(FileGlobal.getFileConstraints(context))
        if (allConstraints == null) {
            Log.i("Constraint", "listConstraint is null")
            allConstraints = LinkedList()
        }
    }

    fun save(context: Context?) {
        FileGlobal.writeBinaryFile(allConditions, FileGlobal.getFileConditions(context))
        FileGlobal.writeBinaryFile(allConstraints, FileGlobal.getFileConstraints(context))
    }

    companion object {
        private var instance: AlarmConditionManager? = null
        fun getInstance(context: Context?): AlarmConditionManager {
            if (instance == null) {
                instance = AlarmConditionManager()
                instance!!.load(context)
            }
            return instance!!
        }
    }
}