package com.iutcalendar.search.fragment.agenda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.calendrier.SearchCalendrier
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.FragmentDisplayRoomFreeBinding


class DisplayRoomFreeFragment : Fragment() {
    private lateinit var binding: FragmentDisplayRoomFreeBinding

    private lateinit var dateRecherche: DateCalendrier
    
    //TODO msg aide pr prevenire que peux pas détecter les salles jamais occupé

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_room_free, container, false)
        binding = FragmentDisplayRoomFreeBinding.bind(view)

        arguments?.getLong("timeInMillis")?.let {
            dateRecherche = DateCalendrier().apply { timeInMillis = it }
            Log.d("SearchRoom", "time is $it -> $dateRecherche")

            binding.listView.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                SearchCalendrier.freeRoomAt(dateRecherche)
            )
        }
        return view
    }
}