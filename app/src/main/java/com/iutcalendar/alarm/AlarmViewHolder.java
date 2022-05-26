package com.iutcalendar.alarm;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    TextView horaire, date;
    Switch isActivateSwitch;
    ImageButton trash;
    View view;

    public AlarmViewHolder(@NonNull View itemView) {
        super(itemView);

        horaire = itemView.findViewById(R.id.horaire);
        date = itemView.findViewById(R.id.date);
        isActivateSwitch = itemView.findViewById(R.id.is_activate);
        trash = itemView.findViewById(R.id.remove_btn);
        view = itemView;
    }
}
