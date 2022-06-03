package com.iutcalendar.alarm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;

import java.util.List;

public class AlarmRecycleView extends RecyclerView.Adapter<AlarmViewHolder> {

    List<AlarmRing> list;
    AlarmViewHolder viewHolder;
    ClickForUpdateListener updateClick;

    public AlarmRecycleView(List<AlarmRing> list, ClickForUpdateListener updateClick) {
        this.list = list;
        Log.d("Alarm", "coutn " + this.getItemCount());
        
        this.updateClick = updateClick;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.alarm_card, parent, false);

        viewHolder = new AlarmViewHolder(eventView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        final int index = viewHolder.getAbsoluteAdapterPosition();
        AlarmRing alarmRing = list.get(position);
        CurrentDate dateRing = alarmRing.getDateTime();


        viewHolder.view.setOnClickListener(view -> {
            Log.d("Alarm", list.size() + " -> " + position + " " + dateRing.toString() + " at " + dateRing.timeToString());
        });

        viewHolder.horaire.setText(dateRing.timeToString());


        String dateAffichage = dateRing.getRelativeDayName(viewHolder.view.getContext());
        if (Character.isDigit(dateAffichage.charAt(dateAffichage.length() - 1))) {
            viewHolder.date.setText(dateAffichage.substring(0, dateAffichage.length() - 5));//enlerver l'année
        } else {
            viewHolder.date.setText(dateAffichage);
        }


        viewHolder.isActivateSwitch.setChecked(alarmRing.isActivate());

        viewHolder.isActivateSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            alarmRing.setActivate(isChecked);
            updateClick.update();
        });

        viewHolder.trash.setOnClickListener(view -> {
            PersonnalAlarmManager.getInstance(view.getContext()).remove(view.getContext(), alarmRing);
            updateClick.update();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}