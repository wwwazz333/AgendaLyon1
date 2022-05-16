package com.iutcalendar;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.calendrier.EventCalendrier;
import com.iutcalendar.data.FileGlobal;
import com.iutcalendar.event.ClickListiner;
import com.iutcalendar.event.DialogPopupEvent;
import com.iutcalendar.event.EventRecycleView;
import com.iutcalendar.language.SettingsApp;

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

        TextView update = new TextView(getActivity());
        update.setLayoutParams(lp);
        update.setGravity(Gravity.CENTER);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
        @ColorInt int color = typedValue.data;
        update.setTextColor(color);
        update.setTextSize(18);


        if (fileCal.exists() && getActivity() != null && getActivity() instanceof MainActivity && getContext() != null) {
            String str = FileGlobal.readFile(fileCal);
            Calendrier cal = new Calendrier(str);


            CurrentDate date = ((MainActivity) getActivity()).getCurrDate();

            List<EventCalendrier> eventToday = cal.getEventsOfDay(date);


            RecyclerView recycleView = view.findViewById(R.id.recycleView);

            ClickListiner listiner = index -> {//Event on click Event
                EventCalendrier ev = eventToday.get(index);
                DialogPopupEvent dialog = new DialogPopupEvent(getContext(), ev, getActivity());
                dialog.show();
            };
            EventRecycleView adapter = new EventRecycleView(eventToday, getActivity().getApplication(), listiner);
            recycleView.setAdapter(adapter);
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_update", true)) {
                //affichage dernière maj
                CurrentDate dateLastEdit = new CurrentDate();
                dateLastEdit.setTimeInMillis(fileCal.lastModified());
                dateLastEdit.runForDate(() -> update.setText(getResources().getString(R.string.last_update,
                        new SimpleDateFormat("HH:mm", SettingsApp.getLocale(getResources())).format(fileCal.lastModified())
                )), () -> update.setText(getResources().getString(R.string.last_update,
                        "error" //impossible que last_update soit demain
                )), () -> update.setText(getResources().getString(R.string.last_update,
                        new SimpleDateFormat("dd/MM/yyyy HH:mm", SettingsApp.getLocale(getResources())).format(fileCal.lastModified())
                )));

                layout.addView(update);
            }
        } else {
            update.setText(R.string.no_last_update);

            layout.addView(update);
        }

        return view;
    }

}