package com.iutcalendar.alarm.condition;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.Serializable;
import java.util.LinkedList;

public class AlarmConditionManager implements Serializable {

    private static AlarmConditionManager instance;
    private LinkedList<AlarmCondtion> listCondition;
    private LinkedList<AlarmConstraintLabel> listConstraint;

    public AlarmConditionManager() {
        listCondition = new LinkedList<>();
        listConstraint = new LinkedList<>();
    }

    public static AlarmConditionManager getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmConditionManager();
            instance.load(context);
        }
        return instance;
    }

    public void addCondition(AlarmCondtion constraintAlarm) {
        listCondition.add(constraintAlarm);
    }

    public void removeCondition(int index) {
        listCondition.remove(index);
    }

    public LinkedList<AlarmCondtion> getAllConditions() {
        return listCondition;
    }

    public void addConstraint(AlarmConstraintLabel alarmConstraintLabel) {
        listConstraint.add(alarmConstraintLabel);
    }

    public void removeConstraint(int index) {
        listConstraint.remove(index);
    }

    public void removeConstraint(AlarmConstraintLabel alarmConstraintLabel) {
        listConstraint.remove(alarmConstraintLabel);
    }

    public LinkedList<AlarmConstraintLabel> getAllConstraints() {
        return listConstraint;
    }

    public boolean matchConstraints(EventCalendrier event) {
        //check si respect toutes les contraintes label
        for (AlarmConstraintLabel constraint : getAllConstraints()) {
            if (!constraint.matchWith(event)) {
                return false;
            }

        }
        return true;
    }


    public void load(Context context) {
        listCondition = FileGlobal.loadBinaryFile(FileGlobal.getFileConditions(context));
        if (listCondition == null) {
            Log.i("Condition", "listCondition is null");
            listCondition = new LinkedList<>();
        }

        listConstraint = FileGlobal.loadBinaryFile(FileGlobal.getFileConstraints(context));

        if (listConstraint == null) {
            Log.i("Constraint", "listConstraint is null");
            listConstraint = new LinkedList<>();
        }
    }

    public void save(Context context) {
        FileGlobal.writeBinaryFile(listCondition, FileGlobal.getFileConditions(context));
        FileGlobal.writeBinaryFile(listConstraint, FileGlobal.getFileConstraints(context));
    }
}
