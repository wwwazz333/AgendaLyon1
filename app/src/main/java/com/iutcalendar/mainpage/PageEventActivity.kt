package com.iutcalendar.mainpage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.event.changement.ChangeDialog
import com.iutcalendar.mainpage.ui.main.SectionsPagerAdapter
import com.iutcalendar.menu.MenuItemClickActivities
import com.iutcalendar.service.WorkUpdate
import com.iutcalendar.settings.SettingsApp
import com.iutcalendar.swiping.GestureEventManager
import com.iutcalendar.swiping.TouchGestureListener
import com.iutcalendar.task.PersonnalCalendrier
import com.iutcalendar.widget.WidgetCalendar
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.ActivityPageEventBinding
import java.util.*

class PageEventActivity : AppCompatActivity() {
    var binding: ActivityPageEventBinding? = null
        private set
    private var fragmentTransaction: FragmentTransaction? = null
    private var currDate: CurrentDate = CurrentDate()
    private var currDateLabel: TextView? = null
    private var nameDayLayout: LinearLayout? = null
    private var dayOfWeek: LinearLayout? = null
    var calendrier: Calendrier? = null
    private var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Global", "PageEventActivity start")
        if (SettingsApp.adapteTheme(this)) { //it will restart, I don't know why
            return
        }
        SettingsApp.setLocale(resources, DataGlobal.getLanguage(applicationContext))
        binding = ActivityPageEventBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initVariable()
        PersonnalCalendrier.getInstance(applicationContext)
        initActionBar()

        //init Calendrier
        initCalendar()
        setCurrDate(dateToLaunchAtFirst)
        initPageViewEvent()
        initGestureSwipeWeek()
        initAllOnClickEvents()


        //affiche la dialog si changements depuis la dernière fois (notif ou non)
        var nombreChange: Int
        if (DataGlobal.getSavedInt(this, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY).also { nombreChange = it } > 0) {
            showChangedEvent(nombreChange)
            DataGlobal.save(this, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY, 0)
        } else {
            Log.d("Extra", "no changes")
        }

        //démarre le service d'arrière-plan avec interval
//        ForegroundServiceUpdate.start(getApplicationContext());
//        UpdateBackgroundJobServices.startScheduleJobBackground(this);
        WorkUpdate.startBackgroundWork(this)


        //update
        update(null)
        initAds()
        /*####Testing feature#####*/Log.d("Global", "PageEventActivity end")
    }

    private fun initAds() {
        MobileAds.initialize(this) { }
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    /**
     * init all on click events
     */
    private fun initAllOnClickEvents() {
        //back to current date
        currDateLabel!!.setOnClickListener { setCurrDate(CurrentDate()) }


        //Click on day
        setOnClickDay(binding!!.dayLundi, 0)
        setOnClickDay(binding!!.dayMardi, 1)
        setOnClickDay(binding!!.dayMercredi, 2)
        setOnClickDay(binding!!.dayJeudi, 3)
        setOnClickDay(binding!!.dayVendredi, 4)
        setOnClickDay(binding!!.daySamedi, 5)
        setOnClickDay(binding!!.dayDimanche, 6)
        setOnClickDay(binding!!.lundiNum, 0)
        setOnClickDay(binding!!.mardiNum, 1)
        setOnClickDay(binding!!.mercrediNum, 2)
        setOnClickDay(binding!!.jeudiNum, 3)
        setOnClickDay(binding!!.vendrediNum, 4)
        setOnClickDay(binding!!.samediNum, 5)
        setOnClickDay(binding!!.dimancheNum, 6)
    }

    /**
     * init gesture to switch between weeks
     */
    private fun initGestureSwipeWeek() {
        val childCount = nameDayLayout!!.childCount
        for (i in 0 until childCount) {
            nameDayLayout!!.getChildAt(i).setOnTouchListener(TouchGestureListener(applicationContext, GestureWeekListener()))
        }
        nameDayLayout!!.setOnTouchListener(TouchGestureListener(applicationContext, GestureWeekListener()))
        dayOfWeek!!.setOnTouchListener(TouchGestureListener(applicationContext, GestureWeekListener()))
    }

    /**
     * init viewPager and sectionsPagerAdapter to see events
     */
    private fun initPageViewEvent() {
        Log.d("Event", "cration Section page adapter------------------")
        viewPager = binding!!.viewPager
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, calendrier)
        viewPager!!.adapter = sectionsPagerAdapter
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                Log.d("Page", "new page : $position")
                setCurrDate(CurrentDate(calendrier?.firstDay).addDay(position))
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        setPositionPageToCurrDate()
    }

    private fun updatePageViewEvent() {
        Log.d("Event", "update Section page adapter------------------")
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, calendrier)
        viewPager!!.removeAllViews()
        viewPager!!.adapter = sectionsPagerAdapter
        setPositionPageToCurrDate()
    }//pcq l'event s'affiche toujours au bout de 30min

    /**
     * @return the date the activity has to be launch
     */
    private val dateToLaunchAtFirst: CurrentDate
        get() {
            var dateToLaunch = CurrentDate()
            Log.d("Widget", "main : " + intent.getBooleanExtra("launch_next_event", false))
            if (intent.getBooleanExtra("launch_next_event", false)) {
                dateToLaunch.add(GregorianCalendar.MINUTE, WidgetCalendar.DELAY_AFTER_EVENT_PASSED) //pcq l'event s'affiche toujours au bout de 30min
                val es = calendrier!!.getNext2EventAfter(dateToLaunch)
                if (es[0] != null) {
                    dateToLaunch = CurrentDate(es[0]?.date)
                }
            } else {
                Log.d("Widget", "default date")
            }
            return dateToLaunch
        }

    private fun initCalendar() {
        val fileCal = FileGlobal.getFileDownload(applicationContext)
        val str = FileGlobal.readFile(fileCal)
        calendrier = Calendrier(str)
    }

    private fun initVariable() {
        currDate = CurrentDate()
        fragmentTransaction = supportFragmentManager.beginTransaction()
        nameDayLayout = findViewById(R.id.nameDayLayout)
        dayOfWeek = findViewById(R.id.dayOfWeek)
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            actionBar.setCustomView(R.layout.action_bar_main_page)
            currDateLabel = actionBar.customView.findViewById(R.id.title)
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
    fun update(onFinishListener: (() -> Unit)?) {
        if (!updating) {
            Log.d("Update", "start")
            updating = true
            Thread {
                FileGlobal.updateAndGetChange(this, calendrier) { context: Context?, intent: Intent? -> startActivity(intent) }
                runOnUiThread {
                    Log.d("Debug", "appeler d'epuis update")
                    updatePageViewEvent()
                }
                if (onFinishListener != null) {
                    onFinishListener()
                }
                updating = false
                Log.d("Update", "end")
            }.start()
        }
    }

    private fun showChangedEvent(nombreChangements: Int) {
        //FIXME pas afficher quand charger agenda
        ChangeDialog(this, nombreChangements).show()
    }

    /*########################################################################
                               GETTERS & SETTERS
    ########################################################################*/

    fun setCurrDate(newCurrentDate: CurrentDate?) {
//        if (newDate.sameDay(currDate)) { //some bugs
//            currDateLabel.setText(currDate.getRelativeDayName(getBaseContext()));
//            return;
//        }

        if (newCurrentDate != null) {
            var newDate = newCurrentDate
            Log.d("Date", "$currDate set curr date to $newDate")
            if (calendrier != null && calendrier!!.firstDay != null && calendrier!!.lastDay != null) {
                if (newDate < calendrier!!.firstDay!!) {
                    newDate = CurrentDate(calendrier!!.firstDay)
                } else if (newDate > calendrier!!.lastDay!!) {
                    newDate = CurrentDate(calendrier!!.lastDay)
                }
            }
            currDate.set(newDate)
            currDateLabel!!.text = currDate.getRelativeDayName(baseContext)
            Log.d("ActionBar", currDate.getRelativeDayName(baseContext))
            setPositionPageToCurrDate()
            setDaysOfWeek()
        }

    }

    private fun setPositionPageToCurrDate() {
        if (calendrier != null && calendrier!!.firstDay != null && viewPager != null) {
            viewPager!!.currentItem = getPosOfDate(currDate)
        }
    }

    private fun getPosOfDate(date: CurrentDate?): Int {
        if (calendrier != null && calendrier!!.firstDay != null) {
            val pos = calendrier!!.firstDay!!.getNbrDayTo(date)
            Log.d("Event", "position = $pos for date $date")
            Log.d("Position", "offset : $pos")
            return pos
        }
        return 0
    }

    private fun setDaysOfWeek() {
        setNumOfMonthAndSelected(binding!!.lundiNum, 0)
        setNumOfMonthAndSelected(binding!!.mardiNum, 1)
        setNumOfMonthAndSelected(binding!!.mercrediNum, 2)
        setNumOfMonthAndSelected(binding!!.jeudiNum, 3)
        setNumOfMonthAndSelected(binding!!.vendrediNum, 4)
        setNumOfMonthAndSelected(binding!!.samediNum, 5)
        setNumOfMonthAndSelected(binding!!.dimancheNum, 6)
    }

    private fun setAnimationGoRight() {
        fragmentTransaction!!.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun setAnimationGoLeft() {
        fragmentTransaction!!.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    private fun setAnimationDirection(d: Int) {
        if (d > 0) {
            setAnimationGoRight()
        } else {
            setAnimationGoLeft()
        }
    }

    private fun setOnClickDay(dayClicked: TextView?, day: Int) {
        dayClicked?.setOnTouchListener(TouchGestureListener(applicationContext, object : GestureWeekListener() {
            override fun onClick() {
                setCurrDate(currDate.getDateOfDayOfWeek(day))
                super.onClick()
            }
        }))
    }

    /**
     * Met les numéros des jours et leur couleur de fond adapté
     *
     * @param numDay le label du jour
     * @param day    son numéro de la semaine
     */
    private fun setNumOfMonthAndSelected(numDay: TextView, day: Int) {
        numDay.text = currDate.getDateOfDayOfWeek(day).day.toString()
        if (currDate.day == currDate.getDateOfDayOfWeek(day).day) {
            numDay.setBackgroundColor(Color.parseColor("#FFC41442"))
        } else if (currDate.getDateOfDayOfWeek(day).sameDay(CurrentDate())) {
            numDay.setBackgroundColor(Color.parseColor("#88C41442"))
        } else {
            numDay.setBackgroundColor(Color.argb(0f, 1.0f, 1.0f, 1.0f))
        }
    }

    /*########################################################################
                                    NAVIGATION WEEKS
     ########################################################################*/
    fun switchToPrevWeek() {
        val sens = -1
        setAnimationDirection(sens)
        setCurrDate(currDate.prevWeek())
    }

    fun switchToNextWeek() {
        val sens = -1
        setAnimationDirection(sens)
        setCurrDate(currDate.nextWeek())
    }

    /*########################################################################
                                    ACTION BAR
     ########################################################################*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return MenuItemClickActivities(this).onMenuItemClick(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_activities, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*########################################################################
                                    GESTION EVENT
     ########################################################################*/
    private open inner class GestureWeekListener : GestureEventManager() {
        override fun onSwipeRight() {
            super.onSwipeRight()
            switchToPrevWeek()
        }

        override fun onSwipeLeft() {
            super.onSwipeLeft()
            switchToNextWeek()
        }
    }

    companion object {
        private var updating = false
    }
}