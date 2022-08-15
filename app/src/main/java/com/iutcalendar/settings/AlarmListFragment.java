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
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.alarm.AlarmRecycleView;
import com.iutcalendar.alarm.PersonnalAlarmManager;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.data.FileGlobal;


public class AlarmListFragment extends AbstractFragmentWitheMenu {


    private RecyclerView recyclerViewAlarm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        recyclerViewAlarm = view.findViewById(R.id.recycle_alarm);

        updateAlarm();
        return view;
    }


    private void updateAlarm() {
        Alarm.setUpAlarm(getContext(), new Calendrier(FileGlobal.readFile(FileGlobal.getFileDownload(getContext()))));


        AlarmRecycleView adapter = new AlarmRecycleView(
                PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList(), this::saveAlarm);
        recyclerViewAlarm.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerViewAlarm.setLayoutManager(layout);

        saveAlarm();
        Log.d("Alarm", "updateAlarm : " + PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList().size());
    }

    private void saveAlarm() {
        PersonnalAlarmManager.getInstance(getContext()).save(getContext());
    }


    /*#################MENU BAR#################*/

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_settings_alarm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            updateAlarm();
        }
        return super.onOptionsItemSelected(item);
    }
}