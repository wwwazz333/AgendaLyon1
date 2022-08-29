package com.iutcalendar.task

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.tools.vibrator.VibratorSimpleUse
import com.univlyon1.tools.agenda.R

class TaskAdapter(
    var context: Context,
    private var relatedEvent: EventCalendrier
) : RecyclerView.Adapter<TaskViewHolder>() {

    private val personalCalendrier
        get() = PersonalCalendrier.getInstance(context)
    private val list
        get() = personalCalendrier.getLinkedTask(relatedEvent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val eventView = inflater.inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(eventView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]
        holder.apply {
            text.text = task.txt
            view.setOnLongClickListener {
                VibratorSimpleUse.pushVibration(context)
                removeTask(task, position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun removeTask(taskClicked: Task, position: Int) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Suppression")
        alertDialog.setMessage(
            """
    Voulez-vous supprimer cette tâche ?
    "${taskClicked.txt}"
    """.trimIndent()
        )
        alertDialog.setPositiveButton(context.getString(R.string.yes)) { dialogInterface: DialogInterface, _: Int ->
            personalCalendrier.apply {
                remove(context, taskClicked)
                save(context)
            }
            Toast.makeText(context, "Tâche supprimer", Toast.LENGTH_SHORT).show()

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)

            dialogInterface.dismiss()
        }

        alertDialog.setNegativeButton(context.getString(R.string.no)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
        alertDialog.show()
    }
}