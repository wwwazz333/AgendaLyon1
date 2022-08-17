package com.iutcalendar.alarm.condition.label_constraint

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class AlarmLabelConstraintViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
    var typeConstraint: Button
    var constraint: EditText
    var delBtn: ImageButton

    init {
        constraint = itemView.findViewById(R.id.constraint)
        typeConstraint = itemView.findViewById(R.id.type_constraint)
        delBtn = itemView.findViewById(R.id.del_btn)
    }
}