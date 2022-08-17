package com.iutcalendar.event

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var debut: TextView
    var fin: TextView
    var summary: TextView
    var salle: TextView
    var countTask: TextView
    var view: View

    init {
        debut = itemView.findViewById(R.id.debut)
        fin = itemView.findViewById(R.id.fin)
        summary = itemView.findViewById(R.id.summary)
        salle = itemView.findViewById(R.id.salle)
        countTask = itemView.findViewById(R.id.countTask)
        view = itemView
    }
}