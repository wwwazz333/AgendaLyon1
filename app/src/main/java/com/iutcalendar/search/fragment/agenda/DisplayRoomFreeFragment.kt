package com.iutcalendar.search.fragment.agenda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iutcalendar.calendrier.DateCalendrier
import com.univlyon1.tools.agenda.R

class DisplayRoomFreeFragment : Fragment() {
    // TODO: fonctionalité

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.getLong("timeInMillis")?.let {
            Log.d("SearchRoom", "time is $it -> ${DateCalendrier().apply { timeInMillis = it }}")
        }

        return inflater.inflate(R.layout.fragment_display_room_free, container, false)
    }
}