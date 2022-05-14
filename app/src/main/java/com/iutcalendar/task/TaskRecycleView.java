package com.iutcalendar.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.event.ClickListiner;

import java.util.List;

public class TaskRecycleView extends RecyclerView.Adapter<TaskViewHolder> {

    List<Task> list;
    TaskViewHolder viewHolder;

    Context context;
    ClickListiner listener;

    public TaskRecycleView(List<Task> list, Context context, ClickListiner listiner) {
        this.list = list;
        this.context = context;
        this.listener = listiner;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.task_card, parent, false);
        viewHolder = new TaskViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final int index = viewHolder.getAdapterPosition();
        Task e = list.get(position);
        viewHolder.name.setText(e.getName());
        viewHolder.description.setText(e.getDescription());

        viewHolder.view.setOnClickListener(view -> listener.click(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}