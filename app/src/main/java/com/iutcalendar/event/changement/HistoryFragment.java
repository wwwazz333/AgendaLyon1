package com.iutcalendar.event.changement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.univlyon1.tools.agenda.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class HistoryFragment extends Fragment {

    public static final String NOMBRE_EVENT = "nombre_event";

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Test", "coucou");
        view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);

        TextView textView = view.findViewById(R.id.text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("History", EventChangmentManager.getInstance(getContext()).getChangmentList().toString());

        List<EventChangment> eventChangementList = new LinkedList<>(EventChangmentManager.getInstance(getContext()).getChangmentList());


        int nombreEventDisplay;
        if (getArguments() != null && (nombreEventDisplay = getArguments().getInt(NOMBRE_EVENT, 0)) > 0) {
            eventChangementList = eventChangementList.subList(0, nombreEventDisplay);
        }

        if (eventChangementList.isEmpty()) {
            textView.setText(R.string.NoEDTChange);
        } else {
            Collections.reverse(eventChangementList);//pour le plus récent en haut, car enregistrer dans l'autre sense
            recyclerView.setAdapter(new EventChangmentRecycleView(getContext(), eventChangementList));
        }
        return view;
    }
}