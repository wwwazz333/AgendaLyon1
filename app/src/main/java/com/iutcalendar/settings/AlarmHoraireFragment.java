package com.iutcalendar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.constraint.AlarmConstraintRecycleView;
import com.iutcalendar.alarm.constraint.ConstraintAlarmManager;

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
        recyclerViewConstraint =view.findViewById(R.id.recycleView);
    }

    private void updateConstraint(){
        AlarmConstraintRecycleView adapter = new AlarmConstraintRecycleView(getContext(), getActivity(),
                ConstraintAlarmManager.getInstance(getContext()).getAllConstraint(), this::updateConstraint);
        recyclerViewConstraint.setAdapter(adapter);
        recyclerViewConstraint.setLayoutManager(new LinearLayoutManager(getActivity()));

        //save si des changement de constraint on été fait
        ConstraintAlarmManager.getInstance(getContext()).save(getContext());
        Log.d("Constraint", "updateConstraint");
    }

    private void addConstraint() {

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