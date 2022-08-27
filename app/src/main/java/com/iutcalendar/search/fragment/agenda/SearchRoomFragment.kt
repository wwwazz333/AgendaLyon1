package com.iutcalendar.search.fragment.agenda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.calendrier.DateCalendrier
import com.iutcalendar.calendrier.SearchCalendrier
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.FragmentSearchRoomBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SearchRoomFragment : Fragment() {
    private lateinit var binding: FragmentSearchRoomBinding
    //TODO check if url rooms

    private var dateSelected = DateCalendrier()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search_room, container, false).also { view ->
            binding = FragmentSearchRoomBinding.bind(view)


            lifecycleScope.launch(Dispatchers.IO) {
                SearchCalendrier.loadCalendrierRoom(requireContext())
                withContext(Dispatchers.Main) {
                    binding.blockClickView.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }



            resetValues()
            binding.resetBtn.setOnClickListener { resetValues() }
            binding.searchBtn.setOnClickListener {
                val bundle = bundleOf("timeInMillis" to dateSelected.timeInMillis)
                Navigation.findNavController(view).navigate(R.id.goToDisplay, bundle)
            }
        }
    }

    private fun resetValues() {
        binding.editTime.apply {
            setIs24HourView(SettingsApp.locale == SettingsApp.localFrance)

            CurrentDate().let {
                hour = it.hour
                minute = it.minute
            }
            setOnTimeChangedListener { _, hourOfDay, minuteTime ->
                dateSelected.hour = hourOfDay
                dateSelected.minute = minuteTime
            }
        }

        binding.calendarView.date = System.currentTimeMillis()
        dateSelected = CurrentDate()
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            dateSelected.from(GregorianCalendar(year, month, dayOfMonth, binding.editTime.hour, binding.editTime.minute, 0))
        }
    }
}