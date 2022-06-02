package com.iutcalendar.alarm.constraint.label_constraint;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.ClickForUpdateListener;
import com.iutcalendar.task.PersonnalCalendrier;
import com.iutcalendar.task.Task;

import java.util.List;

public class AlarmLabelConstraintRecycleView extends RecyclerView.Adapter<AlarmLabelConstraintViewHolder> {

    List<ConstraintLabelAlarm> list;
    AlarmLabelConstraintViewHolder viewHolder;
    Context context;
    ClickForUpdateListener updateListener;

    public AlarmLabelConstraintRecycleView(Context context, List<ConstraintLabelAlarm> list, ClickForUpdateListener updateListener) {
        this.list = list;
        this.context = context;
        this.updateListener = updateListener;
    }

    @NonNull
    @Override
    public AlarmLabelConstraintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.constraint_card, parent, false);

        viewHolder = new AlarmLabelConstraintViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmLabelConstraintViewHolder holder, int position) {
        final int index = viewHolder.getAbsoluteAdapterPosition();
        ConstraintLabelAlarm constraintLabelAlarm = list.get(position);

        viewHolder.typeConstraint.setText(constraintLabelAlarm.getTypeDeContraint().toString(context));
        viewHolder.typeConstraint.setOnClickListener(view -> {
            showEditConstraintType(constraintLabelAlarm);
        });

        viewHolder.constraint.setText(constraintLabelAlarm.getContraintRegex());
        viewHolder.constraint.setOnClickListener(view -> {
            showEditConstraint(constraintLabelAlarm);
        });



        viewHolder.delBtn.setOnClickListener(view -> {
            constraintLabelAlarm.remove();
            updateListener.update();
        });

    }

    private void showEditConstraint(ConstraintLabelAlarm constraintLabelAlarm) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edité contrainte");
        alertDialog.setMessage("Contrainte");

        final EditText editText = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setText(viewHolder.constraint.getText());
        alertDialog.setView(editText);

        alertDialog.setPositiveButton(context.getString(R.string.submit),
                (dialog, which) -> {
                    constraintLabelAlarm.setContraintRegex(editText.getText().toString());
                    updateListener.update();
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    private void showEditConstraintType(ConstraintLabelAlarm constraintLabelAlarm) {
        TypeConstraintEditDialog alertDialog = new TypeConstraintEditDialog(context, constraintLabelAlarm, updateListener);
        alertDialog.setTitle("Edité type contrainte");
        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
