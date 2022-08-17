package com.iutcalendar.event.changement

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.event.changement.EventChangement.Change
import com.univlyon1.tools.agenda.R

class EventChangementRecycleView(private val context: Context?, private var changementsList: List<EventChangement?>) : RecyclerView.Adapter<EventChangementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventChangementViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.changement_card, parent, false)
        return EventChangementViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: EventChangementViewHolder, position: Int) {
        val eventChangement = changementsList[position]
        holder.name.text = eventChangement?.eventChanged?.nameEvent
        holder.action.text = eventChangement?.infoChange?.changement?.toString(context)
        holder.actionDate.text = eventChangement?.dateOfTheChangement.toString()
        when (eventChangement!!.infoChange.changement) {
            Change.ADD -> {
                holder.globalLayout.removeView(holder.fromLayout)
                holder.toDate.text = eventChangement.infoChange.newDate.toString()
            }

            Change.DELETE -> {
                holder.globalLayout.removeView(holder.toLayout)
                holder.fromDate.text = eventChangement.infoChange.prevDate.toString()
            }

            Change.MOVE -> {
                holder.fromDate.text = eventChangement.infoChange.prevDate.toString()
                holder.toDate.text = eventChangement.infoChange.newDate.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return changementsList.size
    }
}