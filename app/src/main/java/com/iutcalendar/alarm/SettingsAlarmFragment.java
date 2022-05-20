package com.iutcalendar.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.DateCalendrier;


public class SettingsAlarmFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_alarm, container, false);

        TextView text = view.findViewById(R.id.test);
        if (getActivity() != null && Alarm.getAlarm(getContext()) != -1) {
            DateCalendrier time = new DateCalendrier();
            time.setTimeInMillis(Alarm.getAlarm(getContext()));
            text.setText(time.timeToString());
        } else {
            text.setText(R.string.No_alarm);
        }

        return view;
    }
}