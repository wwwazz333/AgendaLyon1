package com.iutcalendar.alarm.condition;

import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmConditionViewHolder extends RecyclerView.ViewHolder  {
    View view;
    TextView begingHour, endHour, ringHour;
    ImageButton delBtn;

    CheckedTextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;


    public AlarmConditionViewHolder(@NonNull View itemView) {
        super(itemView);

        begingHour = itemView.findViewById(R.id.begingHour);
        endHour = itemView.findViewById(R.id.endHour);
        ringHour = itemView.findViewById(R.id.ringHour);
//        listConstraint = itemView.findViewById(R.id.listConstraint);
        delBtn = itemView.findViewById(R.id.del_btn);

        monday = itemView.findViewById(R.id.monday);
        tuesday = itemView.findViewById(R.id.tuesday);
        wednesday = itemView.findViewById(R.id.wednesday);
        thursday = itemView.findViewById(R.id.thursday);
        friday = itemView.findViewById(R.id.friday);
        saturday = itemView.findViewById(R.id.saturday);
        sunday = itemView.findViewById(R.id.sunday);


//        addConstraintBtn = itemView.findViewById(R.id.add_constraint_label);


        view = itemView;
    }
}
