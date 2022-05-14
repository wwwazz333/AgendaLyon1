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
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.task.TaskRecycleView;

public class DialogPopupEvent extends Dialog {
    private TextView title, summary, salle, debut, fin;
    private Button okBtn;

    private ImageButton addBtn;

    private RecyclerView recyclerViewTask;

    public DialogPopupEvent(@NonNull Context context, EventCalendrier eventClicked, Activity activity) {
        super(context);
        setContentView(R.layout.dialog_event_edit);
        initVariable();

        this.addBtn.setOnClickListener(view -> showAddTask());

        this.okBtn.setOnClickListener(view -> dismiss());


        String timeDebut = eventClicked.getDate().timeToString();
        String timeFin = eventClicked.getDate().addTime(eventClicked.getDuree()).timeToString();

        this.title.setText(eventClicked.getSummary());
        this.summary.setText(eventClicked.getDescription());
        this.salle.setText(eventClicked.getSalle());
        this.debut.setText(timeDebut);
        this.fin.setText(timeFin);

        ClickListiner listiner = index -> {
        };
        TaskRecycleView adapter = new TaskRecycleView(PersonnalCalendrier.getInstance().getLinkedTask(eventClicked), activity.getApplication(), listiner);
        recyclerViewTask.setAdapter(adapter);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void initVariable() {
        title = findViewById(R.id.title);
        summary = findViewById(R.id.description);
        salle = findViewById(R.id.salle);
        debut = findViewById(R.id.debut);
        fin = findViewById(R.id.fin);


        okBtn = findViewById(R.id.okBtn);
        addBtn = findViewById(R.id.addBtn);

        recyclerViewTask = findViewById(R.id.listTask);
    }

    private void showAddTask() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("ajout tache");
        alertDialog.setMessage("Nom");

        final EditText inputName = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputName.setLayoutParams(lp);
        alertDialog.setView(inputName);

//        alertDialog.setView(new TextView(getContext(), "Déscription"));

        final EditText inputDescription = new EditText(getContext());
        lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputDescription.setLayoutParams(lp);
        alertDialog.setView(inputDescription);


        alertDialog.setPositiveButton("Valide",
                (dialog, which) -> dialog.dismiss());

        alertDialog.setNegativeButton("Annulé",
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }


}
