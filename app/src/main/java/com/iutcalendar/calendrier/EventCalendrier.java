package com.iutcalendar.calendrier;

import java.util.GregorianCalendar;

public class EventCalendrier implements Comparable<EventCalendrier> {
    private String summary;
    private String salle;
    private String description;
    private DateCalendrier debut;
    private int dureeH;
    private int dureeM;

    public EventCalendrier() {
        this.debut = null;
        this.dureeH = 0;
        this.dureeM = 0;
        this.summary = "";
        this.salle = "";
        this.description = "";
    }

    public EventCalendrier(DateCalendrier debut, int dureeH, int dureeM, String summary, String salle, String description) {
        this.debut = debut;
        this.dureeH = dureeH;
        this.dureeM = dureeM;
        this.summary = summary;
        this.salle = salle;
        this.description = description;
    }

    public DateCalendrier getDate() {
        return debut;
    }

    public String getSummary() {
        return summary;
    }

    public String getSalle() {
        return salle;
    }

    public String getDescription() {
        return description;
    }

    public DateCalendrier getDuree() {
        return new DateCalendrier(0, 0, 0, dureeH, dureeM);
    }

    @Override
    public EventCalendrier clone() throws CloneNotSupportedException {
        super.clone();
        return new EventCalendrier(debut, dureeH, dureeM, summary, salle, description);
    }

    public void parseLine(String str) {
        String[] splited = str.split(":");

        if (splited.length < 2) {
            return;
        }
        String title = splited[0];
        if (title.equals("DTSTART")) {//20220318T090000Z
            debut = getDateTime(splited[1]);
        } else if (title.equals("DTEND") && debut != null) {
            DateCalendrier fin = getDateTime(splited[1]);
            fin = fin.subTime(debut);
            dureeH = fin.get(GregorianCalendar.HOUR_OF_DAY);
            dureeM = fin.get(GregorianCalendar.MINUTE);
        } else if (title.equals("SUMMARY")) {
            summary = splited[1];
        } else if (title.equals("LOCATION")) {
            salle = splited[1];
        } else if (title.equals("DESCRIPTION")) {
            description = str.substring(str.indexOf(':') + 1).trim().replace("\\n", "\n").replaceAll("^\n*|\n*$", "");
        }
    }

    private DateCalendrier getDateTime(String str) {
        String[] dateTime = str.substring(0, str.indexOf('Z')).split("T");

        String date = dateTime[0];
        String time = dateTime[1];


        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6)) - 1;//pcq start à 0
        int day = Integer.parseInt(date.substring(6, 8));


        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2, 3));

        DateCalendrier dateCalendrier = new DateCalendrier(day, month, year, hour, minute);
        dateCalendrier.doZoneOffset();
        return dateCalendrier;
    }


    @Override
    public int compareTo(EventCalendrier eventCalendrier) {
        return getDate().compareTo(eventCalendrier.getDate());
    }

    @Override
    public String toString() {
        return debut.toString() + " " + dureeH + ":" + dureeM;
    }
}
