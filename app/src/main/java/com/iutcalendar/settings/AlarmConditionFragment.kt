package com.iutcalendar.settings

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.AlarmRing
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.alarm.condition.AlarmConditionRecycleView
import com.iutcalendar.alarm.condition.AlarmCondtion
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
        val view = inflater.inflate(R.layout.fragment_alarm_condition, container, false)
        initVariable(view)
        updateConditions()
        return view
    }

    private fun initVariable(view: View) {
        recyclerViewConstraint = view.findViewById(R.id.recycleView)
    }

    private fun updateConditions() {
        val adapter = AlarmConditionRecycleView(context,
            AlarmConditionManager.getInstance(context).allConditions, { saveConditions() }) { updateConditions() }
        recyclerViewConstraint!!.adapter = adapter
        recyclerViewConstraint!!.layoutManager = LinearLayoutManager(activity)

        //save si des changements de constraint ont été fait
        saveConditions()
        Log.d("Constraint", "updateConstraint")
    }

    private fun saveConditions() {
        AlarmConditionManager.getInstance(context).save(context)
    }

    private fun addCondition() {

        //demande heure début
        AlarmRing.askTime(context, getString(R.string.BorneInf)) { view: TimePicker?, hourOfDayBegin: Int, minuteBegin: Int ->
            val begin: Long = DateCalendrier.getHourInMillis(hourOfDayBegin, minuteBegin)

            //demande heure fin
            AlarmRing.askTime(context, getString(R.string.BorneSup)) { view1: TimePicker?, hourOfDayEnd: Int, minuteEnd: Int ->
                val end: Long = DateCalendrier.getHourInMillis(hourOfDayEnd, minuteEnd)
                if (begin > end) {
                    DialogMessage.showWarning(context, getString(R.string.Interval), getString(R.string.born_sup_et_inf_inverser))
                } else {
                    //demande heure sonnerie
                    AlarmRing.askTime(context, getString(R.string.TimeToRing)) { view2: TimePicker?, hourOfDayAlarmAt: Int, minuteAlarmAt: Int ->
                        val alarmAt: Long = DateCalendrier.getHourInMillis(hourOfDayAlarmAt, minuteAlarmAt)
                        AlarmConditionManager.getInstance(context).addCondition(AlarmCondtion(begin, end, alarmAt))
                        updateConditions()
                    }
                }
            }
        }
    }

    /*#################MENU BAR#################*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_help_and_add_alarm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.d("MenuBar", "fragment")
        if (id == R.id.addBtn) {
            addCondition()
        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(context, getString(R.string.Help), getString(R.string.aide_conditon_alarm))
        }
        return super.onOptionsItemSelected(item)
    }
}