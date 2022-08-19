package com.iutcalendar.settings

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel.Containing
import com.iutcalendar.alarm.condition.label_constraint.AlarmLabelConstraintRecycleView
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.menu.AbstractFragmentWitheMenu
import com.univlyon1.tools.agenda.R

class AlarmConstraintFragment : AbstractFragmentWitheMenu() {
    private var recyclerViewConstraint: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().invalidateOptionsMenu()
        val view = inflater.inflate(R.layout.fragment_alarm_constraint, container, false)
        initVariable(view)
        updateConstraint()
        return view
    }

    private fun initVariable(view: View) {
        recyclerViewConstraint = view.findViewById(R.id.recycleView)
    }

    private fun updateConstraint() {
        val adapter = AlarmLabelConstraintRecycleView(context,
            AlarmConditionManager.getInstance(context).allConstraints, { saveConstraint() }) { updateConstraint() }
        recyclerViewConstraint!!.adapter = adapter
        recyclerViewConstraint!!.layoutManager = LinearLayoutManager(activity)

        //save si des changements de constraint ont été fait
        saveConstraint()
        Log.d("Constraint", "updateConstraint")
    }

    private fun saveConstraint() {
        AlarmConditionManager.getInstance(context).save(context)
    }

    private fun addConstraint() {
        AlarmConditionManager.getInstance(context).addConstraint(AlarmConstraintLabel(Containing.MUST_NOT_CONTAIN, ""))
        updateConstraint()
    }

    /*#################MENU BAR#################*/
    override fun clickMenu(item: MenuItem) {
        val id = item.itemId
        Log.d("MenuBar", "fragment")
        if (id == R.id.addBtn) {
            addConstraint()
        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(context, getString(R.string.Help), getString(R.string.aid_constraint_alarm))
        }
    }
}