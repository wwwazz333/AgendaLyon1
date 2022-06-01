package com.iutcalendar.alarm.constraint.label_constraint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

import java.util.List;

public class AlarmLabelConstraintRecycleView extends RecyclerView.Adapter<AlarmLabelConstraintViewHolder> {

    List<ConstraintLabelAlarm> list;
    AlarmLabelConstraintViewHolder viewHolder;
    Context context;

    public AlarmLabelConstraintRecycleView(Context context, List<ConstraintLabelAlarm> list) {
        this.list = list;
        this.context = context;
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

        viewHolder.typeConstraint.setText(constraintLabelAlarm.getTypeDeContraint().toString());
        viewHolder.constraint.setText(constraintLabelAlarm.getContraintRegex());

        viewHolder.view.setOnClickListener(view1 -> showEditDialog());

    }

    private void showEditDialog() {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
