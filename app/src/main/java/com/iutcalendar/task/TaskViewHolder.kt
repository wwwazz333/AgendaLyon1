package com.iutcalendar.task

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var text: TextView
    var view: View

    init {
        text = itemView.findViewById(R.id.textSummary)
        view = itemView
    }
}