package com.iutcalendar.task

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var text: TextView
    var alarmIcon: ImageView
    var view: View

    init {
        text = itemView.findViewById(R.id.textSummary)
        alarmIcon = itemView.findViewById(R.id.iconAlarme)
        view = itemView
    }
}