package com.iutcalendar.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class TaskRecycleView(var list: List<Task>, var listener: (taskClicked: Task?) -> Unit) : RecyclerView.Adapter<TaskViewHolder>() {
    var viewHolder: TaskViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.task_card, parent, false)
        viewHolder = TaskViewHolder(eventView)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]
        viewHolder!!.text.text = task.txt
        if (task.isAlarm) {
            viewHolder!!.alarmIcon.visibility = View.VISIBLE
            if (task.isAlarmActivate) {
                viewHolder!!.alarmIcon.setImageDrawable(viewHolder!!.view.context.getDrawable(R.drawable.ic_alarm))
            } else {
                viewHolder!!.alarmIcon.setImageDrawable(viewHolder!!.view.context.getDrawable(R.drawable.ic_alarm_off))
            }
        }
        viewHolder!!.view.setOnClickListener { listener(task) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}