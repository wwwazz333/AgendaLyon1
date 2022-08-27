package com.iutcalendar.mainpage.ui.main

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.event.EventFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager?, private val calendrier: Calendrier?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var countDay = 0

    init {
        Log.d("Page", "end création SectionPagerAdapter")
        countDay = if (calendrier?.firstDay == null) 1 else calendrier.firstDay!!.getNbrDayTo(calendrier.lastDay) + 1
    }

    override fun getItem(position: Int): Fragment {
        val dateToLaunch: CurrentDate = if (calendrier?.firstDay != null) {
            CurrentDate(calendrier.firstDay).addDay(position)
        } else {
            CurrentDate()
        }
        Log.d("Event", "get item at $dateToLaunch at position $position")
        return EventFragment(calendrier, dateToLaunch, FileGlobal.getFileCalendar(mContext))
    }

    override fun getPageTitle(position: Int): CharSequence {
        return DataGlobal.DAYS_OF_WEEK[position].toString()
    }

    override fun getCount(): Int {
        return countDay
    }
}