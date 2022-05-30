package com.iutcalendar.alarm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.settings.SettingsApp;

public class AlarmRingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }


        setContentView(R.layout.activity_alarm_ring);


        TextView horaire = findViewById(R.id.horaire);
        ImageButton stopAlarmBtn = findViewById(R.id.stop_alarm_btn);

        long timeAlarm = getIntent().getLongExtra("time", -1);
        if (timeAlarm != -1) {
            DateCalendrier time = new DateCalendrier();
            time.setTimeInMillis(timeAlarm);
            horaire.setText(time.timeToString());
        } else {
            horaire.setText("Time");
        }


        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                stopAlarmBtn,
                PropertyValuesHolder.ofFloat("scaleX", 2f),
                PropertyValuesHolder.ofFloat("scaleY", 2f)
        );
        scaleDown.setDuration(500);
        scaleDown.setRepeatMode(ValueAnimator.REVERSE);
        scaleDown.setRepeatCount(ValueAnimator.INFINITE);
        scaleDown.start();

        stopAlarmBtn.setOnClickListener(view -> {
            Intent cancelAlarmIntent = new Intent(getApplicationContext(), Alarm.class);
            cancelAlarmIntent.putExtra("action", Alarm.STOP);

            sendBroadcast(cancelAlarmIntent);

            finish();
        });
    }
}