package com.iutcalendar.event

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iutcalendar.calendrier.EventCalendrier
import com.iutcalendar.data.CachedData
import com.iutcalendar.data.ColorEvent
import com.iutcalendar.dialog.ColorPickerDialog
import com.iutcalendar.task.PersonalCalendrier
import com.iutcalendar.task.Task
import com.iutcalendar.task.TaskAdapter
import com.univlyon1.tools.agenda.R

class DialogPopupEvent(
    context: Context,
    eventClicked: EventCalendrier,
    activity: Activity?,
    private val whenFinish: (shouldRestart: Boolean) -> Unit
) : Dialog(context) {
    private val relatedEvent: EventCalendrier
    private val activity: Activity?
    private lateinit var title: TextView
    private lateinit var summary: TextView
    private lateinit var salle: TextView
    private lateinit var horaire: TextView
    private lateinit var duree: TextView
    private lateinit var tpsRestant: TextView
    private lateinit var okBtn: Button
    private lateinit var addBtn: ImageButton
    private lateinit var recyclerViewTask: RecyclerView
    private lateinit var colorEdit: View
    private var shouldRestart: Boolean = false


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

        countMinutesModule().let {
            tpsRestant.text = "${it / 60}h ${it % 60}min"
        }


        updateColorEdit()
        colorEdit.setOnClickListener {
            (colorEdit.background as ColorDrawable).color.apply {
                ColorPickerDialog(context, red, green, blue) { colorId ->
                    ColorEvent.save(context, relatedEvent.nameEvent, colorId)
                    updateColorEdit()
                    shouldRestart = true
                }.show()
            }
        }



        initRecyclerViewTask()
        Log.d("Dialog", "end")
    }

    private fun initVariable() {
        title = findViewById(R.id.title)
        summary = findViewById(R.id.textSummary)
        salle = findViewById(R.id.salle)
        horaire = findViewById(R.id.horaire)
        duree = findViewById(R.id.duree)
        tpsRestant = findViewById(R.id.tpsRestant)
        okBtn = findViewById(R.id.okBtn)
        addBtn = findViewById(R.id.addBtn)
        recyclerViewTask = findViewById(R.id.listTask)
        colorEdit = findViewById(R.id.colorEdit)
    }

    private fun countMinutesModule(): Int {
        var count = 0
        var startCount = false
        CachedData.calendrier.events.forEach { e ->
            if (e.uid == relatedEvent.uid) startCount = true
            if (startCount && e.nameEvent == relatedEvent.nameEvent) count += e.dure.hour * 60 + e.dure.minute
        }
        return count
    }

    private fun initRecyclerViewTask() {
        val taskAdapter = TaskAdapter(context, relatedEvent)
        recyclerViewTask.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        Log.d("Dialog", "updateTask")
    }

    private fun updateColorEdit() {
        colorEdit.setBackgroundColor(ColorEvent.getOrCreate(context, relatedEvent.nameEvent))//should get and not create
    }

    override fun dismiss() {
        whenFinish(shouldRestart)
        super.dismiss()
    }

    private fun showAddTask() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Ajouté un tâche")
        alertDialog.setMessage(context.getString(R.string.task))
        val editText = EditText(context)
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.setView(editText)
        alertDialog.setPositiveButton(
            context.getString(R.string.submit)
        ) { dialog: DialogInterface, _: Int ->
            PersonalCalendrier.getInstance(context).apply {
                addLinkedTask(Task(editText.text.toString(), relatedEvent.uid), relatedEvent)
                save(context)
            }
            dialog.dismiss()
            recyclerViewTask.adapter?.apply {
                notifyItemInserted(itemCount - 1)
            }
        }
        alertDialog.setNegativeButton(context.getString(R.string.cancel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        alertDialog.show()
    }

    private fun changeColor() {


    }

}