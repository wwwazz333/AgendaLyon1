package com.iutcalendar.event.changement;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.data.FileGlobal;

import java.util.LinkedList;
import java.util.List;

public class EventChangmentManager {
    private static EventChangmentManager instance;
    private List<EventChangment> changmentList;

    public static EventChangmentManager getInstance(Context context) {
        if (instance == null) {
            instance = new EventChangmentManager();
            instance.load(context);
        }
        return instance;
    }

    public List<EventChangment> getChangmentList() {
        return changmentList;
    }

    public void load(Context context) {
        changmentList = FileGlobal.loadBinaryFile(FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT));
        if (changmentList == null) {
            changmentList = new LinkedList<>();
        }
        Log.d("History", "Load : " + changmentList);

    }

    public void save(Context context) {
        Log.d("History", "Save : " + changmentList.toString());
        Log.d("History", "succes : " + FileGlobal.writeBinaryFile(changmentList, FileGlobal.getFile(context, FileGlobal.CHANGEMENT_EVENT)));
        load(context);
    }
}
