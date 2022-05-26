package com.iutcalendar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.AlarmRecycleView;
import com.iutcalendar.alarm.ClickForUpdateListener;
import com.iutcalendar.alarm.PersonnalAlarmManager;


public class SettingsAlarmFragment extends Fragment {


    private RecyclerView recyclerViewAlarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_alarm, container, false);

//        TextView text = view.findViewById(R.id.test);
//        StringBuilder txt = new StringBuilder();
//        CurrentDate affichage = new CurrentDate();
//        for (AlarmRing dateAlarm : PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList()) {
//            affichage.setTimeInMillis(dateAlarm.getTimeInMillis());
//            txt.append(affichage.getRelativeDayName(getContext()))
//                    .append(" : ")
//                    .append(affichage.timeToString())
//                    .append("\n");
//        }
       /* if (getContext() != null && Alarm.getAlarm(getContext()) != -1) {
            DateCalendrier time = new DateCalendrier();
            time.setTimeInMillis(Alarm.getAlarm(getContext()));
            text.setText(time.toString());
        } else {
            text.setText(R.string.No_alarm);
        }*/
//        text.setText(txt.toString());


        recyclerViewAlarm = view.findViewById(R.id.recycle_alarm);
        updateAlarm();

        return view;
    }

    private void updateAlarm() {
        ClickForUpdateListener listener = this::updateAlarm;
        AlarmRecycleView adapter = new AlarmRecycleView(
                PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList(), listener);
        recyclerViewAlarm.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setReverseLayout(true);//afficher d'abord les alarm les plus récente
        recyclerViewAlarm.setLayoutManager(layout);
        PersonnalAlarmManager.getInstance(getContext()).save(getContext());
        Log.d("Alarm", "updateAlarm : " + PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList().size());
    }
}