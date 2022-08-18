package com.iutcalendar.alarm

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.notification.Notif
import com.iutcalendar.notification.NotificationChannels
import com.univlyon1.tools.agenda.R
import java.util.*

class Alarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra("action", NONE)) {
            START -> {
                val enabled = DataGlobal.getSavedBoolean(context, DataGlobal.ALARM_ENABLED)
                if (enabled) {
                    //Sound
                    startRingtone(context)

                    //Vibration
                    startVibration(context)

                    //Notification
                    showNotification(context)

                    //affiche activity to disable alarm
                    val activityAlarm = Intent(context, AlarmRingActivity::class.java)
                    activityAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(activityAlarm)
                    Log.d("Alarm", "ringing...")
                } else {
                    Log.d("Alarm", "alarm n'a pas pu sonner car elle est désactiver")
                }
            }

            STOP -> {
                stopRingtone()
                stopVibration(context)
                clearNotif(context)
            }

            else -> Log.d("Alarm", "no action")
        }
    }

    private fun clearNotif(context: Context) {
        Notif.cancelAlarmNotif(context)
    }

    private fun showNotification(context: Context) {
        val ai = Intent(context, AlarmRingActivity::class.java)
        val cancelAlarmIntent = PendingIntent.getActivity(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notif = Notif(
            context, NotificationChannels.ALARM_NOTIFICATION_ID,
            "Alarm", "ring", R.drawable.ic_alarm, cancelAlarmIntent
        )
        notif.setOngoing(true)
        notif.show()
    }

    private fun startVibration(context: Context) {
        val vibrator = getVibrator(context)
        vibrator.cancel()
        val pattern = longArrayOf(1000, 1000, 1000, 1000)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1))
    }

    private fun stopVibration(context: Context) {
        getVibrator(context).cancel()
    }

    private fun startRingtone(context: Context) {
        ring = RingtoneManager.getRingtone(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        val alarmVolume = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        if (ring != null) {
            ring!!.audioAttributes = alarmVolume
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ring!!.isLooping = true
            } else {
                Log.d("Alarm", "can't put loop")
            }
            ring!!.play()
        }

    }

    private fun stopRingtone() {
        if (ring != null) {
            ring!!.stop()
            ring = null
        }
    }

    private fun getVibrator(context: Context): Vibrator {
        val vb: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        return vb
    }

    companion object {
        const val NONE = 0
        const val STOP = 1
        const val START = 2
        private var ring: Ringtone? = null

        /**
         * Cancel l'alarme précédente et en place une nouvelle
         *
         * @param context le context
         * @param time    heure de l'alarme (en ms)
         */
        fun setAlarm(context: Context, time: Long) {
            val ai = Intent(context, Alarm::class.java)
            ai.putExtra("action", START)
            val alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setAlarmClock(AlarmClockInfo(time, alarmIntent), alarmIntent)
        }


        fun setAlarm(context: Context?, time: Long, id: String) {
            setAlarm(context, time, id, START)
        }

        fun setAlarm(context: Context?, time: Long, id: String, howToLaunch: Int) {
            val ai = Intent(context, Alarm::class.java)
            ai.putExtra("action", howToLaunch)
            //change requestCode pour placer plusieurs alarmes ou
            ai.data = Uri.parse("reveil://$id")
            ai.action = id
            val alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setAlarmClock(AlarmClockInfo(time, alarmIntent), alarmIntent)
        }

        fun cancelAlarm(context: Context?, id: String?) {
            val ai = Intent(context, Alarm::class.java)
            ai.putExtra("action", START)
            //change requestCode pour placer plusieurs alarmes ou
            ai.data = Uri.parse("reveil://$id")
            ai.action = id
            val alarmIntent = PendingIntent.getBroadcast(context, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(alarmIntent)
        }

        /**
         * @param context le context
         * @return la prochaine alarm du téléphone y compris celle du system
         */
        fun getAlarm(context: Context): Long {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            return if (am.nextAlarmClock != null) {
                am.nextAlarmClock.triggerTime
            } else -1
        }

        fun setUpAlarm(context: Context?, calendrier: Calendrier) {
            val personalAlarmManager: PersonalAlarmManager = PersonalAlarmManager.getInstance(context)
            val alarmConditionManager: AlarmConditionManager = AlarmConditionManager.getInstance(context)
            var dayAnalysed = CurrentDate()
            Log.d("Constraint", alarmConditionManager.allConstraints.toString())
            Log.d("Constraint", personalAlarmManager.allAlarmToList.toString())
            for (dayAfter in 0..6) {
                Log.d("Alarm", "day analysed $dayAnalysed")
                val events = calendrier.getEventsOfDay(dayAnalysed)
                if (events.isNotEmpty()) {
                    //sauvegarde si alarme désactivée pour ce jour
                    val previouslyDisabledAlarm: MutableList<Long> = LinkedList()
                    val listAlarmForThisDay = personalAlarmManager[dayAnalysed]
                    for (alarmRing in listAlarmForThisDay) {
                        if (!alarmRing.isActivate) {
                            previouslyDisabledAlarm.add(alarmRing.timeInMillis)
                        }
                    }

                    //supprimer toutes les alarmes (Tache) pour se jour
                    personalAlarmManager.removeForDay(context, dayAnalysed)
                    var currEvent = events[0]
                    if (currEvent.dure.hour < 8 || DataGlobal.getSavedBoolean(context, DataGlobal.FERIER_DAY_ENABLED)) {
                        var i = 0
                        do {
                            currEvent = events[i]
                            i++
                        } while (i < events.size && !alarmConditionManager.matchConstraints(currEvent))
                        if (alarmConditionManager.matchConstraints(currEvent)) {
                            //remet ou met l'alarme si besoin
                            val timeAlarmRing = DateCalendrier(currEvent.date)
                            if (DataGlobal.getSavedBoolean(context, DataGlobal.COMPLEX_ALARM_SETTINGS)) { //mode alarmes de type complex
                                for (alarmCondition in AlarmConditionManager.getInstance(context).allConditions!!) {
                                    if (alarmCondition.isApplicableTo(currEvent)) {
                                        timeAlarmRing.setHourWithMillis(alarmCondition.alarmAt)
                                        personalAlarmManager.add(
                                            dayAnalysed, AlarmRing(
                                                timeAlarmRing.timeInMillis,
                                                !previouslyDisabledAlarm.contains(timeAlarmRing.timeInMillis)
                                            )
                                        )
                                    }
                                }
                            } else { //mode alarmes de type simple
                                timeAlarmRing.timeInMillis = timeAlarmRing.timeInMillis - DataGlobal.getSavedInt(context, DataGlobal.TIME_BEFORE_RING) * 60L * 1000L
                                if (DataGlobal.getActivatedDays(context).contains(timeAlarmRing[GregorianCalendar.DAY_OF_WEEK])) {
                                    personalAlarmManager.add(
                                        dayAnalysed, AlarmRing(
                                            timeAlarmRing.timeInMillis,
                                            !previouslyDisabledAlarm.contains(timeAlarmRing.timeInMillis)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                dayAnalysed = dayAnalysed.addDay(1)
            }
            personalAlarmManager.removeUseless(context)
            personalAlarmManager.setUpAlarms(context)
            personalAlarmManager.save(context)
        }
    }
}