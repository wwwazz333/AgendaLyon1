package com.iutcalendar.settings

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.Alarm
import com.iutcalendar.alarm.AlarmAdapter
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.alarm.PersonalAlarmManager
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.menu.AbstractFragmentWitheMenu
import com.univlyon1.tools.agenda.R
import java.util.*

class AlarmListFragment : AbstractFragmentWitheMenu(), DatePickerDialog.OnDateSetListener {
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
            requireContext(),
            Calendrier(FileGlobal.readFile(FileGlobal.getFileCalendar(requireContext())))
        )
        val alarmAdapter = AlarmAdapter(requireContext())
        recyclerViewAlarm?.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        saveAlarm()
        Log.d(
            "Alarm",
            "updateAlarm : " + PersonalAlarmManager.getInstance(requireContext()).allAlarmToList.size
        )
    }

    private fun saveAlarm() {
        PersonalAlarmManager.getInstance(requireContext()).save(requireContext())
    }


    private fun addAlarm() {
        val today = CurrentDate()
        DatePickerDialog(requireContext(), this, today.year, today.get(GregorianCalendar.MONTH), today.get(GregorianCalendar.DAY_OF_MONTH)).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        AlarmRing.askTime(
            requireContext(),
            getString(R.string.New_alarm), { _: TimePicker?, hourOfDay: Int, minute: Int ->


                val date = GregorianCalendar().apply {
                    set(GregorianCalendar.YEAR, year)
                    set(GregorianCalendar.MONTH, month)
                    set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth)
                    set(GregorianCalendar.HOUR_OF_DAY, hourOfDay)
                    set(GregorianCalendar.MINUTE, minute)
                    set(GregorianCalendar.SECOND, 0)
                }

                PersonalAlarmManager.getInstance(requireContext()).addNewAlarm(
                    requireContext(), CurrentDate(), AlarmRing(
                        date.timeInMillis,
                        isDeletable = true
                    )
                )

                recyclerViewAlarm?.adapter?.notifyDataSetChanged()
            },
            CurrentDate().hour, CurrentDate().minute
        )
    }

    /*#################MENU BAR#################*/
    override fun clickMenu(item: MenuItem) {
        when (item.itemId) {
            R.id.updateBtn -> {
                updateAlarm()
            }
            R.id.addBtn -> {
                addAlarm()
            }
        }
    }


}