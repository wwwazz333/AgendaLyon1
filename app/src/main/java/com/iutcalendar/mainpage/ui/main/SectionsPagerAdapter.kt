package com.iutcalendar.mainpage.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.event.EventFragment


class SectionsPagerAdapter(private val parent: FragmentActivity, private val calendrier: Calendrier?) :
    FragmentStateAdapter(parent) {
    private var countDay = 0

    init {
        Log.d("Page", "end création SectionPagerAdapter")
        countDay = if (calendrier?.firstDay == null) 1 else calendrier.firstDay!!.getNbrDayTo(calendrier.lastDay) + 1
    }

    override fun createFragment(position: Int): Fragment {
        val dateToLaunch: CurrentDate = if (calendrier?.firstDay != null) {
            CurrentDate(calendrier.firstDay!!).addDay(position)
        } else {
            CurrentDate()
        }
        Log.d("Event", "get item at $dateToLaunch at position $position")
        return EventFragment(calendrier, dateToLaunch, FileGlobal.getFileCalendar(parent))
    }

//    override fun getPageTitle(position: Int): CharSequence {
//        return DataGlobal.DAYS_OF_WEEK[position].toString()
//    }

    override fun getItemCount(): Int {
        return countDay
    }
}