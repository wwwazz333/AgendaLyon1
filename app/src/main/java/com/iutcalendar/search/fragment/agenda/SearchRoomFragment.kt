package com.iutcalendar.search.fragment.agenda

import android.os.Bundle
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
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.filedownload.FileDownload
import com.iutcalendar.fragment.NavigatorManager
import com.iutcalendar.settings.SettingsApp
import com.iutcalendar.settings.URLSetterFragment
import com.univlyon1.tools.agenda.R
import com.univlyon1.tools.agenda.databinding.FragmentSearchRoomBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SearchRoomFragment : Fragment() {
    private lateinit var binding: FragmentSearchRoomBinding

    private var dateSelected = DateCalendrier()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_room, container, false).also { view ->
            binding = FragmentSearchRoomBinding.bind(view)

            resetValues()
            binding.resetBtn.setOnClickListener { resetValues() }
            binding.searchBtn.setOnClickListener {
                val bundle = bundleOf("timeInMillis" to dateSelected.timeInMillis)
                Navigation.findNavController(view).navigate(R.id.goToDisplay, bundle)
            }


            DataGlobal.getSavedRoomsPath(requireContext()).let { path ->
                lifecycleScope.launch(Dispatchers.IO) {
                    if (path.isNullOrBlank() || !FileDownload.isValideURL(path)) {
                        withContext(Dispatchers.Main) {
                            DialogMessage.showWarning(
                                context, getString(R.string.url_rooms_invalide), getString(R.string.url_rooms_invalide_msg)
                            ) {
                                NavigatorManager.startFragmentFromFragment(this@SearchRoomFragment, URLSetterFragment())
                            }
                        }

                    } else {
                        SearchCalendrier.loadCalendrierRoom(requireContext())
                        withContext(Dispatchers.Main) {
                            loading(false)
                        }
                    }
                }
            }
        }
    }

    private fun loading(state: Boolean) {
        if (state) {
            binding.blockClickView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.blockClickView.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
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