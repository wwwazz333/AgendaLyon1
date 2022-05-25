package com.iutcalendar.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
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
    private final UpdateListener whenFinish;

    public DialogPopupEvent(@NonNull Context context, EventCalendrier eventClicked, Activity activity, UpdateListener whenFinish) {
        super(context);
        Log.d("Dialog", "start");
        setContentView(R.layout.dialog_event_edit);
        initVariable();
        this.relatedEvent = eventClicked;
        this.activity = activity;
        this.whenFinish = whenFinish;

        this.addBtn.setOnClickListener(view -> showAddTask());

        this.okBtn.setOnClickListener(view -> dismiss());


        String timeDebut = relatedEvent.getDate().timeToString();
        String timeFin = relatedEvent.getDate().addTime(relatedEvent.getDuree()).timeToString();

        this.title.setText(relatedEvent.getSummary());
        this.summary.setText(relatedEvent.getDescription());
        this.salle.setText(relatedEvent.getSalle().replace("\\,", ", "));

        this.horaire.setText(context.getString(R.string.both_time, timeDebut, timeFin));
        this.duree.setText(relatedEvent.getDuree().timeToString());

        updatedTask();
        Log.d("Dialog", "end");
    }

    private void updatedTask() {
        ClickListener listener = this::removeTask;
        TaskRecycleView adapter = new TaskRecycleView(PersonnalCalendrier.getInstance(getContext()).getLinkedTask(relatedEvent), listener);
        recyclerViewTask.setAdapter(adapter);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(activity));
        Log.d("Dialog", "updateTask");
    }

    @Override
    public void dismiss() {
        whenFinish.update();
        super.dismiss();
    }

    private void initVariable() {
        title = findViewById(R.id.title);
        summary = findViewById(R.id.textSummary);
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
                    PersonnalCalendrier.getInstance(getContext()).addLinkedTask(new Task(editText.getText().toString(), relatedEvent.getUID()), relatedEvent);
                    PersonnalCalendrier.getInstance(getContext()).save(getContext());
                    dialog.dismiss();
                    updatedTask();
                });

        alertDialog.setNegativeButton(getContext().getString(R.string.cancel),
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    private void removeTask(Task taskClicked) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        if (!taskClicked.isAlarm()) {
            alertDialog.setTitle("Suppression");
            alertDialog.setMessage("Voulez-vous supprimer cette tâche ?\n\"" + taskClicked.getTxt() + "\"");

            alertDialog.setPositiveButton(getContext().getString(R.string.yes), (dialogInterface, i) -> {

                PersonnalCalendrier.getInstance(getContext()).remove(getContext(), taskClicked);
                PersonnalCalendrier.getInstance(getContext()).save(getContext());
                Toast.makeText(getContext(), "Tâche supprimer", Toast.LENGTH_SHORT).show();
                updatedTask();
                dialogInterface.dismiss();
            });
        } else {
            alertDialog.setTitle("Désactivation");
            alertDialog.setMessage("Voulez-vous " + ((taskClicked.isAlarmActivate()) ? "désactivé" : "activé") +
                    " cette tâche ?\n\"" + taskClicked.getTxt() + "\"");

            alertDialog.setPositiveButton(getContext().getString(R.string.yes), (dialogInterface, i) -> {

                taskClicked.setAlarmActivate(!taskClicked.isAlarmActivate());
                PersonnalCalendrier.getInstance(getContext()).save(getContext());
                updatedTask();
                dialogInterface.dismiss();

            });
        }


        alertDialog.setNegativeButton(getContext().getString(R.string.no), (dialogInterface, i) -> dialogInterface.cancel());

        alertDialog.show();
    }


}
