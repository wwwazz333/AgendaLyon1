package com.iutcalendar.alarm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.settings.SettingsApp;

public class AlarmRingActivity extends AppCompatActivity {

    private ImageButton stopAlarmBtn;
    private TextView horaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));

        //wakeup phone & not unlock needed
        wakeAndShowActivity();


        setContentView(R.layout.activity_alarm_ring);


        horaire = findViewById(R.id.horaire);
        stopAlarmBtn = findViewById(R.id.stop_alarm_btn);


        horaire.setText(new CurrentDate().timeToString());


        setAnimAlarmBtn();

        stopAlarmBtn.setOnClickListener(view -> {
            Intent cancelAlarmIntent = new Intent(getApplicationContext(), Alarm.class);
            cancelAlarmIntent.putExtra("action", Alarm.STOP);

            sendBroadcast(cancelAlarmIntent);

            finish();
        });
    }

    private void setAnimAlarmBtn() {
        //Animation Boutton Arret Alarm
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                findViewById(R.id.shadow_btn),
                PropertyValuesHolder.ofFloat("scaleX", 3f),
                PropertyValuesHolder.ofFloat("scaleY", 3f)
        );
        scaleDown.setDuration(500);
        scaleDown.setRepeatMode(ValueAnimator.REVERSE);
        scaleDown.setRepeatCount(ValueAnimator.INFINITE);
        scaleDown.start();
    }

    private void wakeAndShowActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }
}