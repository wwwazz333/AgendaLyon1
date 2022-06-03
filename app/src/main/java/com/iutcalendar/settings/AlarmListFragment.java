package com.iutcalendar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.Alarm;
import com.iutcalendar.alarm.AlarmRecycleView;
import com.iutcalendar.alarm.AlarmRing;
import com.iutcalendar.alarm.PersonnalAlarmManager;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.data.FileGlobal;


public class AlarmListFragment extends Fragment {


    private RecyclerView recyclerViewAlarm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_alarm, container, false);

        recyclerViewAlarm = view.findViewById(R.id.recycle_alarm);

        //TODO update quand arrive sur page pas à avoir à actualiser
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

    private void saveAlarm(){
        PersonnalAlarmManager.getInstance(getContext()).save(getContext());
    }


    /*#################MENU BAR#################*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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