package com.iutcalendar.mainpage.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.CachedData
import com.iutcalendar.event.EventFragment
import com.iutcalendar.mainpage.PageEventActivity


class SectionsPagerAdapter(private val parent: PageEventActivity) :
    FragmentStateAdapter(parent) {
    private var countDay = 0


    init {
        Log.d("Page", "end création SectionPagerAdapter")
        countDay =
            if (CachedData.calendrier.firstDay == null) 1 else CachedData.calendrier.firstDay!!.getNbrDayTo(CachedData.calendrier.lastDay) + 1
    }

    override fun createFragment(position: Int): Fragment {
        val dateToLaunch: CurrentDate = CachedData.calendrier.firstDay?.let { firstDay ->
            Log.d("SectionsPagerAdapter", "firstDay is  $firstDay")
            CurrentDate(firstDay).addDay(position)
        } ?: CurrentDate()
        Log.d("SectionsPagerAdapter", "get item at $dateToLaunch at position $position")
        return EventFragment(dateToLaunch)
    }


    override fun getItemCount(): Int {
        return countDay
    }
}