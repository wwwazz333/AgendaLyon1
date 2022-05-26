package com.iutcalendar.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.AlarmRing;
import com.iutcalendar.alarm.PersonnalAlarmManager;
import com.iutcalendar.calendrier.DateCalendrier;


public class SettingsAlarmFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_alarm, container, false);

        TextView text = view.findViewById(R.id.test);
        StringBuilder txt = new StringBuilder();
        DateCalendrier affichage = new DateCalendrier();
        for (AlarmRing dateAlarm : PersonnalAlarmManager.getInstance(getContext()).getAllAlarmToList()) {
            affichage.setTimeInMillis(dateAlarm.getTimeInMillis());
            txt.append(affichage).append("\n");
        }
       /* if (getContext() != null && Alarm.getAlarm(getContext()) != -1) {
            DateCalendrier time = new DateCalendrier();
            time.setTimeInMillis(Alarm.getAlarm(getContext()));
            text.setText(time.toString());
        } else {
            text.setText(R.string.No_alarm);
        }*/
        text.setText(txt.toString());

        return view;
    }
}