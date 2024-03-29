package com.iutcalendar.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.ColorEvent
import com.iutcalendar.swiping.GestureEventManager
import com.iutcalendar.swiping.TouchGestureListener
import com.iutcalendar.task.PersonalCalendrier
import com.univlyon1.tools.agenda.R

class EventAdapter(
    var list: List<EventCalendrier>,
    var context: Context,
    var clickListener: (Int) -> Unit
) : RecyclerView.Adapter<EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val eventView = inflater.inflate(R.layout.event_card, parent, false)
        return EventViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.apply {
            val eventCalendrier = list[position]

            view.setCardBackgroundColor(ColorEvent.getOrCreate(context, eventCalendrier.nameEvent))
            view.setOnClickListener { clickListener(absoluteAdapterPosition) }

            debut.text = eventCalendrier.date?.timeToString()
            fin.text = eventCalendrier.date?.addTime(eventCalendrier.dure)?.timeToString()
            summary.text = eventCalendrier.nameEvent
            salle.text = eventCalendrier.salle.replace("\\,", "\n")

            PersonalCalendrier.getInstance(context).getCountTaskOf(eventCalendrier).let {
                if (it == 0) {
                    countTask.visibility = View.INVISIBLE
                } else {
                    countTask.visibility = View.VISIBLE
                    countTask.text = it.toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}