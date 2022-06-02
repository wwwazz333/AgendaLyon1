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
import com.iutcalendar.alarm.constraint.AlarmConditionRecycleView;
import com.iutcalendar.alarm.constraint.AlarmCondtion;
import com.iutcalendar.alarm.constraint.AlarmConditionManager;
import com.iutcalendar.calendrier.DateCalendrier;
import com.iutcalendar.dialog.DialogMessage;

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
        AlarmConditionRecycleView adapter = new AlarmConditionRecycleView(getContext(), getActivity(),
                AlarmConditionManager.getInstance(getContext()).getAllConstraint(), this::updateConstraint);
        recyclerViewConstraint.setAdapter(adapter);
        recyclerViewConstraint.setLayoutManager(new LinearLayoutManager(getActivity()));

        //save si des changement de constraint on été fait
        AlarmConditionManager.getInstance(getContext()).save(getContext());
        Log.d("Constraint", "updateConstraint");
    }

    private void addConstraint() {

        //demande heure début
        AlarmRing.askTime(getContext(), "Intervale inferieur (compris)", (view, hourOfDayBeging, minuteBeging) -> {
            long beging = DateCalendrier.getHourInMillis(hourOfDayBeging, minuteBeging);

            //demande heure fin
            AlarmRing.askTime(getContext(), "Intervale supérieur (compris)", (view1, hourOfDayEnd, minuteEnd) -> {
                long end = DateCalendrier.getHourInMillis(hourOfDayEnd, minuteEnd);

                if (beging > end) {
                    DialogMessage.showWarning(getContext(), "Intervale", "La borne supérieur ne peut pas être plus petite que la borne infèrieur.");
                    return;
                }


                //demande heure sonnerie
                AlarmRing.askTime(getContext(), "Horaire à la quelle faire sonné", (view2, hourOfDayAlarmAt, minuteAlarmAt) -> {
                    long alarmAt = DateCalendrier.getHourInMillis(hourOfDayAlarmAt, minuteAlarmAt);


                    AlarmConditionManager.getInstance(getContext()).addConstraint(new AlarmCondtion(beging, end, alarmAt));
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
        inflater.inflate(R.menu.menu_action_constraint_alarm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addBtn) {
            addConstraint();

        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(getContext(), "Aide", getString(R.string.aide_conditon_alarm));
        }
        return super.onOptionsItemSelected(item);
    }
}