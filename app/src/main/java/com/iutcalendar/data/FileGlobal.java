package com.iutcalendar.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileGlobal {
    public static final String nameFileSave = "savedCal.ics";
    public static final String nameFilePersonnalTask = "personalTasks.dat";

    public static File getFileDownload(Context context) {
        return new File(DataGlobal.getSavedPathDownloadFile(context) + "/" + nameFileSave);
    }
    public static File getFilePersonnalTask(Context context) {
        return new File(DataGlobal.getSavedPathDownloadFile(context) + "/" + nameFilePersonnalTask);
    }

    public static String readFile(File file) {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            Log.d("Error", e.getMessage());
            str = "";
        }

        return str;
    }
}


