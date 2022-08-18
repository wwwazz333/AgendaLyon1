package com.iutcalendar.event

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.task.PersonalCalendrier
import com.iutcalendar.task.Task
import com.iutcalendar.task.TaskRecycleView
import com.univlyon1.tools.agenda.R

class DialogPopupEvent(
    context: Context,
    eventClicked: EventCalendrier,
    activity: Activity?,
    private val whenFinish: () -> Unit
) : Dialog(context) {
    private val relatedEvent: EventCalendrier
    private val activity: Activity?
    private lateinit var title: TextView
    private lateinit var summary: TextView
    private lateinit var salle: TextView
    private lateinit var horaire: TextView
    private lateinit var duree: TextView
    private lateinit var okBtn: Button
    private lateinit var addBtn: ImageButton
    private lateinit var recyclerViewTask: RecyclerView


    init {
        Log.d("Dialog", "start")
        setContentView(R.layout.dialog_event_edit)
        initVariable()
        relatedEvent = eventClicked
        this.activity = activity
        addBtn.setOnClickListener { showAddTask() }
        okBtn.setOnClickListener { dismiss() }
        val timeDebut = relatedEvent.date!!.timeToString()
        val timeFin = relatedEvent.date!!.addTime(relatedEvent.dure).timeToString()
        title.text = relatedEvent.nameEvent
        summary.text = relatedEvent.description
        salle.text = relatedEvent.salle.replace("\\,", ", ")
        horaire.text = context.getString(R.string.both_time, timeDebut, timeFin)
        duree.text = relatedEvent.dure.timeToString()
        updatedTask()
        Log.d("Dialog", "end")
    }

    private fun initVariable() {
        title = findViewById(R.id.title)
        summary = findViewById(R.id.textSummary)
        salle = findViewById(R.id.salle)
        horaire = findViewById(R.id.horaire)
        duree = findViewById(R.id.duree)
        okBtn = findViewById(R.id.okBtn)
        addBtn = findViewById(R.id.addBtn)
        recyclerViewTask = findViewById(R.id.listTask)
    }

    private fun updatedTask() {
        val adapter = TaskRecycleView(
            PersonalCalendrier.getInstance(context)!!.getLinkedTask(relatedEvent)
        ) { taskClicked: Task? ->
            if (taskClicked != null) removeTask(taskClicked)
        }

        recyclerViewTask.adapter = adapter
        recyclerViewTask.layoutManager = LinearLayoutManager(activity)
        Log.d("Dialog", "updateTask")
    }

    override fun dismiss() {
        whenFinish()
        super.dismiss()
    }

    private fun showAddTask() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Ajouté un tâche")
        alertDialog.setMessage(context.getString(R.string.task))
        val editText = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = lp
        alertDialog.setView(editText)
        alertDialog.setPositiveButton(
            context.getString(R.string.submit)
        ) { dialog: DialogInterface, _: Int ->
            PersonalCalendrier.getInstance(context)!!
                .addLinkedTask(Task(editText.text.toString(), relatedEvent.uid), relatedEvent)
            PersonalCalendrier.getInstance(context)!!.save(context)
            dialog.dismiss()
            updatedTask()
        }
        alertDialog.setNegativeButton(
            context.getString(R.string.cancel)
        ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        alertDialog.show()
    }

    private fun removeTask(taskClicked: Task) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Suppression")
        alertDialog.setMessage(
            """
    Voulez-vous supprimer cette tâche ?
    "${taskClicked.txt}"
    """.trimIndent()
        )
        alertDialog.setPositiveButton(context.getString(R.string.yes)) { dialogInterface: DialogInterface, _: Int ->
            PersonalCalendrier.getInstance(context)!!.remove(context, taskClicked)
            PersonalCalendrier.getInstance(context)!!.save(context)
            Toast.makeText(context, "Tâche supprimer", Toast.LENGTH_SHORT).show()
            updatedTask()
            dialogInterface.dismiss()
        }

        alertDialog.setNegativeButton(context.getString(R.string.no)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
        alertDialog.show()
    }
}