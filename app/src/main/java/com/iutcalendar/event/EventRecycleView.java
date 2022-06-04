package com.iutcalendar.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.swiping.GestureEventManager;
import com.iutcalendar.swiping.TouchGestureListener;
import com.iutcalendar.task.PersonnalCalendrier;

import java.util.List;

public class EventRecycleView extends RecyclerView.Adapter<EventViewHolder> {

    List<EventCalendrier> list;
    EventViewHolder viewHolder;

    Context context;
    ClickListener clickListener;

    public EventRecycleView(List<EventCalendrier> list, Context context, ClickListener clickListener) {
        this.list = list;
        this.context = context;
        this.clickListener = clickListener;
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
        final int index = viewHolder.getAbsoluteAdapterPosition();
        EventCalendrier e = list.get(position);
        viewHolder.debut.setText(e.getDate().timeToString());
        viewHolder.fin.setText(e.getDate().addTime(e.getDuree()).timeToString());
        viewHolder.summary.setText(e.getSummary());
        viewHolder.salle.setText(e.getSalle().replace("\\,", "\n"));


        int numberTask = PersonnalCalendrier.getInstance(context).getCountTaskOf(e);
        if (numberTask == 0) {
            viewHolder.countTask.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.countTask.setText(String.valueOf(numberTask));
        }
        viewHolder.view.setOnTouchListener(new TouchGestureListener(context, new GestureEventManager() {
            @Override
            protected void onClick() {
                clickListener.click(index);
                super.onClick();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
