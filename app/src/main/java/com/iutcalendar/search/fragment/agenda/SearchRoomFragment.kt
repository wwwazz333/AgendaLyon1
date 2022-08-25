package com.iutcalendar.search.fragment.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.iutcalendar.calendrier.CurrentDate
import com.univlyon1.tools.agenda.R

class SearchRoomFragment : Fragment() {
    private var searchBtn: Button? = null
    private var resetBtn: Button? = null
    private var timeChooser: EditText? = null
    private var calendarView: CalendarView? = null
    private fun initVariables(view: View) {
        searchBtn = view.findViewById(R.id.searchBtn)
        resetBtn = view.findViewById(R.id.resetBtn)
        timeChooser = view.findViewById(R.id.editTextTime)
        calendarView = view.findViewById(R.id.calendarView)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_room, container, false)
        initVariables(view)
        resetValues()
        resetBtn?.setOnClickListener { resetValues() }
        searchBtn?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.goToDisplay)
        }
        return view
    }

    private fun resetValues() {
        timeChooser?.setText(CurrentDate().timeToString())
        calendarView?.date = CurrentDate().timeInMillis
    }
}