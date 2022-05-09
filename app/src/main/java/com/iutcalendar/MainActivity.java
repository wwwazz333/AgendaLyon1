package com.iutcalendar;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.PathGlobal;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.settings.SettingsActivity;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String calDownload = "output_download.ics";
    static boolean active = false;
    private CurrentDate currDate;

    @Override
    public void onResume() {
        super.onResume();
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
            finish();

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


        PathGlobal.setPathDownload(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());


        setCurrDate(new CurrentDate());
        showEvents();

        new Thread(new Runnable() {
            public void run() {
                FileDownload.updateFichier(PathGlobal.getFileDownload().getAbsolutePath(), getApplicationContext());
                if (MainActivity.active) {
                    showEvents();
                }

                System.out.println("udpated");
            }
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
            findViewById(id).setBackgroundColor(Color.parseColor("#FF03DAC5"));
        } else {
            findViewById(id).setBackgroundColor(Color.argb(0f, 1.0f, 1.0f, 1.0f));
        }
    }

}