package com.iutcalendar.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.DateCalendrier;


public class SettingsAlarmFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_alarm, container, false);

        TextView text = view.findViewById(R.id.test);
        DateCalendrier time = new DateCalendrier();
        time.setTimeInMillis(Alarm.getAlarm(getContext()));
        text.setText(new CurrentDate().timeToString());
        return view;
    }
}