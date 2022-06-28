package com.iutcalendar.task;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.univlyon1.tools.agenda.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView text;
    ImageView alarmIcon;
    View view;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        text = itemView.findViewById(R.id.textSummary);
        alarmIcon = itemView.findViewById(R.id.iconAlarme);
        view = itemView;
    }
}