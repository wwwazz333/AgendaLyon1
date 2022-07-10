package com.univlyon1.tools.agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.iutcalendar.calendrier.CurrentDate;


public class SearchRoomFragment extends Fragment {

    private Button searchBtn, resetBtn;

    private EditText timeChooser;
    private CalendarView calendarView;
    private View view;

    public SearchRoomFragment() {
    }

    private void initVariables() {
        searchBtn = view.findViewById(R.id.searchBtn);
        resetBtn = view.findViewById(R.id.resetBtn);

        timeChooser = view.findViewById(R.id.editTextTime);
        calendarView = view.findViewById(R.id.calendarView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_room, container, false);

        initVariables();

        resetValues();

        resetBtn.setOnClickListener((view) -> resetValues());

        return view;
    }

    private void resetValues() {
        timeChooser.setText(new CurrentDate().timeToString());

        calendarView.setDate(new CurrentDate().getTimeInMillis());
    }
}