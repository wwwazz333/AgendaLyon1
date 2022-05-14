package com.iutcalendar.event;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    TextView debut, fin, summary, salle, countTask;
    View view;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);

        debut = itemView.findViewById(R.id.debut);
        fin = itemView.findViewById(R.id.fin);
        summary = itemView.findViewById(R.id.summary);
        salle = itemView.findViewById(R.id.salle);
        countTask = itemView.findViewById(R.id.countTask);

        view = itemView;
    }
}
