package com.iutcalendar.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.EventCalendrier;

import java.util.List;

public class EventRecycleView extends RecyclerView.Adapter<EventViewHolder> {

    List<EventCalendrier> list;
    EventViewHolder viewHolder;

    Context context;
    ClickListiner listiner;

    public EventRecycleView(List<EventCalendrier> list, Context context, ClickListiner listiner) {
        this.list = list;
        this.context = context;
        this.listiner = listiner;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.event_card, parent, false);
        viewHolder = new EventViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final int index = viewHolder.getAdapterPosition();
        EventCalendrier e = list.get(position);
        viewHolder.debut.setText(new StringBuilder().append(e.getDate().getHour()).append("h").toString());
        viewHolder.fin.setText(new StringBuilder().append(e.getDate().addTime(e.getDuree()).getHour()).append("h").toString());
        viewHolder.summary.setText(e.getSummary());
        viewHolder.salle.setText(e.getSalle());

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listiner.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
