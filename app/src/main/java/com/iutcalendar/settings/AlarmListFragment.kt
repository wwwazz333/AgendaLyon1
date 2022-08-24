package com.iutcalendar.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.Alarm
import com.iutcalendar.alarm.AlarmAdapter
import com.iutcalendar.alarm.PersonalAlarmManager
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.menu.AbstractFragmentWitheMenu
import com.univlyon1.tools.agenda.R

class AlarmListFragment : AbstractFragmentWitheMenu() {
    private var recyclerViewAlarm: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().invalidateOptionsMenu()
        val view = inflater.inflate(R.layout.fragment_alarm_list, container, false)
        recyclerViewAlarm = view.findViewById(R.id.recycle_alarm)
        updateAlarm()
        return view
    }

    private fun updateAlarm() {
        Alarm.setUpAlarm(
            context,
            Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(context)))
        )
        val adapter = AlarmAdapter(
            PersonalAlarmManager.getInstance(context).allAlarmToList
        ) { saveAlarm() }
        recyclerViewAlarm!!.adapter = adapter
        val layout = LinearLayoutManager(activity)
        recyclerViewAlarm!!.layoutManager = layout
        saveAlarm()
        Log.d(
            "Alarm",
            "updateAlarm : " + PersonalAlarmManager.getInstance(context).allAlarmToList.size
        )
    }

    private fun saveAlarm() {
        PersonalAlarmManager.getInstance(context).save(context)
    }

    /*#################MENU BAR#################*/
    override fun clickMenu(item: MenuItem) {
        val id = item.itemId
        if (id == R.id.mybutton) {
            updateAlarm()
        }
    }
}