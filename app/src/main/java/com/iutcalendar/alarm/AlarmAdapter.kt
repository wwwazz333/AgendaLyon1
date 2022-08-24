package com.iutcalendar.alarm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.data.DataGlobal
import com.univlyon1.tools.agenda.R

class AlarmAdapter(var list: List<AlarmRing>, var updateClick: () -> Unit) : RecyclerView.Adapter<AlarmViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.alarm_card, parent, false)
        return AlarmViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmRing = list[position]
        val dateRing = alarmRing.dateTime
        if (DataGlobal.isDebug(holder.view.context)) {
            holder.view.setOnClickListener { view: View ->
                Log.d("Alarm", list.size.toString() + " -> " + position + " " + dateRing.toString() + " at " + dateRing.timeToString())
                Toast.makeText(view.context, position.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        holder.horaire.text = dateRing.timeToString()
        val dateAffichage = dateRing.getRelativeDayName(holder.view.context)
        if (Character.isDigit(dateAffichage[dateAffichage.length - 1])) {
            holder.date.text = dateAffichage.substring(0, dateAffichage.length - 5) //enlever l'année
        } else {
            holder.date.text = dateAffichage
        }
        holder.isActivateSwitch.isChecked = alarmRing.isActivate
        holder.isActivateSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            alarmRing.isActivate = isChecked
            updateClick()
        }
        holder.trash.setOnClickListener { view: View ->
            PersonalAlarmManager.getInstance(view.context).remove(view.context, alarmRing)
            updateClick()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}