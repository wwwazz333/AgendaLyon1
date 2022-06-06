package com.iutcalendar.mainpage;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.calendar.iutcalendar.R;
import com.calendar.iutcalendar.databinding.ActivityPageEventBinding;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.mainpage.ui.main.SectionsPagerAdapter;
import com.iutcalendar.service.ForgroundServiceUpdate;
import com.iutcalendar.settings.SettingsActivity;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.widget.WidgetCalendar;

import java.io.File;
import java.util.GregorianCalendar;

public class PageEventActivity extends AppCompatActivity {

    private ActivityPageEventBinding binding;
    private static boolean active = false, updating = false;
    private FragmentTransaction fragmentTransaction;
    private CurrentDate currDate;
    private TextView currDateLabel;
    private LinearLayout nameDayLayout;
    private LinearLayout dayOfWeek;
    private Calendrier calendrier;

    private ViewPager viewPager;

    private SectionsPagerAdapter sectionsPagerAdapter;

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return sectionsPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Global", "PageEventActivity start");
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));
        setContentView(R.layout.activity_page_event);

        binding = ActivityPageEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initVariable();
        PersonnalCalendrier.getInstance(getApplicationContext());

        //init Calendrier
        File fileCal = FileGlobal.getFileDownload(getApplicationContext());
        String str = FileGlobal.readFile(fileCal);
        setCalendrier(new Calendrier(str));

        CurrentDate dateToLaunche = new CurrentDate();
        Log.d("Widget", "main : " + getIntent().getBooleanExtra("launche_next_event", false));
        if (getIntent().getBooleanExtra("launche_next_event", false)) {
            dateToLaunche.add(GregorianCalendar.MINUTE, WidgetCalendar.DELAY_AFTER_EVENT_PASSED);//pcq l'event s'affiche tjrs au bout de 30min
            EventCalendrier[] es = getCalendrier().getNext2EventAfter(dateToLaunche);
            if (es[0] != null) {
                dateToLaunche = new CurrentDate(es[0].getDate());
            }
        } else {
            Log.d("Widget", "default date");
        }


        viewPager = binding.viewPager;
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCalendrier(), getCurrDate());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new PageChangeWeekListener(this));


        setCurrDate(dateToLaunche);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Page", "new page : " + position);
                if (position != getCurrDate().getPosDayOfWeek())
                    setCurrDate(getCurrDate().getDateOfDayOfWeek(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //---------------Gesture swipe---------------
        //Week
        int childCount = nameDayLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            nameDayLayout.getChildAt(i).setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));
        }
        nameDayLayout.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));
        dayOfWeek.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));


        //---------------Button---------------
        currDateLabel.setOnClickListener(view -> setCurrDate(new CurrentDate()));
        findViewById(R.id.settingsBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });


        //Click on day
        setOnclicDay(binding.dayLundi, 0);
        setOnclicDay(binding.dayMardi, 1);
        setOnclicDay(binding.dayMercredi, 2);
        setOnclicDay(binding.dayJeudi, 3);
        setOnclicDay(binding.dayVendredi, 4);
        setOnclicDay(binding.daySamedi, 5);
        setOnclicDay(binding.dayDimanche, 6);


        setOnclicDay(binding.lundiNum, 0);
        setOnclicDay(binding.mardiNum, 1);
        setOnclicDay(binding.mercrediNum, 2);
        setOnclicDay(binding.jeudiNum, 3);
        setOnclicDay(binding.vendrediNum, 4);
        setOnclicDay(binding.samediNum, 5);
        setOnclicDay(binding.dimancheNum, 6);


        //affiche la dialog si ouvert depuis notification (changmenents)
        String changements = getIntent().getStringExtra("changes");
        if (changements != null) {
            Log.d("Extra", changements);
            showChangedEvent(changements);
        } else {
            Log.d("Extra", "no changes");
        }

        //démare le service d'arrière plan avec interval
        ForgroundServiceUpdate.start(getApplicationContext());


        update();


        /*####Testing feature#####*/
//        Alarm.setAlarm(getApplicationContext(), System.currentTimeMillis() + 5_000, "test");

        Log.d("Global", "PageEventActivity end");
    }


    private void initVariable() {
        this.currDate = new CurrentDate();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        currDateLabel = findViewById(R.id.currDateLabel);
        nameDayLayout = findViewById(R.id.nameDayLayout);
        dayOfWeek = findViewById(R.id.dayOfWeek);
    }


    public static boolean isActive() {
        return active;
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


    /*########################################################################
                                     UPDATE
    ########################################################################*/
    public void update() {
        Log.d("Update", "start");
        if (updating) return;
        updating = true;
        //start anim re load
        new Thread(() -> {
            FileGlobal.updateAndGetChange(getApplicationContext(), calendrier, ((context, intent) -> startActivity(intent)));


            Log.d("Update", String.valueOf(PageEventActivity.active));
            if (PageEventActivity.active) {
                Log.d("Update", "activation");
                updateScreen();
            }
            //arret animation re load
            updating = false;
            Log.d("File", "updated");
        }).start();
    }

    private void showChangedEvent(String changes) {
        //TODO faire plus beau
        //FIXME pas afficher quand charger agenda
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PageEventActivity.this);
        alertDialog.setTitle(getString(R.string.event));

        alertDialog.setMessage(changes);

        alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }


    public void updateScreen() {
        updateEvent();
    }

    public void updateEvent() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sectionsPagerAdapter.notifyDataSetChanged();
//            }
//        });

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


        if (!this.currDate.sameWeek(newDate)) {
            Log.d("Date", "change week");
            this.currDate.set(newDate);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), getCalendrier(), getCurrDate());
                    viewPager.setAdapter(sectionsPagerAdapter);
                }
            });

        }


        this.currDate.set(newDate);

        currDateLabel.setText(currDate.getRelativeDayName(getBaseContext()));
//        viewPager.setCurrentItem(currDate.getPosDayOfWeek());

        setDaysOfWeek();
        updateScreen();
    }


    public void setCalendrier(Calendrier calendrier) {
        this.calendrier = calendrier;
    }

    public void setDaysOfWeek() {
        setNumOfMonthAndSelected(binding.lundiNum, 0);
        setNumOfMonthAndSelected(binding.mardiNum, 1);
        setNumOfMonthAndSelected(binding.mercrediNum, 2);
        setNumOfMonthAndSelected(binding.jeudiNum, 3);
        setNumOfMonthAndSelected(binding.vendrediNum, 4);
        setNumOfMonthAndSelected(binding.samediNum, 5);
        setNumOfMonthAndSelected(binding.dimancheNum, 6);
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

    private void setOnclicDay(TextView dayClicked, int day) {
        dayClicked.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener() {
            @Override
            protected void onClick() {
                setCurrDate(currDate.getDateOfDayOfWeek(day));
                super.onClick();
            }
        }));

    }

    public void setNumOfMonthAndSelected(TextView numDay, int day) {
        numDay.setText(String.valueOf(getCurrDate().getDateOfDayOfWeek(day).getDay()));


        if (getCurrDate().getDay() == getCurrDate().getDateOfDayOfWeek(day).getDay()) {
            numDay.setBackgroundColor(Color.parseColor("#FFC41442"));
        } else if (getCurrDate().getDateOfDayOfWeek(day).sameDay(new CurrentDate())) {
            numDay.setBackgroundColor(Color.parseColor("#88C41442"));
        } else {
            numDay.setBackgroundColor(Color.argb(0f, 1.0f, 1.0f, 1.0f));
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

    public void switchToPrevWeek() {
        int sens = -1;

        setAnimationDirection(sens);
        setCurrDate(getCurrDate().prevWeek());
    }

    public void switchToPrevWeekAlt() {
        int sens = -1;


        setAnimationDirection(sens);
        setCurrDate(getCurrDate().prevWeek());

//        setCurrDate(getCurrDate().getDateOfDayOfWeek(6));
    }

    public void switchToNextWeek() {
        int sens = -1;


        setAnimationDirection(sens);
        setCurrDate(getCurrDate().nextWeek());
    }

    public void switchToNextWeekAlt() {
        int sens = -1;


        setAnimationDirection(sens);
        setCurrDate(getCurrDate().prevWeek());

//        setCurrDate(getCurrDate().getDateOfDayOfWeek(0));
    }


    /*########################################################################
                                    GESTION EVENT
     ########################################################################*/

    private class GestureWeekListener extends GestureEventManager {

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            switchToPrevWeek();
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            switchToNextWeek();
        }
    }

}