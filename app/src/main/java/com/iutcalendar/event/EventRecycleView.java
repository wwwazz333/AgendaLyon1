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
    GestureEventManager gestureEventManager;
    ClickListener clickListener;

    public EventRecycleView(List<EventCalendrier> list, Context context, ClickListener clickListener, GestureEventManager eventManager) {
        this.list = list;
        this.context = context;
        this.clickListener = clickListener;
        this.gestureEventManager = eventManager;
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
        viewHolder.debut.setText(e.getDate().timeToString());
        viewHolder.fin.setText(e.getDate().addTime(e.getDuree()).timeToString());
        viewHolder.summary.setText(e.getSummary());
        viewHolder.salle.setText(e.getSalle().replace("\\,", "\n"));


        int numberTask = PersonnalCalendrier.getInstance().getLinkedTask(e).size();
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
                gestureEventManager.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                gestureEventManager.onSwipeLeft();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                gestureEventManager.onSwipeDown();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
