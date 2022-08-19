package com.iutcalendar.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.alarm.condition.AlarmCondition
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.alarm.condition.AlarmConditionRecycleView
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.menu.AbstractFragmentWitheMenu
import com.univlyon1.tools.agenda.R

class AlarmConditionFragment : AbstractFragmentWitheMenu() {
    private var recyclerViewConstraint: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().invalidateOptionsMenu()
        val view = inflater.inflate(R.layout.fragment_alarm_condition, container, false)
        initVariable(view)
        updateConditions()
        return view
    }

    private fun initVariable(view: View) {
        recyclerViewConstraint = view.findViewById(R.id.recycleView)
    }

    private fun updateConditions() {
        val adapter = AlarmConditionRecycleView(
            context,
            AlarmConditionManager.getInstance(context).allConditions,
            { itemPos: Int ->
                recyclerViewConstraint?.adapter?.notifyItemChanged(itemPos)
                saveConditions()
            }, { itemPos: Int ->
                recyclerViewConstraint?.adapter?.notifyItemRemoved(itemPos)
                recyclerViewConstraint?.adapter?.notifyItemRangeChanged(
                    itemPos,
                    AlarmConditionManager.getInstance(context).allConditions?.size!!
                )
                saveConditions()
            })
        { saveConditions() }
        recyclerViewConstraint?.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity)
        }

        //save si des changements de constraint ont été fait
        saveConditions()
        Log.d("Constraint", "updateConstraint")
    }

    private fun saveConditions() {
        AlarmConditionManager.getInstance(context).save(context)
    }

    private fun addCondition() {

        //demande heure début
        AlarmRing.askTime(
            context,
            getString(R.string.BorneInf)
        ) { _: TimePicker?, hourOfDayBegin: Int, minuteBegin: Int ->
            val begin: Long = DateCalendrier.getHourInMillis(hourOfDayBegin, minuteBegin)

            //demande heure fin
            AlarmRing.askTime(
                context,
                getString(R.string.BorneSup)
            ) { _: TimePicker?, hourOfDayEnd: Int, minuteEnd: Int ->
                val end: Long = DateCalendrier.getHourInMillis(hourOfDayEnd, minuteEnd)
                if (begin > end) {
                    DialogMessage.showWarning(
                        context,
                        getString(R.string.Interval),
                        getString(R.string.born_sup_et_inf_inverser)
                    )
                } else {
                    //demande heure sonnerie
                    AlarmRing.askTime(
                        context,
                        getString(R.string.TimeToRing)
                    ) { _: TimePicker?, hourOfDayAlarmAt: Int, minuteAlarmAt: Int ->
                        val alarmAt: Long =
                            DateCalendrier.getHourInMillis(hourOfDayAlarmAt, minuteAlarmAt)
                        AlarmConditionManager.getInstance(context)
                            .addCondition(AlarmCondition(begin, end, alarmAt))
                        updateConditions()
                    }
                }
            }
        }
    }

    /*#################MENU BAR#################*/
    override fun clickMenu(item: MenuItem) {
        val id = item.itemId
        Log.d("MenuBar", "fragment")
        if (id == R.id.addBtn) {
            addCondition()
        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(
                context,
                getString(R.string.Help),
                getString(R.string.aide_conditon_alarm)
            )
        }
    }
}