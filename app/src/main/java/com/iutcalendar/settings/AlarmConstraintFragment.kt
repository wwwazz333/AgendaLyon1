package com.iutcalendar.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel.Containing
import com.iutcalendar.alarm.condition.label_constraint.AlarmLabelConstraintAdapter
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
        val adapter = AlarmConditionManager.getInstance(requireContext()).allConstraints?.let {
            AlarmLabelConstraintAdapter(
                context,
                it
            ) { saveConstraint() }
        }
        recyclerViewConstraint!!.adapter = adapter
        recyclerViewConstraint!!.layoutManager = LinearLayoutManager(activity)

        //save si des changements de constraint ont été fait
        saveConstraint()
        Log.d("Constraint", "updateConstraint")
    }

    private fun saveConstraint() {
        AlarmConditionManager.getInstance(requireContext()).save(requireContext())
    }

    private fun addConstraint() {
        AlarmConditionManager.getInstance(requireContext())
            .addConstraint(AlarmConstraintLabel(Containing.MUST_NOT_CONTAIN, ""))
        recyclerViewConstraint?.adapter?.let {
            it.notifyItemInserted(it.itemCount - 1)
        }
        saveConstraint()
    }

    /*#################MENU BAR#################*/
    override fun clickMenu(item: MenuItem) {
        val id = item.itemId
        Log.d("MenuBar", "fragment")
        if (id == R.id.addBtn) {
            addConstraint()
        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(
                context,
                getString(R.string.Help),
                getString(R.string.aid_constraint_alarm)
            )
        }
    }
}