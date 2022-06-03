package com.iutcalendar.alarm.condition;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.LinkedList;

public class AlarmConditionManager implements Serializable {

    private LinkedList<AlarmCondtion> listCondition;
    private LinkedList<AlarmConstraintLabel> listConstraint;

    private static AlarmConditionManager instance;

    public static AlarmConditionManager getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmConditionManager();
            instance.load(context);
        }
        return instance;
    }

    public AlarmConditionManager() {
        listCondition = new LinkedList<>();
        listConstraint = new LinkedList<>();
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
            if(!constraint.matchWith(event)){
                return false;
            }

        }
        return true;
    }


    public void load(Context context) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(FileGlobal.getFileConditions(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            Object obj = in.readObject();
            if (obj instanceof LinkedList) {
                listCondition = (LinkedList<AlarmCondtion>) obj;
            } else {
                Log.e("File", "personnal alarm error : wrong type, please delete your personnal alarm file");
            }

            Log.d("File", "file alarm loaded");
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("File", "class non trouvé pour alarmManager : " + e.getMessage());
        }


        try {
            stream = new FileInputStream(FileGlobal.getFileConstraints(context));
        } catch (FileNotFoundException e) {
            Log.w("File", FileGlobal.getFileConstraints(context).getName() + " doesn't existe.");
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            Object obj = in.readObject();
            if (obj instanceof LinkedList) {
                listConstraint = (LinkedList<AlarmConstraintLabel>) obj;
            } else {
                Log.e("File", "personnal alarm error : wrong type, please delete your personnal constraints alarm file");
            }

            Log.d("File", "file alarm loaded");
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("File", "class non trouvé pour alarmManager : " + e.getMessage());
        }

    }

    public void save(Context context) {
        FileGlobal.writeBinaryFile(listCondition, FileGlobal.getFileConditions(context));
        FileGlobal.writeBinaryFile(listConstraint, FileGlobal.getFileConstraints(context));
    }
}
