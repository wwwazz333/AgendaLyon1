package com.iutcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.settings.SettingsActivity;
import com.iutcalendar.swiping.OnSwipeTouchListener;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    static boolean active = false;
    private CurrentDate currDate;

    @Override
    public void onResume() {
        super.onResume();
        showEvents();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.settingsBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });


        //Navigate weeks
        findViewById(R.id.nextWeek).setOnClickListener(view -> {
            setCurrDate(getCurrDate().nextWeek());
            showEvents();
        });
        findViewById(R.id.prevWeek).setOnClickListener(view -> {
            setCurrDate(currDate.prevWeek());
            showEvents();
        });

        //click on day
        setOnclicDay(R.id.dayLundi, GregorianCalendar.MONDAY);
        setOnclicDay(R.id.dayMardi, GregorianCalendar.TUESDAY);
        setOnclicDay(R.id.dayMercredi, GregorianCalendar.WEDNESDAY);
        setOnclicDay(R.id.dayJeudi, GregorianCalendar.THURSDAY);
        setOnclicDay(R.id.dayVendredi, GregorianCalendar.FRIDAY);
        setOnclicDay(R.id.daySamedi, GregorianCalendar.SATURDAY);
        setOnclicDay(R.id.dayDimanche, GregorianCalendar.SUNDAY);


        setOnclicDay(R.id.lundiNum, GregorianCalendar.MONDAY);
        setOnclicDay(R.id.mardiNum, GregorianCalendar.TUESDAY);
        setOnclicDay(R.id.mercrediNum, GregorianCalendar.WEDNESDAY);
        setOnclicDay(R.id.jeudiNum, GregorianCalendar.THURSDAY);
        setOnclicDay(R.id.vendrediNum, GregorianCalendar.FRIDAY);
        setOnclicDay(R.id.samediNum, GregorianCalendar.SATURDAY);
        setOnclicDay(R.id.dimancheNum, GregorianCalendar.SUNDAY);


        DataGlobal.savePathDownloadFile(getApplicationContext(), getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());


        setCurrDate(new CurrentDate());
        showEvents();

        update();


//        Intent myIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
//        myIntent.putExtra(AlarmClock.EXTRA_HOUR, getCurrDate().getHour());
//        myIntent.putExtra(AlarmClock.EXTRA_MINUTES, getCurrDate().getMinute()+1);
//
//        startActivity(myIntent);


        View rootLayout = findViewById(R.id.swipe_action_view);
        rootLayout.setOnTouchListener(new Gesture());
    }

    public void update() {
        new Thread(() -> {
            FileDownload.updateFichier(FileGlobal.getFileDownload(getApplicationContext()).getAbsolutePath(), getApplicationContext());
            if (MainActivity.active) {
                showEvents();
            }
            Log.d("File", "updated");
        }).start();
    }

    public void setDaysOfWeek() {
        setNumOfMonthAndSelected(R.id.lundiNum, GregorianCalendar.MONDAY);
        setNumOfMonthAndSelected(R.id.mardiNum, GregorianCalendar.TUESDAY);
        setNumOfMonthAndSelected(R.id.mercrediNum, GregorianCalendar.WEDNESDAY);
        setNumOfMonthAndSelected(R.id.jeudiNum, GregorianCalendar.THURSDAY);
        setNumOfMonthAndSelected(R.id.vendrediNum, GregorianCalendar.FRIDAY);
        setNumOfMonthAndSelected(R.id.samediNum, GregorianCalendar.SATURDAY);
        setNumOfMonthAndSelected(R.id.dimancheNum, GregorianCalendar.SUNDAY);
    }

    public void showEvents() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        EventFragment fragment = new EventFragment();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public CurrentDate getCurrDate() {
        return this.currDate;
    }

    public void setCurrDate(CurrentDate currDate) {
        this.currDate = currDate;
        ((TextView) findViewById(R.id.currDateLabel)).setText(currDate.toString());
        setDaysOfWeek();
    }

    private void setOnclicDay(int id, int day) {
        findViewById(id).setOnClickListener(view -> {
            setCurrDate(currDate.getDateOfDayOfWeek(day));
            setDaysOfWeek();
            showEvents();
        });
    }

    public void setNumOfMonthAndSelected(int id, int day) {
        ((TextView) findViewById(id)).setText(String.valueOf(getCurrDate().getDateOfDayOfWeek(day).getDay()));
        if (getCurrDate().getDay() == getCurrDate().getDateOfDayOfWeek(day).getDay()) {
            findViewById(id).setBackgroundColor(Color.parseColor("#FFC41442"));
        } else {
            findViewById(id).setBackgroundColor(Color.argb(0f, 1.0f, 1.0f, 1.0f));
        }
    }


    class Gesture extends OnSwipeTouchListener {
        protected Gesture() {
            super(MainActivity.this);
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            int sens = -1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setCurrDate(getCurrDate().addDay(sens));
        }

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            int sens = 1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setCurrDate(getCurrDate().addDay(sens));
        }

        @Override
        public void onSwipeDown() {
            super.onSwipeDown();
            Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_SHORT).show();
            update();
        }

    }

}