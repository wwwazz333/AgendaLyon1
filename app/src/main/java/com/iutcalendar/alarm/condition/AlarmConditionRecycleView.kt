package com.iutcalendar.alarm.condition

import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.calendrier.DateCalendrier
import com.univlyon1.tools.agenda.R
import java.util.*

class AlarmConditionRecycleView(var context: Context?, var list: List<AlarmCondtion>?, private var updateListener: () -> Unit, private var reloadListener: () -> Unit) : RecyclerView.Adapter<AlarmConditionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmConditionViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.condition_alarm_card, parent, false)
        return AlarmConditionViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: AlarmConditionViewHolder, position: Int) {
        val alarmConstraint = list!![position]
        holder.beginHour.setText(DateCalendrier.Companion.timeLongToString(alarmConstraint.beging))
        holder.endHour.setText(DateCalendrier.Companion.timeLongToString(alarmConstraint.end))
        holder.ringHour.setText(DateCalendrier.Companion.timeLongToString(alarmConstraint.alarmAt))
        holder.beginHour.setOnClickListener { view: View? ->
            AlarmRing.Companion.askTime(context, OnTimeSetListener { view1: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.beging = DateCalendrier.Companion.getHourInMillis(hourOfDay, minute)
                updateListener()
            })
        }
        holder.endHour.setOnClickListener { view: View? ->
            AlarmRing.Companion.askTime(context, OnTimeSetListener { view1: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.end = DateCalendrier.Companion.getHourInMillis(hourOfDay, minute)
                updateListener()
            })
        }
        holder.ringHour.setOnClickListener { view: View? ->
            AlarmRing.Companion.askTime(context, OnTimeSetListener { view1: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.alarmAt = DateCalendrier.Companion.getHourInMillis(hourOfDay, minute)
                updateListener()
            })
        }
        holder.delBtn.setOnClickListener { view: View? ->
            AlarmConditionManager.Companion.getInstance(context)!!.removeCondition(position)
            reloadListener()
        }


        //setCheckable days
        initDayCheck(alarmConstraint, holder.monday, GregorianCalendar.MONDAY)
        initDayCheck(alarmConstraint, holder.tuesday, GregorianCalendar.TUESDAY)
        initDayCheck(alarmConstraint, holder.wednesday, GregorianCalendar.WEDNESDAY)
        initDayCheck(alarmConstraint, holder.thursday, GregorianCalendar.THURSDAY)
        initDayCheck(alarmConstraint, holder.friday, GregorianCalendar.FRIDAY)
        initDayCheck(alarmConstraint, holder.saturday, GregorianCalendar.SATURDAY)
        initDayCheck(alarmConstraint, holder.sunday, GregorianCalendar.SUNDAY)
    }

    private fun initDayCheck(alarmConstraint: AlarmCondtion, check: CheckedTextView?, value: Int) {
        if (alarmConstraint.daysEnabled.contains(value)) {
            check!!.isChecked = true
        }
        check!!.setOnClickListener { v: View? ->
            check.toggle()
            if (check.isChecked) alarmConstraint.daysEnabled.add(value) else alarmConstraint.daysEnabled.remove(Integer.valueOf(value))
            updateListener()
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}