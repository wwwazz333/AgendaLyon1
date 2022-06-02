package com.iutcalendar.alarm.condition.label_constraint;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.ClickForUpdateListener;
import com.iutcalendar.alarm.condition.AlarmConditionManager;

import java.util.List;

public class AlarmLabelConstraintRecycleView extends RecyclerView.Adapter<AlarmLabelConstraintViewHolder> {

    List<AlarmConstraintLabel> list;
    AlarmLabelConstraintViewHolder viewHolder;
    Context context;
    ClickForUpdateListener updateListener, reloadListener;

    public AlarmLabelConstraintRecycleView(Context context, List<AlarmConstraintLabel> list, ClickForUpdateListener updateListener, ClickForUpdateListener reloadListener) {
        this.list = list;
        this.context = context;
        this.updateListener = updateListener;
        this.reloadListener = reloadListener;
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
        AlarmConstraintLabel constraintLabelAlarm = list.get(position);

        viewHolder.typeConstraint.setText(constraintLabelAlarm.getTypeDeContraint().toString(context));
        viewHolder.typeConstraint.setOnClickListener(view -> {
            showEditConstraintType(constraintLabelAlarm);
        });

        viewHolder.constraint.setText(constraintLabelAlarm.getContraintText());
        viewHolder.constraint.setOnClickListener(view -> {
            showEditConstraint(constraintLabelAlarm);
        });


        viewHolder.delBtn.setOnClickListener(view -> {
            AlarmConditionManager.getInstance(context).removeConstraint(constraintLabelAlarm);
            reloadListener.update();
        });

    }

    private void showEditConstraint(AlarmConstraintLabel constraintLabelAlarm) {
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
                    constraintLabelAlarm.setConstraintString(editText.getText().toString());
                    viewHolder.constraint.setText(editText.getText());
                    updateListener.update();
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    private void showEditConstraintType(AlarmConstraintLabel constraintLabelAlarm) {
        AlarmConstraintLabel.Containing type;
        if (viewHolder.typeConstraint.getText().toString().equals(AlarmConstraintLabel.Containing.MUST_CONTAIN.toString(context))) {
            type = AlarmConstraintLabel.Containing.MUST_NOT_CONTAIN;
        } else {
            type = AlarmConstraintLabel.Containing.MUST_CONTAIN;
        }

        constraintLabelAlarm.setTypeDeContraint(type);
        viewHolder.typeConstraint.setText(type.toString(context));
        updateListener.update();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
