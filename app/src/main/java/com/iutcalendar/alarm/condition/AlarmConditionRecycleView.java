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
    AlarmConditionViewHolder viewHolder;
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

        viewHolder = new AlarmConditionViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmConditionViewHolder holder, int position) {
        final int index = viewHolder.getAbsoluteAdapterPosition();
        AlarmCondtion alarmConstraint = list.get(position);

        viewHolder.begingHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getBeging()));
        viewHolder.endHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getEnd()));
        viewHolder.ringHour.setText(DateCalendrier.timeLongToString(alarmConstraint.getAlarmAt()));


        viewHolder.begingHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setBeging(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });
        viewHolder.endHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setEnd(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });
        viewHolder.ringHour.setOnClickListener(view -> {
            AlarmRing.askTime(context, (view1, hourOfDay, minute) -> {
                alarmConstraint.setAlarmAt(DateCalendrier.getHourInMillis(hourOfDay, minute));
                updateListener.update();
            });
        });

        viewHolder.delBtn.setOnClickListener(view -> {
            AlarmConditionManager.getInstance(context).removeCondition(position);
            reloadListener.update();
        });


        //setCheckable days
        initDayCheck(alarmConstraint, viewHolder.monday, GregorianCalendar.MONDAY);
        initDayCheck(alarmConstraint, viewHolder.tuesday, GregorianCalendar.TUESDAY);
        initDayCheck(alarmConstraint, viewHolder.wednesday, GregorianCalendar.WEDNESDAY);
        initDayCheck(alarmConstraint, viewHolder.thursday, GregorianCalendar.THURSDAY);
        initDayCheck(alarmConstraint, viewHolder.friday, GregorianCalendar.FRIDAY);
        initDayCheck(alarmConstraint, viewHolder.saturday, GregorianCalendar.SATURDAY);
        initDayCheck(alarmConstraint, viewHolder.sunday, GregorianCalendar.SUNDAY);
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
                alarmConstraint.getDaysEnabled().remove(new Integer(value));

            updateListener.update();
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
