package com.iutcalendar.alarm;

import android.content.Context;
import android.util.Log;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.data.FileGlobal;

import java.io.*;
import java.util.*;

public class PersonnalAlarmManager implements Serializable {

    private static PersonnalAlarmManager instance;
    private HashMap<String, List<AlarmRing>> alarms;

    public static PersonnalAlarmManager getInstance(Context context) {
        if (instance == null) {
            instance = new PersonnalAlarmManager();
            instance.load(context);
            Log.d("Alarm", "------------LOAD-------------");
            for (List<AlarmRing> l : instance.alarms.values()) {
                for (AlarmRing a : l) {
                    Log.d("Alarm", a.toString());
                }
            }
            Log.d("Alarm", "-------------------------");
        }
        return instance;
    }

    public PersonnalAlarmManager() {
        alarms = new HashMap<>();
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
     * @param day
     * @return le timeMillis de l'alarm correspondant à l'event ou -1 si pas d'alarm
     */
    public List<AlarmRing> get(DateCalendrier day) {
        return alarms.getOrDefault(createDayId(day), new LinkedList<AlarmRing>());
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

    public LinkedList<AlarmRing> getAllAlarmToList() {
        LinkedList<AlarmRing> liste = new LinkedList<>();
        for (List<AlarmRing> listAlarm : alarms.values()) {
            liste.addAll(listAlarm);
        }
        return liste;
    }

    public void load(Context context) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(FileGlobal.getFilePersonnalAlarm(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(stream);
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                alarms = (HashMap<String, List<AlarmRing>>) obj;
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
        Log.d("Alarm", "----------SAVE---------------");
        for (List<AlarmRing> l : alarms.values()) {
            for (AlarmRing a : l) {
                Log.d("Alarm", a.toString());
            }
        }
        Log.d("Alarm", "-------------------------");
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(FileGlobal.getFilePersonnalAlarm(context));
        } catch (FileNotFoundException e) {
            Log.w("File", "fileTask doesn't existe.");
            return;
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(alarms);
            out.close();
            stream.close();
            Log.d("File", "file alarm saved");
        } catch (IOException e) {
            Log.e("File", "couldn't write in file alarm");
        }
    }
}
