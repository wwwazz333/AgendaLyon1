package com.iutcalendar;

import java.io.File;

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
}
