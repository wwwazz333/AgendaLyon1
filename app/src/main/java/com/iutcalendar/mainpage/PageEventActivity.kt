package com.iutcalendar.mainpage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.CachedData
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.event.changement.ChangeDialog
import com.iutcalendar.mainpage.ui.main.SectionsPagerAdapter
import com.iutcalendar.menu.MenuItemClickActivities
import com.iutcalendar.service.WorkUpdate
import com.iutcalendar.settings.SettingsApp
import com.iutcalendar.swiping.GestureEventManager
import com.iutcalendar.swiping.TouchGestureListener
import com.iutcalendar.task.PersonalCalendrier
import com.iutcalendar.widget.WidgetCalendar
import com.univlyon1.tools.agenda.BuildConfig
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.ActivityPageEventBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class PageEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityPageEventBinding
        private set
    private var fragmentTransaction: FragmentTransaction? = null
    private var currDate: CurrentDate = CurrentDate()
    private var currDateLabel: TextView? = null
    var calendrier: Calendrier
        get() = CachedData.calendrier
        set(value) {
            CachedData.calendrier = value
        }
    private val viewPager: ViewPager2
        get() = binding.viewPager

    var savedInstanceCalendrier: Calendrier? = null


    override fun onResume() {
        super.onResume()
        Log.d("PageEventActivity", "onResume")
        if (hasToReload) {
            currDate = CurrentDate()
            initPageViewEvent()
            hasToReload = false
        } else savedInstanceCalendrier?.let {
            if (it != CachedData.calendrier) {
                initPageViewEvent()
            }
            Log.d("PageEventActivity", "new has = ${CachedData.calendrier.events.size}\t prev has ${it.events.size}")
        }
    }

    override fun onPause() {
        super.onPause()
        savedInstanceCalendrier = calendrier.clone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("PageEventActivity", "PageEventActivity start")
        binding = ActivityPageEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        PersonalCalendrier.getInstance(applicationContext)
        initActionBar()

        //init Calendrier
        initCalendar()
        setCurrDate(dateToLaunchAtFirst)
        initPageViewEvent()
        initGestureSwipeWeek()
        initAllOnClickEvents()


        //affiche la dialog si changements depuis la dernière fois (notif ou non)
        var nombreChange: Int
        if (DataGlobal.getSavedInt(this, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY)
                .also { nombreChange = it } > 0
        ) {
            showChangedEvent(nombreChange)
            DataGlobal.save(this, DataGlobal.NOMBRE_CHANGE_TO_DISPLAY, 0)
        } else {
            Log.d("Extra", "no changes")
        }

        //démarre le service en arrière-plan avec interval
        WorkUpdate.startBackgroundWork(this)

        //update
        update {}
        initAds()

        //if update show news
        if (DataGlobal.getSavedString(applicationContext, DataGlobal.LAST_UPDATE_TAG) != BuildConfig.VERSION_NAME) {
            DialogMessage.showInfo(this, baseContext.resources.getString(R.string.What_new), baseContext.resources.getString(R.string.news_msg)) {
                DataGlobal.save(applicationContext, DataGlobal.LAST_UPDATE_TAG, BuildConfig.VERSION_NAME)
            }
        }


        /*####Testing feature#####*/
        Log.d("PageEventActivity", "PageEventActivity end")

    }

    private fun initAds() {
        MobileAds.initialize(this) { }
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    /**
     * init all on click events
     */
    private fun initAllOnClickEvents() {
        //back to current date
        currDateLabel?.setOnClickListener { setCurrDate(CurrentDate()) }

        //Click on day
        binding.let {
            setOnClickDay(it.dayLundi, 0)
            setOnClickDay(it.dayMardi, 1)
            setOnClickDay(it.dayMercredi, 2)
            setOnClickDay(it.dayJeudi, 3)
            setOnClickDay(it.dayVendredi, 4)
            setOnClickDay(it.daySamedi, 5)
            setOnClickDay(it.dayDimanche, 6)

            setOnClickDay(it.lundiNum, 0)
            setOnClickDay(it.mardiNum, 1)
            setOnClickDay(it.mercrediNum, 2)
            setOnClickDay(it.jeudiNum, 3)
            setOnClickDay(it.vendrediNum, 4)
            setOnClickDay(it.samediNum, 5)
            setOnClickDay(it.dimancheNum, 6)
        }
    }

    /**
     * init gesture to switch between weeks
     */
    private fun initGestureSwipeWeek() {
        binding.nameDayLayout.apply {
            for (i in 0 until childCount) {
                getChildAt(i)
                    .setOnTouchListener(TouchGestureListener(applicationContext, GestureWeekListener()))
            }
            setOnTouchListener(
                TouchGestureListener(
                    applicationContext,
                    GestureWeekListener()
                )
            )
        }
        binding.dayOfWeek.apply {
            setOnTouchListener(
                TouchGestureListener(
                    applicationContext,
                    GestureWeekListener()
                )
            )
        }
    }

    /**
     * init viewPager and sectionsPagerAdapter to see events
     */
    fun initPageViewEvent() {
        Log.d("Event", "creation Section page adapter")
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("Page", "new page : $position")
                calendrier.firstDay?.let {
                    setCurrDate(CurrentDate(it).addDay(position))
                }

            }
        })

        setCurrDate(currDate)
        setPositionPageToCurrDate()
    }


    private fun updatePageViewEvent() {
        Log.d("Event", "update Section page adapter")

        viewPager.adapter?.notifyDataSetChanged()

        setPositionPageToCurrDate()
    }

    /**
     * @return the date the activity has to be launch
     */
    private val dateToLaunchAtFirst: CurrentDate
        get() {
            var dateToLaunch = CurrentDate()
            Log.d("Widget", "main : " + intent.getBooleanExtra("launch_next_event", false))
            if (intent.getBooleanExtra("launch_next_event", false)) {
                dateToLaunch.add(
                    GregorianCalendar.MINUTE,
                    WidgetCalendar.DELAY_AFTER_EVENT_PASSED
                ) //pcq event est toujours affiché toujours au bout de 30min
                val es = calendrier.getNext2EventAfter(dateToLaunch)
                es[0]?.date?.let { date ->
                    dateToLaunch = CurrentDate(date)
                }
            } else {
                Log.d("Widget", "default date")
            }
            return dateToLaunch
        }

    private fun initCalendar() {
        calendrier = Calendrier(FileGlobal.readFile(FileGlobal.getFileCalendar(applicationContext)))
    }

    private fun initVariable() {
        currDate = CurrentDate()
        fragmentTransaction = supportFragmentManager.beginTransaction()
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

    private val poolActionEndUpdate = ArrayDeque<() -> Unit>()

    /**
     * update the calendar file
     *
     * @param onFinishListener toujours appelé quand la maj se fini
     */
    fun update(onFinishListener: (() -> Unit)) {
        poolActionEndUpdate.add(onFinishListener)
        if (!updating) {
            Log.d("Update", "start")
            updating = true

            lifecycleScope.launch(Dispatchers.IO) {
                FileGlobal.updateAndGetChange(
                    this@PageEventActivity,
                    calendrier
                ) { _: Context?, intent: Intent? -> startActivity(intent) }

                withContext(Dispatchers.Main) {
                    updatePageViewEvent()
                }
                while (poolActionEndUpdate.isNotEmpty()) {
                    poolActionEndUpdate.pop()()
                }
                updating = false
                Log.d("Update", "end")
            }
        }
    }


    private fun showChangedEvent(nombreChangements: Int) {
        ChangeDialog(this, nombreChangements).show()
    }

    /*########################################################################
                               GETTERS & SETTERS
    ########################################################################*/

    fun setCurrDate(newCurrentDate: CurrentDate?) {
        if (newCurrentDate != null) {
            var newDate: CurrentDate = newCurrentDate
            Log.d("Date", "$currDate set curr date to $newDate")
            calendrier.firstDay?.let { firstDay ->
                calendrier.lastDay?.let { lastDay ->
                    if (newDate < firstDay) {
                        newDate = CurrentDate(firstDay)
                    } else if (newDate > lastDay) {
                        newDate = CurrentDate(lastDay)
                    }
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
        if (calendrier.firstDay != null) {
            viewPager.currentItem = getPosOfDate(currDate)
        }
    }

    private fun getPosOfDate(date: CurrentDate?): Int {
        if (calendrier.firstDay != null) {
            val pos = calendrier.firstDay!!.getNbrDayTo(date)
            Log.d("Event", "position = $pos for date $date")
            Log.d("Position", "offset : $pos")
            return pos
        }
        return 0
    }

    private fun setDaysOfWeek() {
        setNumOfMonthAndSelected(binding.lundiNum, 0)
        setNumOfMonthAndSelected(binding.mardiNum, 1)
        setNumOfMonthAndSelected(binding.mercrediNum, 2)
        setNumOfMonthAndSelected(binding.jeudiNum, 3)
        setNumOfMonthAndSelected(binding.vendrediNum, 4)
        setNumOfMonthAndSelected(binding.samediNum, 5)
        setNumOfMonthAndSelected(binding.dimancheNum, 6)
    }

    private fun setOnClickDay(dayClicked: TextView?, day: Int) {
        dayClicked?.apply {
            setOnTouchListener(
                TouchGestureListener(
                    applicationContext,
                    object : GestureWeekListener() {
                        override fun onClick() {
                            setCurrDate(currDate.getDateOfDayOfWeek(day))
                            super.onClick()
                        }
                    })
            )
        }
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
        setCurrDate(currDate.prevWeek())
    }

    fun switchToNextWeek() {
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
        var hasToReload = false
    }
}