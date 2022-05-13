package com.iutcalendar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.event.ClickListiner;
import com.iutcalendar.event.EventRecycleView;
import com.iutcalendar.swiping.GestureEventManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventFragment extends Fragment {

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        LinearLayout layout = view.findViewById(R.id.mainLayout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        File fileCal = FileGlobal.getFileDownload(getContext());


        if (fileCal.exists()) {
//            String str = DataSaver.getSavedCal(getContext());
            String str = FileGlobal.readFile(fileCal);
            Calendrier cal = new Calendrier(str);


            CurrentDate date = ((MainActivity) getActivity()).getCurrDate();

            List<EventCalendrier> eventToday = cal.getEventsOfDay(date);

            RecyclerView recycleView = view.findViewById(R.id.recycleView);
            ClickListiner listiner = index -> Toast.makeText(getActivity(), eventToday.get(index).getDescription(), Toast.LENGTH_LONG).show();
            EventRecycleView adapter = new EventRecycleView(eventToday, getActivity().getApplication(), listiner);
            recycleView.setAdapter(adapter);
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_update", true)) {


                TextView update = new TextView(getActivity());
                update.setLayoutParams(lp);
                update.setGravity(Gravity.CENTER);
                update.setTextSize(18);

                update.setText("last update : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fileCal.lastModified()));
//                update.setText(DataSaver.getSavedLastUpdate(getContext()));
                layout.addView(update);
            }
        } else {
            TextView update = new TextView(getActivity());
            update.setLayoutParams(lp);
            update.setGravity(Gravity.CENTER);
            update.setTextSize(18);

            update.setText("no update...");


            layout.addView(update);
        }

        return view;
    }

}