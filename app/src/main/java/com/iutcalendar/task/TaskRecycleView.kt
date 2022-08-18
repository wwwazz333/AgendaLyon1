package com.iutcalendar.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R

class TaskRecycleView(var list: List<Task>, var listener: (taskClicked: Task?) -> Unit) :
    RecyclerView.Adapter<TaskViewHolder>() {
    private var viewHolder: TaskViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.task_card, parent, false)
        viewHolder = TaskViewHolder(eventView)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]
        viewHolder?.apply {
            text.text = task.txt
            view.setOnClickListener { listener(task) }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}