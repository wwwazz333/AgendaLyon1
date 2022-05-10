package com.iutcalendar.data;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PathGlobal {
    public static final String nameFileSave = "savedCal.ics";
    private static String pathDownload;

    public static String getPathDownload() {
        return PathGlobal.pathDownload;
    }

    public static void setPathDownload(String pathDownload) {
        PathGlobal.pathDownload = pathDownload;
    }

    public static File getFileDownload() {
        return new File(PathGlobal.pathDownload + "/" + nameFileSave);
    }

    public static String readFile(File file) {
        String str = null;
        try {
            str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            Log.d("Error", e.getMessage());
            str = "";
        }

        return str;
    }
}


