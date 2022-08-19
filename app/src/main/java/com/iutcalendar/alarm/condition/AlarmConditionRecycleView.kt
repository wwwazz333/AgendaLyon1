package com.iutcalendar.alarm.condition

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.calendrier.DateCalendrier
import com.univlyon1.tools.agenda.R
import java.util.*

class AlarmConditionRecycleView(
    var context: Context?,
    var list: List<AlarmCondition>?,
    private var updateItemListener: (itemPos: Int) -> Unit,
    private var removeItemListener: (itemPos: Int) -> Unit,
    private var saveListener: () -> Unit
) : RecyclerView.Adapter<AlarmConditionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmConditionViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.condition_alarm_card, parent, false)
        return AlarmConditionViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: AlarmConditionViewHolder, position: Int) {
        val alarmConstraint = list!![position]
        holder.beginHour.text = DateCalendrier.timeLongToString(alarmConstraint.begin)
        holder.endHour.text = DateCalendrier.timeLongToString(alarmConstraint.end)
        holder.ringHour.text = DateCalendrier.timeLongToString(alarmConstraint.alarmAt)
        holder.beginHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.begin = DateCalendrier.getHourInMillis(hourOfDay, minute)
                updateItemListener(position)
            }
        }
        holder.endHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.end = DateCalendrier.getHourInMillis(hourOfDay, minute)
                updateItemListener(position)
            }
        }
        holder.ringHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.alarmAt = DateCalendrier.getHourInMillis(hourOfDay, minute)
                updateItemListener(position)
            }
        }
        holder.delBtn.setOnClickListener {
            AlarmConditionManager.getInstance(context).removeCondition(position)
            removeItemListener(position)
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

    private fun initDayCheck(alarmConstraint: AlarmCondition, check: CheckedTextView?, value: Int) {
        if (alarmConstraint.daysEnabled.contains(value)) {
            check!!.isChecked = true
        }
        check!!.setOnClickListener {
            check.toggle()
            if (check.isChecked) alarmConstraint.daysEnabled.add(value)
            else alarmConstraint.daysEnabled.remove(Integer.valueOf(value))

            saveListener()
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}