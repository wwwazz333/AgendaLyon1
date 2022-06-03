package com.iutcalendar.alarm.condition.label_constraint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.ClickForUpdateListener;
import com.iutcalendar.alarm.condition.AlarmConditionManager;

import java.util.List;

public class AlarmLabelConstraintRecycleView extends RecyclerView.Adapter<AlarmLabelConstraintViewHolder> {

    List<AlarmConstraintLabel> list;
    //    AlarmLabelConstraintViewHolder viewHolder;
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

        return new AlarmLabelConstraintViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmLabelConstraintViewHolder holder, int position) {
        AlarmConstraintLabel constraintLabelAlarm = list.get(position);

        holder.typeConstraint.setText(constraintLabelAlarm.getTypeDeContraint().toString(context));
        holder.typeConstraint.setOnClickListener(view -> showEditConstraintType(holder, constraintLabelAlarm));

        holder.constraint.setText(constraintLabelAlarm.getContraintText());
        holder.constraint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                constraintLabelAlarm.setConstraintString(holder.constraint.getText().toString());
                updateListener.update();
            }
        });
        holder.constraint.setSelected(false);
//        holder.constraint.setOnClickListener(view -> showEditConstraint(holder, constraintLabelAlarm));


        holder.delBtn.setOnClickListener(view -> {
            AlarmConditionManager.getInstance(context).removeConstraint(constraintLabelAlarm);
            reloadListener.update();
        });

    }


    private void showEditConstraint(@NonNull AlarmLabelConstraintViewHolder holder, AlarmConstraintLabel constraintLabelAlarm) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edité contrainte");
        alertDialog.setMessage("Contrainte");

        final EditText editText = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setText(holder.constraint.getText());
        alertDialog.setView(editText);

        alertDialog.setPositiveButton(context.getString(R.string.submit),
                (dialog, which) -> {
                    constraintLabelAlarm.setConstraintString(editText.getText().toString());
                    holder.constraint.setText(editText.getText());
                    updateListener.update();
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    private void showEditConstraintType(@NonNull AlarmLabelConstraintViewHolder holder, AlarmConstraintLabel constraintLabelAlarm) {
        AlarmConstraintLabel.Containing type;
        if (holder.typeConstraint.getText().toString().equals(AlarmConstraintLabel.Containing.MUST_NOT_CONTAIN.toString(context))) {
            type = AlarmConstraintLabel.Containing.MUST_NOT_BE_EXACTLY;
        } else {
            type = AlarmConstraintLabel.Containing.MUST_NOT_CONTAIN;
        }

        constraintLabelAlarm.setTypeDeContraint(type);
        holder.typeConstraint.setText(type.toString(context));
        updateListener.update();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
