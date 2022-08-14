package com.iutcalendar.mainpage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.event.changement.ChangeDialog;
import com.iutcalendar.mainpage.ui.main.SectionsPagerAdapter;
import com.iutcalendar.menu.MenuItemClickActivities;
import com.iutcalendar.service.ForegroundServiceUpdate;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.widget.WidgetCalendar;
import com.univlyon1.tools.agenda.R;
import com.univlyon1.tools.agenda.databinding.ActivityPageEventBinding;

import java.io.File;
import java.util.GregorianCalendar;

public class PageEventActivity extends AppCompatActivity {

    private static boolean updating = false;
    private ActivityPageEventBinding binding;
    private FragmentTransaction fragmentTransaction;
    private CurrentDate currDate;
    private TextView currDateLabel;
    private LinearLayout nameDayLayout;
    private LinearLayout dayOfWeek;
    private Calendrier calendrier;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Global", "PageEventActivity start");

        if (SettingsApp.adapteTheme(this)) {//it will restart, I don't know why
            return;
        }
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));


        binding = ActivityPageEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initVariable();
        PersonnalCalendrier.getInstance(getApplicationContext());

        initActionBar();

        //init Calendrier
        initCalendar();


        setCurrDate(getDateToLaunchAtFirst());

        initPageViewEvent();


        initGestureSwipeWeek();


        initAllOnClickEvents();


        //affiche la dialog si ouverte depuis notification (changements)
        String changements = getIntent().getStringExtra("changes");
        if (changements != null) {
            Log.d("Extra", changements);
            showChangedEvent(changements);
        } else {
            Log.d("Extra", "no changes");
        }

        //démarre le service d'arrière-plan avec interval
        ForegroundServiceUpdate.start(getApplicationContext());

        //update
        update(null);

        initAds();
        /*####Testing feature#####*/

        Log.d("Global", "PageEventActivity end");


    }


    private void initAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * init all on click events
     */
    private void initAllOnClickEvents() {
        //back to current date
        currDateLabel.setOnClickListener(view -> setCurrDate(new CurrentDate()));


        //Click on day
        setOnClickDay(binding.dayLundi, 0);
        setOnClickDay(binding.dayMardi, 1);
        setOnClickDay(binding.dayMercredi, 2);
        setOnClickDay(binding.dayJeudi, 3);
        setOnClickDay(binding.dayVendredi, 4);
        setOnClickDay(binding.daySamedi, 5);
        setOnClickDay(binding.dayDimanche, 6);


        setOnClickDay(binding.lundiNum, 0);
        setOnClickDay(binding.mardiNum, 1);
        setOnClickDay(binding.mercrediNum, 2);
        setOnClickDay(binding.jeudiNum, 3);
        setOnClickDay(binding.vendrediNum, 4);
        setOnClickDay(binding.samediNum, 5);
        setOnClickDay(binding.dimancheNum, 6);
    }

    /**
     * init gesture to switch between weeks
     */
    private void initGestureSwipeWeek() {
        int childCount = nameDayLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            nameDayLayout.getChildAt(i).setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));
        }
        nameDayLayout.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));
        dayOfWeek.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener()));
    }

    /**
     * init viewPager and sectionsPagerAdapter to see events
     */
    private void initPageViewEvent() {
        Log.d("Event", "cration Section page adapter------------------");

        viewPager = binding.viewPager;
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCalendrier());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Page", "new page : " + position);
                setCurrDate(new CurrentDate(getCalendrier().getFirstDay()).addDay(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setPositionPageToCurrDate();
    }

    private void updatePageViewEvent() {
        Log.d("Event", "update Section page adapter------------------");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCalendrier());
        viewPager.removeAllViews();
        viewPager.setAdapter(sectionsPagerAdapter);
        setPositionPageToCurrDate();
    }

    /**
     * @return the date the activity has to be launch
     */
    private CurrentDate getDateToLaunchAtFirst() {
        CurrentDate dateToLaunch = new CurrentDate();
        Log.d("Widget", "main : " + getIntent().getBooleanExtra("launch_next_event", false));
        if (getIntent().getBooleanExtra("launch_next_event", false)) {
            dateToLaunch.add(GregorianCalendar.MINUTE, WidgetCalendar.DELAY_AFTER_EVENT_PASSED);//pcq l'event s'affiche toujours au bout de 30min
            EventCalendrier[] es = getCalendrier().getNext2EventAfter(dateToLaunch);
            if (es[0] != null) {
                dateToLaunch = new CurrentDate(es[0].getDate());
            }
        } else {
            Log.d("Widget", "default date");
        }
        return dateToLaunch;
    }

    public ActivityPageEventBinding getBinding() {
        return binding;
    }

    private void initCalendar() {
        File fileCal = FileGlobal.getFileDownload(getApplicationContext());
        String str = FileGlobal.readFile(fileCal);
        setCalendrier(new Calendrier(str));
    }

    private void initVariable() {
        this.currDate = new CurrentDate();

        this.fragmentTransaction = getSupportFragmentManager().beginTransaction();

        this.nameDayLayout = findViewById(R.id.nameDayLayout);
        this.dayOfWeek = findViewById(R.id.dayOfWeek);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_main_page);
            currDateLabel = actionBar.getCustomView().findViewById(R.id.title);
        }
    }



    /*########################################################################
                                     UPDATE
    ########################################################################*/

    /**
     * update the calendar file
     *
     * @param onFinishListener est toujours appelé à la fin de la méthode
     */
    public void update(OnFinishListener onFinishListener) {
        if (!updating) {
            Log.d("Update", "start");
            updating = true;
            new Thread(() -> {
                FileGlobal.updateAndGetChange(this, calendrier, ((context, intent) -> startActivity(intent)));

                runOnUiThread(() -> {
                    Log.d("Debug", "appeler d'epuis update");

                    updatePageViewEvent();
//                    setCurrDate(getCurrDate());
                });

                if (onFinishListener != null) onFinishListener.finished();
                updating = false;
                Log.d("Update", "end");
            }).start();
        }


    }

    private void showChangedEvent(String changes) {
        //TODO faire plus beau
        //FIXME pas afficher quand charger agenda
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PageEventActivity.this);
//        alertDialog.setTitle(getString(R.string.event));
//
////        alertDialog.setMessage(changes);
//        alertDialog.setView(getLayoutInflater().inflate(R.layout.fragment_history, null));
//
//        alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
//
//        alertDialog.show();

        new ChangeDialog(this).show();

    }

    /*########################################################################
                               GETTERS & SETTERS
    ########################################################################*/
    public CurrentDate getCurrDate() {
        return this.currDate;
    }

    public void setCurrDate(CurrentDate newDate) {
//        if (newDate.sameDay(currDate)) { //some bugs
//            currDateLabel.setText(currDate.getRelativeDayName(getBaseContext()));
//            return;
//        }
        Log.d("Date", currDate + " set curr date to " + newDate);

        if (getCalendrier() != null && getCalendrier().getFirstDay() != null && getCalendrier().getLastDay() != null) {
            if (newDate.compareTo(getCalendrier().getFirstDay()) < 0) {
                newDate = new CurrentDate(getCalendrier().getFirstDay());
            } else if (newDate.compareTo(getCalendrier().getLastDay()) > 0) {
                newDate = new CurrentDate(getCalendrier().getLastDay());
            }
        }

        this.currDate.set(newDate);

        currDateLabel.setText(currDate.getRelativeDayName(getBaseContext()));
        Log.d("ActionBar", currDate.getRelativeDayName(getBaseContext()));

        setPositionPageToCurrDate();
        setDaysOfWeek();
    }

    private void setPositionPageToCurrDate() {
        if (getCalendrier() != null && getCalendrier().getFirstDay() != null && viewPager != null) {
            viewPager.setCurrentItem(getPosOfDate(this.currDate));
        }
    }

    private int getPosOfDate(CurrentDate date) {
        int pos = getCalendrier().getFirstDay().getNbrDayTo(date);
        Log.d("Event", "position = " + pos + " for date " + date);
        Log.d("Position", "offset : " + pos);
        return pos;
    }

    public Calendrier getCalendrier() {
        return calendrier;
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

    private void setOnClickDay(TextView dayClicked, int day) {
        if (dayClicked != null)
            dayClicked.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener() {
                @Override
                protected void onClick() {
                    setCurrDate(currDate.getDateOfDayOfWeek(day));
                    super.onClick();
                }
            }));

    }

    /**
     * met les numéros des jours et leur couleur de fond adapté
     *
     * @param numDay le label du jour
     * @param day    son numéro de la semaine
     */
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
                                    NAVIGATION WEEKS
     ########################################################################*/

    public void switchToPrevWeek() {
        int sens = -1;

        setAnimationDirection(sens);
        setCurrDate(getCurrDate().prevWeek());
    }

    public void switchToNextWeek() {
        int sens = -1;

        setAnimationDirection(sens);
        setCurrDate(getCurrDate().nextWeek());
    }

    /*########################################################################
                                    ACTION BAR
     ########################################################################*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return new MenuItemClickActivities(this).onMenuItemClick(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activities, menu);
        return super.onCreateOptionsMenu(menu);
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