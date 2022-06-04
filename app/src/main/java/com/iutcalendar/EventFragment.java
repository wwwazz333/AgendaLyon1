package com.iutcalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.event.ClickListener;
import com.iutcalendar.event.DialogPopupEvent;
import com.iutcalendar.event.EventRecycleView;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.settings.SettingsApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventFragment extends Fragment {


    private Calendrier calendrier;
    private CurrentDate date;
    private File fileUpdate;

    public EventFragment() {
        // Required empty public constructor
    }

    public EventFragment(Calendrier calendrier, CurrentDate date, File fileUpdate) {
        this.calendrier = calendrier;
        this.date = date;
        this.fileUpdate = fileUpdate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        if (getActivity() != null) {


//            File fileCal = FileGlobal.getFileDownload(getContext());

            TextView update = view.findViewById(R.id.updateText);



            if (fileUpdate.exists() && getActivity() != null && getActivity() instanceof PageEventActivity && getContext() != null) {
                PageEventActivity mainActivity = ((PageEventActivity) getActivity());

//                Calendrier cal = mainActivity.getCalendrier();

//                CurrentDate date = mainActivity.getCurrDate();

                List<EventCalendrier> eventToday = calendrier.getEventsOfDay(date);

                for (EventCalendrier e : eventToday) {
                    Log.d("Date", e.toString());
                }


                ClickListener listener = index -> {//Event on click Event
                    EventCalendrier ev = eventToday.get(index);
                    DialogPopupEvent dialog = new DialogPopupEvent(getContext(), ev, getActivity(), mainActivity::updateScreen);
                    dialog.show();
                };

                EventRecycleView adapter = new EventRecycleView(eventToday, getActivity().getApplication(), listener);
                recycleView.setAdapter(adapter);
                recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


                if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_update", true)) {
                    //affichage dernière maj
                    CurrentDate dateLastEdit = new CurrentDate();
                    dateLastEdit.setTimeInMillis(fileUpdate.lastModified());
                    dateLastEdit.runForDate(() -> update.setText(getResources().getString(R.string.last_update,
                            new SimpleDateFormat("HH:mm", SettingsApp.getLocale()).format(fileUpdate.lastModified())
                    )), () -> update.setText(getResources().getString(R.string.last_update,
                            "error" //impossible que last_update soit demain
                    )), () -> update.setText(getResources().getString(R.string.last_update,
                            new SimpleDateFormat("dd/MM/yyyy HH:mm", SettingsApp.getLocale()).format(fileUpdate.lastModified())
                    )));

                }
            } else {
                update.setText(R.string.no_last_update);
            }
        }

        return view;
    }

}