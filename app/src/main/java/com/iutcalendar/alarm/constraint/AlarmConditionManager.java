package com.iutcalendar.alarm.constraint;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.LinkedList;

public class AlarmConditionManager implements Serializable {

    private LinkedList<AlarmCondtion> listeConstraint;

    private static AlarmConditionManager instance;

    public static AlarmConditionManager getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmConditionManager();
            instance.load(context);
        }
        return instance;
    }

    public AlarmConditionManager() {
        listeConstraint = new LinkedList<>();
    }


    public void addConstraint(AlarmCondtion constraintAlarm) {
        listeConstraint.add(constraintAlarm);
    }

    public void removeConstraint(int index) {
        listeConstraint.remove(index);
    }

    public LinkedList<AlarmCondtion> getAllConstraint(){
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
                listeConstraint = (LinkedList<AlarmCondtion>) obj;
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
