package com.iutcalendar.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.AlarmRing;
import com.iutcalendar.alarm.constraint.AlarmConstraintRecycleView;
import com.iutcalendar.alarm.constraint.ConstraintAlarm;
import com.iutcalendar.alarm.constraint.ConstraintAlarmManager;
import com.iutcalendar.calendrier.DateCalendrier;

public class AlarmHoraireFragment extends Fragment {

    RecyclerView recyclerViewConstraint;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm_horaire, container, false);

        initVaraible();


        updateConstraint();
        return view;
    }

    private void initVaraible() {
        recyclerViewConstraint = view.findViewById(R.id.recycleView);
    }

    private void updateConstraint() {
        AlarmConstraintRecycleView adapter = new AlarmConstraintRecycleView(getContext(), getActivity(),
                ConstraintAlarmManager.getInstance(getContext()).getAllConstraint(), this::updateConstraint);
        recyclerViewConstraint.setAdapter(adapter);
        recyclerViewConstraint.setLayoutManager(new LinearLayoutManager(getActivity()));

        //save si des changement de constraint on été fait
        ConstraintAlarmManager.getInstance(getContext()).save(getContext());
        Log.d("Constraint", "updateConstraint");
    }

    private void addConstraint() {

        //demande heure début
        AlarmRing.askTime(getContext(), (view, hourOfDayBeging, minuteBeging) -> {
            long beging = DateCalendrier.getHourInMillis(hourOfDayBeging, minuteBeging);

            //demande heure fin
            AlarmRing.askTime(getContext(), (view1, hourOfDayEnd, minuteEnd) -> {
                long end = DateCalendrier.getHourInMillis(hourOfDayEnd, minuteEnd);


                //demande heure sonnerie
                AlarmRing.askTime(getContext(), (view2, hourOfDayAlarmAt, minuteAlarmAt) -> {
                    long alarmAt = DateCalendrier.getHourInMillis(hourOfDayAlarmAt, minuteAlarmAt);


                    ConstraintAlarmManager.getInstance(getContext()).addConstraint(new ConstraintAlarm(beging, end, alarmAt));
                    updateConstraint();
                });
            });
        });
    }


    /*#################MENU BAR#################*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_add_constraint_alarm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addBtn) {
            addConstraint();

        }
        return super.onOptionsItemSelected(item);
    }
}