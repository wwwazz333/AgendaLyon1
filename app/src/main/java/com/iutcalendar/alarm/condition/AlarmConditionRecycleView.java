package com.iutcalendar.alarm.condition;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.AlarmRing;
import com.iutcalendar.alarm.ClickForUpdateListener;
import com.iutcalendar.alarm.condition.label_constraint.AlarmLabelConstraintRecycleView;
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel;
import com.iutcalendar.calendrier.DateCalendrier;

import java.util.GregorianCalendar;
import java.util.List;

public class AlarmConditionRecycleView extends RecyclerView.Adapter<AlarmConditionViewHolder> {

    List<AlarmCondtion> list;
//    AlarmConditionViewHolder viewHolder;
    Context context;
    ClickForUpdateListener updateListener, reloadListener;

    public AlarmConditionRecycleView(Context context, List<AlarmCondtion> list, ClickForUpdateListener updateListener, ClickForUpdateListener reload) {
        this.list = list;
        this.context = context;
        this.updateListener = updateListener;
        this.reloadListener = reload;
    }

    @NonNull
    @Override
    public AlarmConditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.condition_alarm_card, parent, false);

        return new AlarmConditionViewHolder(eventView);
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmConditionViewHolder holder, int position) {
        AlarmCondtion alarmConstraint = list.get(position);

        holder.begingHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getBeging()));
        holder.endHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getEnd()));
        holder.ringHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getAlarmAt()));


        holder.begingHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setBeging(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });
        holder.endHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setEnd(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });
        holder.ringHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setAlarmAt(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });

        holder.delBtn.setOnClickListener(view -> {
            AlarmConditionManager.getInstance(context).removeCondition(position);
            reloadListener.update();
        });


        //setCheckable days
        initDayCheck(alarmConstraint, holder.monday, GregorianCalendar.MONDAY);
        initDayCheck(alarmConstraint, holder.tuesday, GregorianCalendar.TUESDAY);
        initDayCheck(alarmConstraint, holder.wednesday, GregorianCalendar.WEDNESDAY);
        initDayCheck(alarmConstraint, holder.thursday, GregorianCalendar.THURSDAY);
        initDayCheck(alarmConstraint, holder.friday, GregorianCalendar.FRIDAY);
        initDayCheck(alarmConstraint, holder.saturday, GregorianCalendar.SATURDAY);
        initDayCheck(alarmConstraint, holder.sunday, GregorianCalendar.SUNDAY);
    }

    private void initDayCheck(AlarmCondtion alarmConstraint, CheckedTextView check, int value) {
        if (alarmConstraint.getDaysEnabled().contains(value)) {
            check.setChecked(true);
        }

        check.setOnClickListener(v -> {
            check.toggle();

            if (check.isChecked())
                alarmConstraint.getDaysEnabled().add(value);
            else
                alarmConstraint.getDaysEnabled().remove(Integer.valueOf(value));

            updateListener.update();
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
