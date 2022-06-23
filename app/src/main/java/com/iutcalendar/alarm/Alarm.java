package com.iutcalendar.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;
import com.iutcalendar.alarm.condition.AlarmConditionManager;
import com.iutcalendar.alarm.condition.AlarmCondtion;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.notification.Notif;
import com.univlyon1.tools.agenda.R;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Alarm extends BroadcastReceiver {
    public final static int NONE = 0, STOP = 1, START = 2;

    private static Ringtone ring;

    /**
     * cancel l'alarm précédente et en place une nouvelle
     *
     * @param context le context
     * @param time    heure de l'alarm (en ms)
     */
    public static void setAlarm(Context context, long time) {
        Intent ai = new Intent(context, Alarm.class);
        ai.putExtra("action", Alarm.START);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(time, alarmIntent), alarmIntent);
    }

    public static void setAlarm(Context context, long time, String id) {
        setAlarm(context, time, id, Alarm.START);
    }

    public static void setAlarm(Context context, long time, String id, int howToLaunch) {
        Intent ai = new Intent(context, Alarm.class);
        ai.putExtra("action", howToLaunch);
        //change requestCode pour placer plusieur alarm ou
        ai.setData(Uri.parse("reveil://" + id));
        ai.setAction(id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(time, alarmIntent), alarmIntent);
    }

    public static void cancelAlarm(Context context, String id) {
        Intent ai = new Intent(context, Alarm.class);
        ai.putExtra("action", Alarm.START);
        //change requestCode pour placer plusieur alarm ou
        ai.setData(Uri.parse("reveil://" + id));
        ai.setAction(id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmIntent);
    }

    /**
     * @param context le context
     * @return la prochaine alarm du téléphone y compris celle du system
     */
    public static long getAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am.getNextAlarmClock() != null) {
            return am.getNextAlarmClock().getTriggerTime();
        }
        return -1;
    }

    public static void setUpAlarm(Context context, Calendrier calendrier) {
        PersonnalAlarmManager personnalAlarmManager = PersonnalAlarmManager.getInstance(context);

        AlarmConditionManager alarmConditionManager = AlarmConditionManager.getInstance(context);


        CurrentDate dayAnalysed = new CurrentDate();

        Log.d("Constraint", alarmConditionManager.getAllConstraints().toString());
        Log.d("Constraint", personnalAlarmManager.getAllAlarmToList().toString());
        for (int dayAfter = 0; dayAfter < 7; dayAfter++) {
            Log.d("Alarm", "day analysed " + dayAnalysed);
            List<EventCalendrier> events = calendrier.getEventsOfDay(dayAnalysed);


            if (!events.isEmpty()) {
                //sauvgrade si alarme désactivé pour se jour
                List<Long> previouslyDisabledAlarm = new LinkedList<>();
                List<AlarmRing> listAlarmForThisDay = personnalAlarmManager.get(dayAnalysed);
                for (AlarmRing alarmRing : listAlarmForThisDay) {
                    if (!alarmRing.isActivate()) {
                        previouslyDisabledAlarm.add(alarmRing.getTimeInMillis());
                    }
                }

                //supprimer toutes les alarmes (Tach) pour se jour
                personnalAlarmManager.removeForDay(context, dayAnalysed);

                EventCalendrier currEvent = events.get(0);
                if (currEvent.getDuree().getHour() < 8 || DataGlobal.getSavedBoolean(context, DataGlobal.FERIER_DAY_ENABELED)) {
                    int i = 0;
                    do {
                        currEvent = events.get(i);
                        i++;
                    } while (i < events.size() && !alarmConditionManager.matchConstraints(currEvent));
                    if (alarmConditionManager.matchConstraints(currEvent)) {
                        //remet ou met l'alarm si besoin

                        DateCalendrier timeAlarmRing = new DateCalendrier(currEvent.getDate());
                        if (DataGlobal.getSavedBoolean(context, DataGlobal.COMPLEX_ALARM_SETTINGS)) {//mode alarmes de type comples
                            for (AlarmCondtion alarmCondtion : AlarmConditionManager.getInstance(context).getAllConditions()) {
                                if (alarmCondtion.isApplicableTo(currEvent)) {
                                    timeAlarmRing.setHourWithMillis(alarmCondtion.getAlarmAt());
                                    personnalAlarmManager.add(dayAnalysed, new AlarmRing(timeAlarmRing.getTimeInMillis(),
                                            !previouslyDisabledAlarm.contains(timeAlarmRing.getTimeInMillis())));
                                }
                            }
                        } else {//mode alarmes de type simple

                            timeAlarmRing.setTimeInMillis(timeAlarmRing.getTimeInMillis() - DataGlobal.getSavedInt(context, DataGlobal.TIME_BEFORE_RING) * 60L * 1000L);
                            if (DataGlobal.getActivatedDays(context).contains(timeAlarmRing.get(GregorianCalendar.DAY_OF_WEEK))) {
                                personnalAlarmManager.add(dayAnalysed, new AlarmRing(timeAlarmRing.getTimeInMillis(),
                                        !previouslyDisabledAlarm.contains(timeAlarmRing.getTimeInMillis())));
                            }
                        }
                    }
                }

            }

            dayAnalysed = dayAnalysed.addDay(1);
        }
        personnalAlarmManager.removeUseless(context);
        personnalAlarmManager.setUpAlarms(context);
        personnalAlarmManager.save(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra("action", Alarm.NONE)) {
            case Alarm.START:
                boolean enabled = DataGlobal.getSavedBoolean(context, DataGlobal.ALARM_ENABELED);
                if (enabled) {
                    //Sound
                    startRingtion(context);

                    //Vibration
                    startVibration(context);

                    //Notification
                    showNotification(context);

                    //affiche activity to disable alarm
                    Intent activityAlarm = new Intent(context, AlarmRingActivity.class);
                    activityAlarm.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(activityAlarm);

                    Log.d("Alarm", "ringing...");
                } else {
                    Log.d("Alarm", "alarm n'a pas pu sonner car elle est désactiver");
                }
                break;
            case Alarm.STOP:
                stopRington();
                stopVibration(context);
                clearNotif(context);
                break;
            default:
                Log.d("Alarm", "no action");
                break;
        }

    }

    private void clearNotif(Context context) {
        Notif.cancelAlarmNotif(context);
    }

    private void showNotification(Context context) {
        Intent ai = new Intent(context, AlarmRingActivity.class);
        PendingIntent cancelAlarmIntent = PendingIntent.getActivity(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notif notif = new Notif(context, Notif.ALARM_NOTIFICATION_ID, NotificationManager.IMPORTANCE_HIGH,
                "Alarm", "ring", R.drawable.ic_alarm, cancelAlarmIntent);
        notif.setOngoing(true);
        notif.show();
    }

    private void startVibration(Context context) {
        final Vibrator vibrator = getVibrator(context);
        if (vibrator != null) {
            vibrator.cancel();
            long[] pattern = {1000, 1000, 1000, 1000};
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
        }
    }

    private void stopVibration(Context context) {
        final Vibrator vibrator = getVibrator(context);
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void startRingtion(Context context) {
        Alarm.ring = RingtoneManager.getRingtone(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        AudioAttributes alarmVoume = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        ring.setAudioAttributes(alarmVoume);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ring.setLooping(true);
        } else {
            Log.d("Alarm", "can't put loop");
        }
        ring.play();
    }

    private void stopRington() {
        if (ring != null) {
            ring.stop();
            ring = null;
        }
    }

    private Vibrator getVibrator(Context context) {
        Vibrator vb;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vb = vibratorManager.getDefaultVibrator();
        } else {
            vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return vb;
    }
}
