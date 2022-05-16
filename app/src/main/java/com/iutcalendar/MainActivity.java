package com.iutcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.data.Tuple;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.settings.SettingsActivity;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.ReloadAnimationFragment;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;

import java.io.File;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static boolean active = false, updating = false;
    private FragmentTransaction fragmentTransaction;
    private CurrentDate currDate;
    private GestureDetectorCompat gestureD;
    private TextView currDateLabel;
    private LinearLayout nameDayLayout;
    private LinearLayout dayOfWeek;
    private Calendrier calendrier, savePrevCalendrier;

    private void initVariable() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        gestureD = new GestureDetectorCompat(this, new GestureListener());

        currDateLabel = findViewById(R.id.currDateLabel);
        nameDayLayout = findViewById(R.id.nameDayLayout);
        dayOfWeek = findViewById(R.id.dayOfWeek);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureD.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScreen();
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
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));
        setContentView(R.layout.activity_main);


        initVariable();
        PersonnalCalendrier.getInstance().load(getApplicationContext());


        currDateLabel.setOnClickListener(view -> setCurrDate(new CurrentDate()));

        //---------------Gesture swipe---------------
        int childCount = nameDayLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            nameDayLayout.getChildAt(i).setOnTouchListener(new TouchGestureListener(getApplicationContext(), new GestureWeekListener()));
        }
        nameDayLayout.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new GestureWeekListener()));
        dayOfWeek.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new GestureWeekListener()));


        //---------------Button---------------
        findViewById(R.id.settingsBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        //Navigate weeks
        findViewById(R.id.nextWeek).setOnClickListener(view -> setCurrDate(getCurrDate().nextWeek()));
        findViewById(R.id.prevWeek).setOnClickListener(view -> setCurrDate(currDate.prevWeek()));

        //Click on day
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
        //init Calendrier
        File fileCal = FileGlobal.getFileDownload(getApplicationContext());
        String str = FileGlobal.readFile(fileCal);
        setCalendrier(new Calendrier(str));
        savePrevCalendrier = getCalendrier().clone();

        CurrentDate dateToLaunche = new CurrentDate();
        long timeInMillis = getIntent().getLongExtra("day_to_launche", -1);
        Log.d("Date", String.valueOf(timeInMillis));
        if (timeInMillis != -1) {
            Log.d("Date", "not default date");
            dateToLaunche.setTimeInMillis(timeInMillis);
        } else {
            Log.d("Date", "default date");
        }
        setCurrDate(dateToLaunche);

        for (EventCalendrier e : getCalendrier().getEvents()) {
            Log.d("Calendrier", e.toString());
        }
        Log.d("Calendrier", "-------------FIN MAIN ACTIVITY-------------");

//        update();


//        Intent myIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
//        myIntent.putExtra(AlarmClock.EXTRA_HOUR, getCurrDate().getHour());
//        myIntent.putExtra(AlarmClock.EXTRA_MINUTES, getCurrDate().getMinute()+1);
//
//        startActivity(myIntent);
    }

    /*########################################################################
                                     UPDATE
    ########################################################################*/
    public void update() {
        if (updating) return;
        updating = true;
        startFragment(R.id.animFragment, new ReloadAnimationFragment());//start anim re load
        new Thread(() -> {
            FileDownload.updateFichier(FileGlobal.getFileDownload(getApplicationContext()).getAbsolutePath(), getApplicationContext());
            if (MainActivity.active) {
                updateScreen();
            }
            Log.d("File", "updated");
            startFragment(R.id.animFragment, new Fragment());//arret animation re load
            updating = false;
        }).start();
    }

    public void checkChanges() {
        for (Tuple change : getCalendrier().getChangedEvent(savePrevCalendrier)) {
            Log.d("Calendrier", change.second + " : " + change.first.toString());
        }
        Log.d("Calendrier", "-------------FIN UPDATE-------------");
        savePrevCalendrier = getCalendrier().clone();
    }

    public void updateScreen() {
        if (DataGlobal.getSavedBoolean(getApplicationContext(), "summer_offset")) {
            Log.d("Offset", "summer offset");
            DateCalendrier.setSummerOffset(1);
        } else {
            DateCalendrier.setSummerOffset(0);
            Log.d("Offset", "winter offset");
        }

        updateEvent();
    }

    public void updateEvent() {
        startFragment(R.id.frameLayout, new EventFragment());
    }

    /*########################################################################
                                    GETTERS
    ########################################################################*/
    public CurrentDate getCurrDate() {
        return this.currDate;
    }

    public Calendrier getCalendrier() {
        return calendrier;
    }

    /*########################################################################
                                        SETTERS
      ########################################################################*/
    public void setCurrDate(CurrentDate newDate) {
        Log.d("Date", "set curr date");
        this.currDate = newDate;
        newDate.runForDate(() -> {
            currDateLabel.setText(getResources().getString(R.string.today));
            Log.d("Date", "today");
        }, () -> {
            currDateLabel.setText(getResources().getString(R.string.tomorrow));
            Log.d("Date", "tomorrow");
        }, () -> {
            Log.d("Date", "else");
            currDateLabel.setText(newDate.toString(SettingsApp.getLocale(getResources())));
        });

        setDaysOfWeek();
        updateScreen();
    }


    public void setCalendrier(Calendrier calendrier) {
        this.calendrier = calendrier;
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

    public void setAnimationGoRight() {
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void setAnimationGoLeft() {
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);

    }

    public void setAnimationDirection(int d) {
        if (d > 0) {
            setAnimationGoRight();
        } else {
            setAnimationGoLeft();
        }
    }

    private void setOnclicDay(int id, int day) {
        findViewById(id).setOnTouchListener(new TouchGestureListener(getApplicationContext(), new GestureWeekListener() {
            @Override
            protected void onClick() {
                setCurrDate(currDate.getDateOfDayOfWeek(day));
                super.onClick();
            }
        }));

    }

    public void setNumOfMonthAndSelected(int id, int day) {
        ((TextView) findViewById(id)).setText(String.valueOf(getCurrDate().getDateOfDayOfWeek(day).getDay()));
        if (getCurrDate().getDay() == getCurrDate().getDateOfDayOfWeek(day).getDay()) {
            findViewById(id).setBackgroundColor(Color.parseColor("#FFC41442"));
        } else if (getCurrDate().getDateOfDayOfWeek(day).sameDay(new CurrentDate())) {
            findViewById(id).setBackgroundColor(Color.parseColor("#88C41442"));
        } else {
            findViewById(id).setBackgroundColor(Color.argb(0f, 1.0f, 1.0f, 1.0f));
        }
    }

    /*########################################################################
                                    OTHER
     ########################################################################*/
    public void startFragment(int id, Fragment fragment) {
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
    }


    /*########################################################################
                                    GESTION EVENT
     ########################################################################*/
    private class GestureListener extends GestureEventManager {

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            int sens = -1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setAnimationDirection(sens);
            setCurrDate(getCurrDate().addDay(sens));
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            int sens = 1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setAnimationDirection(sens);
            setCurrDate(getCurrDate().addDay(sens));
        }

        @Override
        public void onSwipeDown() {
            super.onSwipeDown();
            update();
        }
    }

    public class GestureWeekListener extends GestureEventManager {

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            int sens = -1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setAnimationDirection(sens);
            if (sens > 0) {
                setCurrDate(getCurrDate().nextWeek());
            } else {
                setCurrDate(getCurrDate().prevWeek());
            }
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            int sens = 1;
            if (DataGlobal.getSavedBoolean(getApplicationContext(), "revert_swaping_day")) {
                sens *= -1;
            }
            setAnimationDirection(sens);
            if (sens > 0) {
                setCurrDate(getCurrDate().nextWeek());
            } else {
                setCurrDate(getCurrDate().prevWeek());
            }
        }
    }


}