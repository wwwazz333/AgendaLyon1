package com.iutcalendar.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.task.ClickListener;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.task.Task;
import com.iutcalendar.task.TaskRecycleView;

public class DialogPopupEvent extends Dialog {
    private final EventCalendrier relatedEvent;
    private final Activity activity;
    private TextView title, summary, salle, horaire, duree;
    private Button okBtn;
    private ImageButton addBtn;
    private RecyclerView recyclerViewTask;

    public DialogPopupEvent(@NonNull Context context, EventCalendrier eventClicked, Activity activity) {
        super(context);
        setContentView(R.layout.dialog_event_edit);
        initVariable();
        this.relatedEvent = eventClicked;
        this.activity = activity;

        this.addBtn.setOnClickListener(view -> showAddTask());

        this.okBtn.setOnClickListener(view -> dismiss());


        String timeDebut = relatedEvent.getDate().timeToString();
        String timeFin = relatedEvent.getDate().addTime(relatedEvent.getDuree()).timeToString();

        this.title.setText(relatedEvent.getSummary());
        this.summary.setText(relatedEvent.getDescription());
        this.salle.setText(relatedEvent.getSalle());

        this.horaire.setText(context.getString(R.string.both_time, timeDebut, timeFin));
        this.duree.setText(relatedEvent.getDuree().timeToString());

        updatedTask();

    }

    private void updatedTask() {
        ClickListener listener = this::removeTask;
        TaskRecycleView adapter = new TaskRecycleView(PersonnalCalendrier.getInstance().getLinkedTask(relatedEvent), activity.getApplication(), listener);
        recyclerViewTask.setAdapter(adapter);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(activity));
    }


    private void initVariable() {
        title = findViewById(R.id.title);
        summary = findViewById(R.id.textTask);
        salle = findViewById(R.id.salle);
        horaire = findViewById(R.id.horaire);
        duree = findViewById(R.id.duree);


        okBtn = findViewById(R.id.okBtn);
        addBtn = findViewById(R.id.addBtn);

        recyclerViewTask = findViewById(R.id.listTask);
    }

    private void showAddTask() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Ajouté un tâche");
        alertDialog.setMessage(getContext().getString(R.string.task));

        final EditText editText = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton(getContext().getString(R.string.submit),
                (dialog, which) -> {
                    PersonnalCalendrier.getInstance().addLinkedTask(new Task(editText.getText().toString(), relatedEvent.getUID()), relatedEvent);
                    PersonnalCalendrier.getInstance().save(getContext());
                    dialog.dismiss();
                    updatedTask();
                });

        alertDialog.setNegativeButton(getContext().getString(R.string.cancel),
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    private void removeTask(Task taskClicked) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Suppression");
        alertDialog.setMessage("Voulez-vous supprimer cette tâche ?\n\"" + taskClicked.getTxt() + "\"");

        alertDialog.setPositiveButton(getContext().getString(R.string.yes), (dialogInterface, i) -> {
            updatedTask();
            PersonnalCalendrier.getInstance().remove(taskClicked);
            PersonnalCalendrier.getInstance().save(getContext());
            Toast.makeText(getContext(), "Tâche supprimer", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

        alertDialog.setNegativeButton(getContext().getString(R.string.no), (dialogInterface, i) -> dialogInterface.cancel());

        alertDialog.show();
    }


}
