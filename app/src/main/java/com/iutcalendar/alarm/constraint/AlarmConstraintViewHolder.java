package com.iutcalendar.alarm.constraint;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmConstraintViewHolder extends RecyclerView.ViewHolder  {
    View view;
    TextView begingHour, endHour, ringHour;
    ImageButton delBtn;

    RecyclerView listConstraint;

    public AlarmConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        begingHour = itemView.findViewById(R.id.begingHour);
        endHour = itemView.findViewById(R.id.endHour);
        ringHour = itemView.findViewById(R.id.ringHour);
        listConstraint = itemView.findViewById(R.id.listConstraint);
        delBtn = itemView.findViewById(R.id.del_btn);
        view = itemView;
    }
}
