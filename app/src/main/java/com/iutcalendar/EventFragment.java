package com.iutcalendar;

import android.app.Activity;
import android.app.Application;
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
    private SwipeRefreshLayout swipeRefreshLayout;

    private EventRecycleView adapter;
    private RecyclerView recycleView;
    private int position;


    private Activity activity;
    @Override
    public void onResume() {
        super.onResume();
    }

    public EventFragment() {
        // Required empty public constructor
    }

    public EventFragment(Calendrier calendrier, CurrentDate date, int position, File fileUpdate) {
        this.calendrier = calendrier;
        this.date = date;
        this.fileUpdate = fileUpdate;
        this.position = position;
    }

    public void updateRecycleViewOnThread() {
        Log.d("Event", String.valueOf(getActivity()==null));
        if(activity != null){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRecycleView();
            }
        });}


    }

    public void updateRecycleView() {
        List<EventCalendrier> eventToday = calendrier.getEventsOfDay(new CurrentDate(date).getDateOfDayOfWeek(position));

        ClickListener listener = index -> {//Event on click Event
            EventCalendrier ev = eventToday.get(index);
            DialogPopupEvent dialog = new DialogPopupEvent(getContext(), ev, getActivity(), () -> {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecycleView();
                    }
                });
            });
            dialog.show();
        };

        EventRecycleView adapter = new EventRecycleView(eventToday, getActivity().getApplication(), listener);
        recycleView.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recycleView = view.findViewById(R.id.recycleView);
        activity = getActivity();


        if (getActivity() != null && getActivity() instanceof PageEventActivity) {
            PageEventActivity mainActivity = ((PageEventActivity) getActivity());


            TextView update = view.findViewById(R.id.updateText);

            swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Thread(() -> {
                        FileGlobal.updateAndGetChange(getContext(), calendrier, ((context, intent) -> startActivity(intent)));


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (PageEventActivity.isActive()) {
                                    updateRecycleView();
                                    mainActivity.updateScreen();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    }).start();
                }
            });


            if (fileUpdate.exists() && getContext() != null) {


                updateRecycleView();
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