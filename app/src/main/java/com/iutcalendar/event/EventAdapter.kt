package com.iutcalendar.event

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.swiping.GestureEventManager
import com.iutcalendar.swiping.TouchGestureListener
import com.iutcalendar.task.PersonalCalendrier
import com.univlyon1.tools.agenda.R

class EventAdapter(
    var list: List<EventCalendrier>,
    var context: Context,
    var clickListener: (Int) -> Unit
) : RecyclerView.Adapter<EventViewHolder>() {
    private var viewHolder: EventViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.event_card, parent, false)
        viewHolder = EventViewHolder(eventView)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        viewHolder?.apply {
            val eventCalendrier = list[position]
            debut.text = eventCalendrier.date?.timeToString()
            fin.text = eventCalendrier.date!!.addTime(eventCalendrier.dure).timeToString()
            summary.text = eventCalendrier.nameEvent
            salle.text = eventCalendrier.salle.replace("\\,", "\n")

            PersonalCalendrier.getInstance(context)?.getCountTaskOf(eventCalendrier)?.let {
                if (it == 0) {
                    countTask.visibility = View.INVISIBLE
                } else {
                    countTask.text = it.toString()
                }
            }

            view.setOnTouchListener(TouchGestureListener(context, object : GestureEventManager() {
                override fun onClick() {
                    clickListener(absoluteAdapterPosition)
                    super.onClick()
                }
            }))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}