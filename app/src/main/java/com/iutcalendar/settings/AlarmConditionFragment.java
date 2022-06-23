package com.iutcalendar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.AlarmRing;
import com.iutcalendar.alarm.condition.AlarmConditionManager;
import com.iutcalendar.alarm.condition.AlarmConditionRecycleView;
import com.iutcalendar.alarm.condition.AlarmCondtion;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.dialog.DialogMessage;

public class AlarmConditionFragment extends Fragment {

    RecyclerView recyclerViewConstraint;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm_condition, container, false);

        initVaraible();


        updateConditions();
        return view;
    }

    private void initVaraible() {
        recyclerViewConstraint = view.findViewById(R.id.recycleView);
    }

    private void updateConditions() {
        AlarmConditionRecycleView adapter = new AlarmConditionRecycleView(getContext(),
                AlarmConditionManager.getInstance(getContext()).getAllConditions(), this::saveConditions, this::updateConditions);
        recyclerViewConstraint.setAdapter(adapter);
        recyclerViewConstraint.setLayoutManager(new LinearLayoutManager(getActivity()));

        //save si des changement de constraint on été fait
        saveConditions();
        Log.d("Constraint", "updateConstraint");
    }

    private void saveConditions() {
        AlarmConditionManager.getInstance(getContext()).save(getContext());
    }

    private void addCondition() {

        //demande heure début
        AlarmRing.askTime(getContext(), getString(R.string.BorneInf), (view, hourOfDayBeging, minuteBeging) -> {
            long beging = DateCalendrier.getHourInMillis(hourOfDayBeging, minuteBeging);

            //demande heure fin
            AlarmRing.askTime(getContext(), getString(R.string.BorneSup), (view1, hourOfDayEnd, minuteEnd) -> {
                long end = DateCalendrier.getHourInMillis(hourOfDayEnd, minuteEnd);

                if (beging > end) {
                    DialogMessage.showWarning(getContext(), getString(R.string.Interval), getString(R.string.born_sup_et_inf_inverser));
                    return;
                }


                //demande heure sonnerie
                AlarmRing.askTime(getContext(), getString(R.string.TimeToRing), (view2, hourOfDayAlarmAt, minuteAlarmAt) -> {
                    long alarmAt = DateCalendrier.getHourInMillis(hourOfDayAlarmAt, minuteAlarmAt);


                    AlarmConditionManager.getInstance(getContext()).addCondition(new AlarmCondtion(beging, end, alarmAt));
                    updateConditions();
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
        inflater.inflate(R.menu.menu_action_constraint_alarm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addBtn) {
            addCondition();

        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(getContext(), getString(R.string.Help), getString(R.string.aide_conditon_alarm));
        }
        return super.onOptionsItemSelected(item);
    }
}