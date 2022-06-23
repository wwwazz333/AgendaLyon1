package com.iutcalendar.alarm.condition.label_constraint;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.univlyon1.tools.agenda.R;

public class AlarmLabelConstraintViewHolder extends RecyclerView.ViewHolder {
    View view;
    Button typeConstraint;
    EditText constraint;
    ImageButton delBtn;


    public AlarmLabelConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        constraint = itemView.findViewById(R.id.constraint);
        typeConstraint = itemView.findViewById(R.id.type_constraint);
        delBtn = itemView.findViewById(R.id.del_btn);
    }
}
