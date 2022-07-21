package com.iutcalendar.filedownload;

import com.iutcalendar.calendrier.InputStreamFileException;

public class WrongURLException extends InputStreamFileException {
    public WrongURLException() {
        super("mauvais URL");
    }
}
