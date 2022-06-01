package com.iutcalendar.alarm.constraint;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmConstraintViewHolder extends RecyclerView.ViewHolder  {
    View view;
    TextView begingHour, endHour, ringHour;

    RecyclerView listConstraint;

    public AlarmConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        begingHour = itemView.findViewById(R.id.begingHour);
        endHour = itemView.findViewById(R.id.endHour);
        ringHour = itemView.findViewById(R.id.ringHour);
        listConstraint = itemView.findViewById(R.id.listConstraint);
        view = itemView;
    }
}
