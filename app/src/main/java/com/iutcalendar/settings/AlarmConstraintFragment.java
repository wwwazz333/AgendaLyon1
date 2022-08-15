package com.iutcalendar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iutcalendar.menu.AbstractFragmentWitheMenu;
import com.univlyon1.tools.agenda.R;
import com.iutcalendar.alarm.condition.AlarmConditionManager;
import com.iutcalendar.alarm.condition.label_constraint.AlarmConstraintLabel;
import com.iutcalendar.alarm.condition.label_constraint.AlarmLabelConstraintRecycleView;
import com.iutcalendar.dialog.DialogMessage;

public class AlarmConstraintFragment extends AbstractFragmentWitheMenu {

    RecyclerView recyclerViewConstraint;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm_constraint, container, false);

        initVaraible();

        updateConstraint();
        return view;
    }

    private void initVaraible() {
        recyclerViewConstraint = view.findViewById(R.id.recycleView);
    }

    private void updateConstraint() {
        AlarmLabelConstraintRecycleView adapter = new AlarmLabelConstraintRecycleView(getContext(),
                AlarmConditionManager.getInstance(getContext()).getAllConstraints(), this::saveConstraint, this::updateConstraint);
        recyclerViewConstraint.setAdapter(adapter);
        recyclerViewConstraint.setLayoutManager(new LinearLayoutManager(getActivity()));

        //save si des changements de constraint ont été fait
        saveConstraint();
        Log.d("Constraint", "updateConstraint");
    }

    private void saveConstraint() {
        AlarmConditionManager.getInstance(getContext()).save(getContext());
    }

    private void addConstraint() {
        AlarmConditionManager.getInstance(getContext()).addConstraint(new AlarmConstraintLabel(AlarmConstraintLabel.Containing.MUST_NOT_CONTAIN, ""));
        updateConstraint();
    }


    /*#################MENU BAR#################*/

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_help_and_add_alarm, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("MenuBar", "fragment");

        if (id == R.id.addBtn) {
            addConstraint();

        } else if (id == R.id.aideBtn) {
            DialogMessage.showAide(getContext(), getString(R.string.Help), getString(R.string.aid_constraint_alarm));
        }
        return super.onOptionsItemSelected(item);
    }
}