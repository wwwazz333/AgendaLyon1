package com.iutcalendar.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileGlobal {
    public static final String nameFileSave = "savedCal.ics";
    public static final String nameFilePersonnalTask = "personalTasks.dat";
    public static final String nameFilePersonnalAlarm = "personalAlarms.dat";


    public static String getPathDownladDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static File getFileDownload(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFileSave);
    }

    public static File getFilePersonnalTask(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFilePersonnalTask);
    }

    public static File getFilePersonnalAlarm(Context context) {
        return new File(getPathDownladDir(context) + "/" + nameFilePersonnalAlarm);
    }

    public static String readFile(File file) {
        StringBuilder build = new StringBuilder();

        Path path = Paths.get(file.getAbsolutePath());

        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path);
        } catch (FileNotFoundException | NoSuchFileException e) {
            return "";
        } catch (IOException e) {
            Log.e("File", e.getMessage());
            return "";
        }
        try {
            String str;
            while ((str = reader.readLine()) != null) {
                build.append(str).append('\n');
            }
        } catch (IOException e) {
            Log.e("File", e.getMessage());
            return "";
        }

        return build.toString();
    }
}


