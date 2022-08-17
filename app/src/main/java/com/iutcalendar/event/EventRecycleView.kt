package com.iutcalendar.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.swiping.GestureEventManager
import com.iutcalendar.swiping.TouchGestureListener
import com.iutcalendar.task.PersonalCalendrier
import com.univlyon1.tools.agenda.R

class EventRecycleView(var list: List<EventCalendrier?>?, var context: Context, var clickListener: (Int) -> Unit) : RecyclerView.Adapter<EventViewHolder>() {
    private var viewHolder: EventViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.event_card, parent, false)
        viewHolder = EventViewHolder(eventView)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val index = viewHolder!!.absoluteAdapterPosition
        val e = list!![position]!!
        viewHolder!!.debut.text = e.date?.timeToString()
        viewHolder!!.fin.text = e.date!!.addTime(e.dure).timeToString()
        viewHolder!!.summary.text = e.nameEvent
        viewHolder!!.salle.text = e.salle.replace("\\,", "\n")
        val numberTask: Int = PersonalCalendrier.getInstance(context)!!.getCountTaskOf(e)
        if (numberTask == 0) {
            viewHolder!!.countTask.visibility = View.INVISIBLE
        } else {
            viewHolder!!.countTask.text = numberTask.toString()
        }
        viewHolder!!.view.setOnTouchListener(TouchGestureListener(context, object : GestureEventManager() {
            override fun onClick() {
                clickListener(index)
                super.onClick()
            }
        }))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}