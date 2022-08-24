package com.iutcalendar.alarm.condition

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.calendrier.DateCalendrier
import com.univlyon1.tools.agenda.R
import java.util.*

class AlarmConditionAdapter(
    var context: Context?,
    var list: List<AlarmCondition>?,
    private var saveListener: () -> Unit
) : RecyclerView.Adapter<AlarmConditionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmConditionViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.condition_alarm_card, parent, false)
        return AlarmConditionViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: AlarmConditionViewHolder, position: Int) {
        Log.d("AlarmConditionRecycleView", position.toString())

        val alarmConstraint = list!![position]
        holder.beginHour.text = DateCalendrier.timeLongToString(alarmConstraint.begin)
        holder.endHour.text = DateCalendrier.timeLongToString(alarmConstraint.end)
        holder.ringHour.text = DateCalendrier.timeLongToString(alarmConstraint.alarmAt)
        holder.beginHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.begin = DateCalendrier.getHourInMillis(hourOfDay, minute)
                notifyItemChanged(position)
                saveListener()
            }
        }
        holder.endHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.end = DateCalendrier.getHourInMillis(hourOfDay, minute)
                notifyItemChanged(position)
                saveListener()
            }
        }
        holder.ringHour.setOnClickListener {
            AlarmRing.askTime(context) { _: TimePicker?, hourOfDay: Int, minute: Int ->
                alarmConstraint.alarmAt = DateCalendrier.getHourInMillis(hourOfDay, minute)
                notifyItemChanged(position)
                saveListener()
            }
        }
        holder.delBtn.setOnClickListener {
            AlarmConditionManager.getInstance(context).removeCondition(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
            saveListener()
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

    private fun initDayCheck(alarmCondition: AlarmCondition, check: CheckedTextView, value: Int) {
        check.isChecked = alarmCondition.daysEnabled.contains(value)
        check.setOnClickListener {
            check.toggle()
            if (check.isChecked) alarmCondition.daysEnabled.add(value)
            else alarmCondition.daysEnabled.remove(Integer.valueOf(value))

            saveListener()
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}