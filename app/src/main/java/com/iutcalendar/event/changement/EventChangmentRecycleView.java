package com.iutcalendar.event.changement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

import java.util.List;

public class EventChangmentRecycleView extends RecyclerView.Adapter<EventChangmentViewHolder> {
    private final Context context;
    List<EventChangment> changementsList;

    public EventChangmentRecycleView(Context context, List<EventChangment> changementsList) {
        this.context = context;
        this.changementsList = changementsList;
    }

    @NonNull
    @Override
    public EventChangmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.changement_card, parent, false);

        return new EventChangmentViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventChangmentViewHolder holder, int position) {
        EventChangment eventChangment = changementsList.get(position);


        holder.name.setText(eventChangment.getEventChanged().getNameEvent());

        holder.action.setText(eventChangment.getInfoChange().getChangement().toString(context));
        holder.actionDate.setText(eventChangment.getDateOfTheChangement().toString());

        switch (eventChangment.getInfoChange().getChangement()) {
            case ADD:
                holder.globalLayout.removeView(holder.fromLayout);
                holder.toDate.setText(eventChangment.getInfoChange().getNewDate().toString());
                break;
            case DELETE:
                holder.globalLayout.removeView(holder.toLayout);
                holder.fromDate.setText(eventChangment.getInfoChange().getPrevDate().toString());
                break;
            case MOVE:
                holder.fromDate.setText(eventChangment.getInfoChange().getPrevDate().toString());
                holder.toDate.setText(eventChangment.getInfoChange().getNewDate().toString());
                break;
        }


    }

    @Override
    public int getItemCount() {
        return changementsList.size();
    }
}
