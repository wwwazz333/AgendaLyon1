package com.iutcalendar.alarm

import android.view.View
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var horaire: TextView
    var date: TextView
    var isActivateSwitch: Switch
    var trash: ImageButton
    var view: View

    init {
        horaire = itemView.findViewById(R.id.horaire)
        date = itemView.findViewById(R.id.date)
        isActivateSwitch = itemView.findViewById(R.id.is_activate)
        trash = itemView.findViewById(R.id.remove_btn)
        view = itemView
    }
}