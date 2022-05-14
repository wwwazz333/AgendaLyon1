package com.iutcalendar.task;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class TaskViewHolder  extends RecyclerView.ViewHolder {

    TextView name;
    TextView description;
    View view;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        description = itemView.findViewById(R.id.description);

        view = itemView;
    }
}