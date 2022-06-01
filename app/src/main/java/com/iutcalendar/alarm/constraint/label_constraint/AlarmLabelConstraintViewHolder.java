package com.iutcalendar.alarm.constraint.label_constraint;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmLabelConstraintViewHolder extends RecyclerView.ViewHolder  {
    View view;
    TextView typeConstraint, constraint;

    public AlarmLabelConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        constraint = itemView.findViewById(R.id.constraint);
        typeConstraint = itemView.findViewById(R.id.type_constraint);
        view = itemView;
    }
}
