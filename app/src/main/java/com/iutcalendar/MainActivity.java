package com.iutcalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.data.Tuple;
import com.iutcalendar.filedownload.FileDownload;
import com.iutcalendar.notification.BackgroundNotificationUpdate;
import com.iutcalendar.settings.SettingsActivity;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.ReloadAnimationFragment;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static boolean active = false, updating = false;
    private FragmentTransaction fragmentTransaction;
    private CurrentDate currDate;
    private TextView currDateLabel;
    private LinearLayout nameDayLayout;
    private LinearLayout dayOfWeek;
    private Calendrier calendrier, savePrevCalendrier;
    private ConstraintLayout rootLayout;
    private View actionView;
    private FrameLayout frameLayout;


    private static final String NOTIFICATION_CHANNEL_ID = "12001";
    private final static String default_notification_channel_id = "modificationChanelId";

    private void initVariable() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        currDateLabel = findViewById(R.id.currDateLabel);
        nameDayLayout = findViewById(R.id.nameDayLayout);
        dayOfWeek = findViewById(R.id.dayOfWeek);
        rootLayout = findViewById(R.id.rootLayout);
        actionView = findViewById(R.id.actionView);
        frameLayout = findViewById(R.id.frameLayout);

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
        Log.d("Global", "MainActivity start");
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

        actionView.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new GestureDayListener()));


        //---------------Button---------------
        findViewById(R.id.settingsBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });


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
        Log.d("Widget", "main : " + getIntent().getBooleanExtra("launche_next_event", false));
        if (getIntent().getBooleanExtra("launche_next_event", false)) {
            EventCalendrier[] es = getCalendrier().getNext2EventAfter(dateToLaunche);
            if (es.length >= 1) {
                dateToLaunche = new CurrentDate(es[0].getDate());
            }
        } else {
            Log.d("Widget", "default date");
        }
        setCurrDate(dateToLaunche);

        update();

        String changements = getIntent().getStringExtra("changes");
        if (changements != null) {
            Log.d("Extra", changements);
            showChangedEvent(changements);
        } else {
            Log.d("Extra", "no changes");
        }

        if (!BackgroundNotificationUpdate.foregroundServiceRunning(this)) {
            startForegroundService(new Intent(this, BackgroundNotificationUpdate.class));
        }


        Log.d("Global", "MainActivity end");
    }

    /*########################################################################
                                     UPDATE
    ########################################################################*/
    public void update() {
        Log.d("Update", "start");
        if (updating) return;
        updating = true;
        startFragment(R.id.animFragment, new ReloadAnimationFragment());//start anim re load
        new Thread(() -> {
            FileDownload.updateFichier(FileGlobal.getFileDownload(getApplicationContext()).getAbsolutePath(), getApplicationContext());
            Log.d("Update", String.valueOf(MainActivity.active));
            if (MainActivity.active) {
                Log.d("Update", "activation");
                updateScreen();
                startFragment(R.id.animFragment, new Fragment());//arret animation re load
            }
            updating = false;
            Log.d("File", "updated");
        }).start();
    }

    public void checkChanges() {
        Log.d("Update", "check change start");
        List<Tuple<EventCalendrier, Calendrier.InfoChange>> changes = getCalendrier().getChangedEvent(savePrevCalendrier);
        if (!changes.isEmpty()) {
            String changesMsg = Calendrier.changeToString(getApplicationContext(), changes);
            showChangedEvent(changesMsg);

        }
        savePrevCalendrier = getCalendrier().clone();
    }

    private void showChangedEvent(String changes) {
        //TODO faire plus beau
        //FIXME pas afficher quand charger agenda
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getString(R.string.event));

        alertDialog.setMessage(changes);

        alertDialog.setPositiveButton(getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());


        alertDialog.show();
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
    private class GestureDayListener extends GestureEventManager {

        @Override
        protected void onClick() {
            super.onClick();
        }

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

    private class GestureWeekListener extends GestureEventManager {

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

    public GestureEventManager getGesutreDaily() {
        return new GestureDayListener();
    }


}