package com.iutcalendar.mainpage;

import android.app.AlertDialog;
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
import com.calendar.iutcalendar.R;
import com.calendar.iutcalendar.databinding.ActivityPageEventBinding;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.mainpage.ui.main.SectionsPagerAdapter;
import com.iutcalendar.menu.MenuItemClickActivities;
import com.iutcalendar.service.ForgroundServiceUpdate;
import com.iutcalendar.settings.SettingsApp;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.widget.WidgetCalendar;

import java.io.File;
import java.util.GregorianCalendar;

public class PageEventActivity extends AppCompatActivity {

    private static boolean active = false, updating = false;
    private ActivityPageEventBinding binding;
    private FragmentTransaction fragmentTransaction;
    private CurrentDate currDate;
    private TextView currDateLabel;
    private LinearLayout nameDayLayout;
    private LinearLayout dayOfWeek;
    private Calendrier calendrier;

    private ViewPager viewPager;

    public static boolean isActive() {
        return active;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Global", "PageEventActivity start");
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));

        binding = ActivityPageEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initVariable();
        PersonnalCalendrier.getInstance(getApplicationContext());

        initActionBar();

        //init Calendrier
        initCalendar();


        CurrentDate dateToLaunche = getDateToLaunchAtFirst();

        initPageViewEvent();


        setCurrDate(dateToLaunche);


        initGestureSwipeWeek();


        initAllOnClickEvents();


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
    }

    /**
     * @return the date the activity has to be launch
     */
    private CurrentDate getDateToLaunchAtFirst() {
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
        return dateToLaunche;
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


    /*########################################################################
                                     UPDATE
    ########################################################################*/

    /**
     * update the calendar file
     */
    public void update() {
        Log.d("Update", "start");
        if (updating) return;
        updating = true;
        new Thread(() -> {
            FileGlobal.updateAndGetChange(getApplicationContext(), calendrier, ((context, intent) -> startActivity(intent)));

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

    /*########################################################################
                               GETTERS & SETTERS
    ########################################################################*/
    public CurrentDate getCurrDate() {
        return this.currDate;
    }

    public void setCurrDate(CurrentDate newDate) {
        Log.d("Date", "set curr date");

        if (getCalendrier() != null && getCalendrier().getFirstDay() != null && getCalendrier().getLastDay() != null) {
            if (newDate.compareTo(getCalendrier().getFirstDay()) < 0) {
                newDate = new CurrentDate(getCalendrier().getFirstDay());
            } else if (newDate.compareTo(getCalendrier().getLastDay()) > 0) {
                newDate = new CurrentDate(getCalendrier().getLastDay());
            }
        }

        this.currDate.set(newDate);

        currDateLabel.setText(currDate.getRelativeDayName(getBaseContext()));

        if (getCalendrier() != null && getCalendrier().getFirstDay() != null) {
            int pos = getCalendrier().getFirstDay().getNbrDayTo(newDate);
            Log.d("Position", "offset : " + pos);
            viewPager.setCurrentItem(pos);
        }

        setDaysOfWeek();
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
        if(dayClicked != null) dayClicked.setOnTouchListener(new TouchGestureListener(getApplicationContext(), new PageEventActivity.GestureWeekListener() {
            @Override
            protected void onClick() {
                setCurrDate(currDate.getDateOfDayOfWeek(day));
                super.onClick();
            }
        }));

    }

    /**
     * met les numéro des jours et leur couleur de fond adapté
     * @param numDay le label du jour
     * @param day son numéro de la semaine
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
        inflater.inflate(R.menu.popup_menu_activities, menu);
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