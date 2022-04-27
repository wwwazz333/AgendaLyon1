package com.iutcalendar;

import java.io.File;

public class PathGlobal {
    private static PathGlobal instance = null;
    private String pathDownload;

    public PathGlobal() {

    }

    public static PathGlobal getInstance() {
        if (instance == null) {
            instance = new PathGlobal();
        }
        return instance;
    }

    public String getPathDownload() {
        return pathDownload;
    }

    public void setPathDownload(String pathDownload) {
        this.pathDownload = pathDownload;
    }

    public File getFileDownload() {
        return new File(getPathDownload() + "/savedCal.ics");
    }
}
