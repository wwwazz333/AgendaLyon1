package com.iutcalendar.alarm.constraint;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.LinkedList;

public class ConstraintAlarmManager implements Serializable {

    private LinkedList<ConstraintAlarm> listeConstraint;

    private static ConstraintAlarmManager instance;

    public static ConstraintAlarmManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConstraintAlarmManager();
            instance.load(context);
        }
        return instance;
    }

    public ConstraintAlarmManager() {
    }


    public void addConstraint(ConstraintAlarm constraintAlarm) {
        listeConstraint.add(constraintAlarm);
    }

    public void removeConstraint(int index) {
        listeConstraint.remove(index);
    }

    public LinkedList<ConstraintAlarm> getAllConstraint(){
        return listeConstraint;
    }


    public void load(Context context) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(FileGlobal.getFileConstraints(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            Object obj = in.readObject();
            if (obj instanceof LinkedList) {
                listeConstraint = (LinkedList<ConstraintAlarm>) obj;
            } else {
                Log.e("File", "personnal alarm error : wrong type, please delete your personnal alarm file");
            }

            Log.d("File", "file alarm loaded");
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("File", "class non trouvé pour personnalCalendrier : " + e.getMessage());
        }

    }

    public void save(Context context) {
        FileGlobal.writeBinaryFile(listeConstraint, FileGlobal.getFileConstraints(context));
    }
}
