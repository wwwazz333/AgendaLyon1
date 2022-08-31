package com.iutcalendar.mainpage.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.event.EventFragment
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.snackbar.ErrorSnackBar
import com.univlyon1.tools.agenda.R


class SectionsPagerAdapter(private val parent: PageEventActivity, private val calendrier: Calendrier?) :
    FragmentStateAdapter(parent) {
    private var countDay = 0

    init {
        Log.d("Page", "end création SectionPagerAdapter")
        countDay = if (calendrier?.firstDay == null) 1 else calendrier.firstDay!!.getNbrDayTo(calendrier.lastDay) + 1
    }

    override fun createFragment(position: Int): Fragment {
        if (calendrier == null) {
            ErrorSnackBar.showError(parent.binding.root, parent.getString(R.string.error_should_reboot))
        }
        val dateToLaunch: CurrentDate = calendrier?.firstDay?.let { firstDay ->
            Log.d("SectionsPagerAdapter", "firstDay is  $firstDay")
            CurrentDate(firstDay).addDay(position)
        } ?: CurrentDate()
        Log.d("SectionsPagerAdapter", "get item at $dateToLaunch at position $position")
        return EventFragment(calendrier, dateToLaunch, FileGlobal.getFileCalendar(parent))
    }


    override fun getItemCount(): Int {
        return countDay
    }
}