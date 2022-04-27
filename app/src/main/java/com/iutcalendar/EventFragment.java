package com.iutcalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventFragment extends Fragment {

    public EventFragment() {
        // Required empty public constructor
    }

    public String readFile(File file) {
        String str = null;
        try {
            str = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            Log.d("Error", e.getMessage());
            str = "";
        }

        return str;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        LinearLayout layout = view.findViewById(R.id.mainLayout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        File fileCal = PathGlobal.getInstance().getFileDownload();


        if (fileCal.exists()) {
            String str = readFile(fileCal);
            Calendrier cal = new Calendrier(str);


            CurrentDate date = ((MainActivity) getActivity()).getCurrDate();

            List<EventCalendrier> eventToday = cal.getEventsOfDay(date);


            for (EventCalendrier e : eventToday) {
                layout.addView(createEvent(e));
            }

            TextView t = new TextView(getActivity());
            t.setLayoutParams(lp);
            t.setGravity(Gravity.CENTER);
            t.setTextSize(18);

            t.setText(new StringBuilder().append("last update : ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fileCal.lastModified())).toString());


            layout.addView(t);
        } else {
            TextView t = new TextView(getActivity());
            t.setLayoutParams(lp);
            t.setGravity(Gravity.CENTER);
            t.setTextSize(18);

            t.setText("no update...");


            layout.addView(t);
        }

        return view;
    }

    public View createEvent(EventCalendrier e) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout horaire = new LinearLayout(getActivity());
        horaire.setOrientation(LinearLayout.VERTICAL);

        TextView debut = new TextView(getActivity());
        debut.setText(new StringBuilder().append(e.getDate().getHour()).append("h").toString());
        debut.setTextSize(18);
        TextView fin = new TextView(getActivity());
        fin.setText(new StringBuilder().append(e.getDate().addTime(e.getDuree()).getHour()).append("h").toString());
        fin.setTextSize(18);

        horaire.addView(debut);
        horaire.addView(fin);
        horaire.setLayoutParams(lp);
        layout.addView(horaire);


        TextView summary = new TextView(getActivity());

        summary.setLayoutParams(lp);
        summary.setGravity(Gravity.CENTER);

        summary.setTextSize(18);

        summary.setText(e.getSummary());

        layout.addView(summary);

        TextView salle = new TextView(getActivity());
        salle.setLayoutParams(lp);
        salle.setGravity(Gravity.RIGHT);
        salle.setTextSize(18);

        salle.setText(e.getSalle());

        layout.addView(salle);
        layout.setPadding(20, 0, 20, 0);


        return layout;
    }
}