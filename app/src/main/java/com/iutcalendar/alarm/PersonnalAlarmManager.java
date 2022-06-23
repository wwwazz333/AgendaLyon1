package com.iutcalendar.alarm;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.Serializable;
import java.util.*;

public class PersonnalAlarmManager implements Serializable {

    private static PersonnalAlarmManager instance;
    private HashMap<String, List<AlarmRing>> alarms;

    public PersonnalAlarmManager() {
        alarms = new HashMap<>();
    }

    public static PersonnalAlarmManager getInstance(Context context) {
        if (instance == null) {
            instance = new PersonnalAlarmManager();
            instance.load(context);
        }
        return instance;
    }

    public static String createDayId(DateCalendrier day) {
        return day.getYear() + ":" + day.get(GregorianCalendar.DAY_OF_YEAR);
    }

    public boolean add(DateCalendrier day, AlarmRing alarmRing) {
        Log.d("Alarm", alarmRing.toString());
        if (alarmRing.getTimeInMillis() > System.currentTimeMillis()) {
            List<AlarmRing> alarmOfDay = alarms.getOrDefault(createDayId(day), new LinkedList<>());
            if (alarmOfDay.isEmpty()) {
                alarms.put(createDayId(day), alarmOfDay);
            }
            alarmOfDay.add(alarmRing);
            return true;
        }
        return false;
    }

    public void setUpAlarms(Context context) {
        for (Map.Entry<String, List<AlarmRing>> entry : alarms.entrySet()) {
            for (AlarmRing alarm : entry.getValue()) {
                alarm.setAlarm(context);
            }
        }
    }

    /**
     * @param day le jour où doivent sonné les alarms qu'on retourne
     * @return le timeMillis de l'alarm correspondant à l'event ou -1 si pas d'alarm
     */
    public List<AlarmRing> get(DateCalendrier day) {
        return alarms.getOrDefault(createDayId(day), new LinkedList<>());
    }

    private void removeAlarm(Context context, AlarmRing alarmRing, Iterator<AlarmRing> it) {
        alarmRing.cancelAlarm(context);
        it.remove();
    }

    public void remove(Context context, AlarmRing alarmRing) {
        for (List<AlarmRing> list : alarms.values()) {
            Iterator<AlarmRing> it = list.iterator();
            while (it.hasNext()) {
                AlarmRing alarm = it.next();
                if (alarm.equals(alarmRing)) {
                    removeAlarm(context, alarm, it);
                }
            }
        }
    }

    public void removeForDay(Context context, DateCalendrier day) {
        Iterator<AlarmRing> it = get(day).iterator();
        while (it.hasNext()) {
            removeAlarm(context, it.next(), it);
        }
    }

    public void removeUseless(Context context) {
        for (Map.Entry<String, List<AlarmRing>> entry : alarms.entrySet()) {
            Iterator<AlarmRing> it = entry.getValue().iterator();
            while (it.hasNext()) {
                AlarmRing alarmRing = it.next();
                if (alarmRing.getTimeInMillis() < 0 || alarmRing.getTimeInMillis() < System.currentTimeMillis()) {
                    removeAlarm(context, alarmRing, it);
                }
            }
        }
    }

    public ArrayList<AlarmRing> getAllAlarmToList() {
        ArrayList<AlarmRing> liste = new ArrayList<>();
        for (List<AlarmRing> listAlarm : alarms.values()) {
            liste.addAll(listAlarm);
        }
        Collections.sort(liste);
        return liste;
    }

    public void load(Context context) {
        alarms = FileGlobal.loadBinaryFile(FileGlobal.getFilePersonnalAlarm(context));
        if (alarms == null) {
            alarms = new HashMap<>();
        }
    }

    public void save(Context context) {
        FileGlobal.writeBinaryFile(alarms, FileGlobal.getFilePersonnalAlarm(context));
    }
}
