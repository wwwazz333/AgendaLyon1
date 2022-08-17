package com.iutcalendar.alarm.condition.label_constraint

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.alarm.condition.AlarmConditionManager
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel.Containing
import com.univlyon1.tools.agenda.R

class AlarmLabelConstraintRecycleView(var context: Context?, var list: List<AlarmConstraintLabel>?, var updateListener: () -> Unit, var reloadListener: () -> Unit) : RecyclerView.Adapter<AlarmLabelConstraintViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmLabelConstraintViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.constraint_card, parent, false)
        return AlarmLabelConstraintViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: AlarmLabelConstraintViewHolder, position: Int) {
        val constraintLabelAlarm = list!![position]
        holder.typeConstraint.text = constraintLabelAlarm.typeDeContraint.toString(context)
        holder.typeConstraint.setOnClickListener { showEditConstraintType(holder, constraintLabelAlarm) }
        holder.constraint.setText(constraintLabelAlarm.contraintText)
        holder.constraint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                constraintLabelAlarm.setConstraintString(holder.constraint.text.toString())
                updateListener()
            }
        })
        holder.constraint.isSelected = false
        holder.delBtn.setOnClickListener {
            AlarmConditionManager.getInstance(context).removeConstraint(constraintLabelAlarm)
            reloadListener()
        }
    }

    private fun showEditConstraintType(holder: AlarmLabelConstraintViewHolder, constraintLabelAlarm: AlarmConstraintLabel) {
        val type: Containing = if (holder.typeConstraint.text.toString() == Containing.MUST_NOT_CONTAIN.toString(context)) {
            Containing.MUST_NOT_BE_EXACTLY
        } else {
            Containing.MUST_NOT_CONTAIN
        }
        constraintLabelAlarm.typeDeContraint = type
        holder.typeConstraint.text = type.toString(context)
        updateListener()
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}