package com.iutcalendar.alarm.constraint;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.constraint.label_constraint.AlarmLabelConstraintRecycleView;
import com.iutcalendar.calendrier.DateCalendrier;

import java.util.List;

public class AlarmConstraintRecycleView extends RecyclerView.Adapter<AlarmConstraintViewHolder> {

    List<ConstraintAlarm> list;
    AlarmConstraintViewHolder viewHolder;
    Activity activity;
    Context context;

    public AlarmConstraintRecycleView(Context context, Activity activity, List<ConstraintAlarm> list) {
        this.list = list;
        this.activity  = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public AlarmConstraintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.constraint_alarm_card, parent, false);

        viewHolder = new AlarmConstraintViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmConstraintViewHolder holder, int position) {
        final int index = viewHolder.getAbsoluteAdapterPosition();
        ConstraintAlarm alarmConstraint = list.get(position);

        viewHolder.begingHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getBeging()));
        viewHolder.endHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getEnd()));
        viewHolder.ringHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getAlarmAt()));


        AlarmLabelConstraintRecycleView adapter = new AlarmLabelConstraintRecycleView(context, alarmConstraint.getConstraintLabels());
        viewHolder.listConstraint.setAdapter(adapter);
        viewHolder.listConstraint.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
