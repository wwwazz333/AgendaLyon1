package com.iutcalendar.event.changement;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class EventChangmentViewHolder extends RecyclerView.ViewHolder {
    TextView name, action, actionDate, fromLabel, fromDate, toLabel, toDate;
    LinearLayout globalLayout, fromLayout, toLayout;

    public EventChangmentViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.nameEvent);

        action = itemView.findViewById(R.id.actionLabel);
        actionDate = itemView.findViewById(R.id.actionDate);

        fromLabel = itemView.findViewById(R.id.fromLabel);
        fromDate = itemView.findViewById(R.id.fromDate);

        toLabel = itemView.findViewById(R.id.toLabel);
        toDate = itemView.findViewById(R.id.toDate);

        globalLayout = itemView.findViewById(R.id.globalLayout);
        fromLayout = itemView.findViewById(R.id.fromLayout);
        toLayout = itemView.findViewById(R.id.toLayout);

    }
}
