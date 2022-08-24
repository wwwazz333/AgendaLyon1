package com.iutcalendar.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.tools.vibrator.VibratorSimpleUse
import com.univlyon1.tools.agenda.R

class TaskAdapter(
    var context: Context,
    var list: List<Task>,
    var removeTaskListener: (taskClicked: Task?) -> Unit
) :
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
            view.setOnLongClickListener {
                VibratorSimpleUse.pushVibration(context)
                removeTaskListener(task)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}