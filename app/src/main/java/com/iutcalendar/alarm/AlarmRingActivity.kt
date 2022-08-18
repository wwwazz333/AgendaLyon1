package com.iutcalendar.alarm

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R

class AlarmRingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsApp.setLocale(resources, DataGlobal.getLanguage(applicationContext))

        //wakeup phone & not unlock needed
        wakeAndShowActivity()
        supportActionBar?.hide()

        setContentView(R.layout.activity_alarm_ring)
        val horaire: TextView = findViewById(R.id.horaire)
        val stopAlarmBtn: ImageButton = findViewById(R.id.stop_alarm_btn)
        horaire.text = CurrentDate().timeToString()
        setAnimAlarmBtn()
        stopAlarmBtn.setOnClickListener {
            val cancelAlarmIntent = Intent(applicationContext, Alarm::class.java)
            cancelAlarmIntent.putExtra("action", Alarm.STOP)
            sendBroadcast(cancelAlarmIntent)
            finish()
        }
    }

    private fun setAnimAlarmBtn() {
        //Animation Button arrêt Alarm
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            findViewById<View>(R.id.shadow_btn),
            PropertyValuesHolder.ofFloat("scaleX", 3f),
            PropertyValuesHolder.ofFloat("scaleY", 3f)
        )
        scaleDown.duration = 500
        scaleDown.repeatMode = ValueAnimator.REVERSE
        scaleDown.repeatCount = ValueAnimator.INFINITE
        scaleDown.start()
    }

    private fun wakeAndShowActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }
}