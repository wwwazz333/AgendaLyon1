package com.iutcalendar.task;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView text;
    View view;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        text = itemView.findViewById(R.id.textTask);
        view = itemView;
    }
}