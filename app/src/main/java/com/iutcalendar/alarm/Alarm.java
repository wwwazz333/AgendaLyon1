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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.notification.Notif;

public class Alarm extends BroadcastReceiver {
    public final static int NONE = 0, STOP = 1, START = 2;

    private static Ringtone ring;

    /**
     * cancel l'alarm précédente et en place une nouvelle
     *
     * @param context
     * @param time    heure de l'alarm (en ms)
     */
    public static void setAlarm(Context context, long time) {
        Intent ai = new Intent(context, Alarm.class);
        ai.putExtra("action", Alarm.START);
        //change requestCode pour placer plusieur alarm ou
//        ai.setData(Uri.parse("custom://" + alarm.ID));
//        ai.setAction(String.valueOf(alarm.ID));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(time, alarmIntent), alarmIntent);
    }

    /**
     * @param context
     * @return la prochaine alarm du téléphone y compris celle du system
     */
    public static long getAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am.getNextAlarmClock() != null) {
            return am.getNextAlarmClock().getTriggerTime();
        }
        return -1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra("action", Alarm.NONE)) {
            case Alarm.START:
                Boolean enabled = DataGlobal.getSavedBoolean(context, DataGlobal.ALARM_ENABELED);
                if (enabled) {
                    //Sound
                    Alarm.ring = RingtoneManager.getRingtone(context, Settings.System.DEFAULT_RINGTONE_URI);
                    AudioAttributes alarmVoume = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                    ring.setAudioAttributes(alarmVoume);
                    ring.play();

                    //Vibration

                    final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.cancel();
                        long[] pattern = {1000, 1000, 1000, 1000};
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
                    }

                    //Notification
                    Intent ai = new Intent(context, Alarm.class);
                    ai.putExtra("action", Alarm.STOP);
                    PendingIntent cancelAlarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    new Notif(context, Notif.ALARM_NOTIFICATION_ID, NotificationManager.IMPORTANCE_HIGH,
                            "Alarm", "ring", R.drawable.ic_alarm, cancelAlarmIntent).show();

                    Log.d("Alarm", "ringing...");
                } else {
                    Log.d("Alarm", "alarm n'a pas pu sonner car elle est désactiver");
                }
                break;
            case Alarm.STOP:
                if (ring != null) {
                    ring.stop();
                    ring = null;
                }
                final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.cancel();
                }
                break;
            default:
                Log.d("Alarm", "no action");
                break;
        }

    }
}
