package com.iutcalendar.alarm.constraint.label_constraint;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmLabelConstraintViewHolder extends RecyclerView.ViewHolder  {
    View view;
    TextView typeConstraint;
    TextView constraint;
    ImageButton delBtn;

    Spinner spinner;

    public AlarmLabelConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        constraint = itemView.findViewById(R.id.constraint);
        typeConstraint = itemView.findViewById(R.id.type_constraint);
        delBtn = itemView.findViewById(R.id.del_btn);


        spinner = itemView.findViewById(R.id.spinner);



    }
}
