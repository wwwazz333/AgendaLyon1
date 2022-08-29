package com.iutcalendar.event.changement

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class EventChangementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name: TextView
    var action: TextView
    var actionDate: TextView
    private var fromLabel: TextView
    var fromDate: TextView
    private var toLabel: TextView
    var toDate: TextView
    var globalLayout: LinearLayout
    var fromLayout: LinearLayout
    var toLayout: LinearLayout

    init {
        name = itemView.findViewById(R.id.nameEvent)
        action = itemView.findViewById(R.id.actionLabel)
        actionDate = itemView.findViewById(R.id.actionDate)
        fromLabel = itemView.findViewById(R.id.fromLabel)
        fromDate = itemView.findViewById(R.id.fromDate)
        toLabel = itemView.findViewById(R.id.toLabel)
        toDate = itemView.findViewById(R.id.toDate)
        globalLayout = itemView.findViewById(R.id.globalLayout)
        fromLayout = itemView.findViewById(R.id.fromLayout)
        toLayout = itemView.findViewById(R.id.toLayout)
    }
}