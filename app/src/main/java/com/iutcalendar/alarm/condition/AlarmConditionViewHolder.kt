package com.iutcalendar.alarm.condition

import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class AlarmConditionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var view: View
    var beginHour: TextView
    var endHour: TextView
    var ringHour: TextView
    var delBtn: ImageButton
    var monday: CheckedTextView
    var tuesday: CheckedTextView
    var wednesday: CheckedTextView
    var thursday: CheckedTextView
    var friday: CheckedTextView
    var saturday: CheckedTextView
    var sunday: CheckedTextView

    init {
        beginHour = itemView.findViewById(R.id.beginHour)
        endHour = itemView.findViewById(R.id.endHour)
        ringHour = itemView.findViewById(R.id.ringHour)
        //        listConstraint = itemView.findViewById(R.id.listConstraint);
        delBtn = itemView.findViewById(R.id.del_btn)
        monday = itemView.findViewById(R.id.monday)
        tuesday = itemView.findViewById(R.id.tuesday)
        wednesday = itemView.findViewById(R.id.wednesday)
        thursday = itemView.findViewById(R.id.thursday)
        friday = itemView.findViewById(R.id.friday)
        saturday = itemView.findViewById(R.id.saturday)
        sunday = itemView.findViewById(R.id.sunday)


//        addConstraintBtn = itemView.findViewById(R.id.add_constraint_label);
        view = itemView
    }
}