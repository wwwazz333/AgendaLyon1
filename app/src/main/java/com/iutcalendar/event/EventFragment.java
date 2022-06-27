package com.iutcalendar.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.settings.SettingsApp;
import com.univlyon1.tools.agenda.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventFragment extends Fragment {


    private static int number = 0;
    private Calendrier calendrier;
    private CurrentDate date;
    private File fileUpdate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView update;
    private RecyclerView recycleView;
    private int myNumber;


    public EventFragment() {
        // Required empty public constructor
    }

    public EventFragment(Calendrier calendrier, CurrentDate date, File fileUpdate) {
        this.calendrier = calendrier;
        this.date = date;
        this.fileUpdate = fileUpdate;

        Log.d("Event", "creation fragment event for " + date);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateUI() {

        updateRecycleView();
        updateLabel();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateRecycleView() {
        if (getActivity() != null && calendrier != null) {
            if (date == null) Log.e("Event", "date is null");
            else Log.d("Event", "date update recycle view " + date + " for event " + myNumber);
            List<EventCalendrier> eventToday = calendrier.clone().getEventsOfDay(date);

            ClickListener listener = index -> {//Event on click Event
                if (getContext() != null && getActivity() != null) {
                    EventCalendrier ev = eventToday.get(index);
                    DialogPopupEvent dialog = new DialogPopupEvent(getContext(), ev, getActivity(), () -> getActivity().runOnUiThread(this::updateRecycleView));
                    dialog.show();
                }
            };

            EventRecycleView adapter = new EventRecycleView(eventToday, getActivity().getApplication(), listener);
            recycleView.setAdapter(adapter);
        }
    }

    public void updateLabel() {
        if (update == null) return;

        if (getContext() != null && PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_update", true)) {
            if (calendrier != null && getContext() != null && fileUpdate != null && FileGlobal.getFileDownload(getContext()).exists()) {
                //affichage la dernière maj
                CurrentDate dateLastEdit = new CurrentDate();
                dateLastEdit.setTimeInMillis(fileUpdate.lastModified());
                dateLastEdit.runForDate(() -> update.setText(getResources().getString(R.string.last_update,
                        new SimpleDateFormat("HH:mm", SettingsApp.getLocale()).format(fileUpdate.lastModified())
                )), () -> update.setText(getResources().getString(R.string.last_update,
                        "error" //impossible que last_update soit demain
                )), () -> update.setText(getResources().getString(R.string.last_update,
                        new SimpleDateFormat("dd/MM/yyyy HH:mm", SettingsApp.getLocale()).format(fileUpdate.lastModified())
                )));
            } else {
                update.setText(R.string.no_last_update);
            }
        } else {
            update.setText("");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myNumber = number++;
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recycleView = view.findViewById(R.id.recycleView);
        update = view.findViewById(R.id.updateText);


        if (getActivity() != null && getActivity() instanceof PageEventActivity) {
            PageEventActivity parentActivity = (PageEventActivity) getActivity();
            swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
            swipeRefreshLayout.setOnRefreshListener(() -> parentActivity.update(() -> swipeRefreshLayout.setRefreshing(false)));
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        updateUI();

        return view;
    }
}