package com.iutcalendar.alarm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;

import java.util.List;

public class AlarmRecycleView extends RecyclerView.Adapter<AlarmViewHolder> {

    List<AlarmRing> list;
    ClickForUpdateListener updateClick;

    public AlarmRecycleView(List<AlarmRing> list, ClickForUpdateListener updateClick) {
        this.list = list;
        this.updateClick = updateClick;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.alarm_card, parent, false);

        return new AlarmViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmRing alarmRing = list.get(position);
        CurrentDate dateRing = alarmRing.getDateTime();


        if (DataGlobal.isDebug(holder.view.getContext())) {
            holder.view.setOnClickListener(view -> {
                Log.d("Alarm", list.size() + " -> " + position + " " + dateRing.toString() + " at " + dateRing.timeToString());
                Toast.makeText(view.getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            });
        }


        holder.horaire.setText(dateRing.timeToString());

        String dateAffichage = dateRing.getRelativeDayName(holder.view.getContext());
        if (Character.isDigit(dateAffichage.charAt(dateAffichage.length() - 1))) {
            holder.date.setText(dateAffichage.substring(0, dateAffichage.length() - 5));//enlerver l'année
        } else {
            holder.date.setText(dateAffichage);
        }

        holder.isActivateSwitch.setChecked(alarmRing.isActivate());

        holder.isActivateSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            alarmRing.setActivate(isChecked);
            updateClick.update();
        });

        holder.trash.setOnClickListener(view -> {
            PersonnalAlarmManager.getInstance(view.getContext()).remove(view.getContext(), alarmRing);
            updateClick.update();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}