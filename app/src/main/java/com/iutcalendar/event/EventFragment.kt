package com.iutcalendar.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.iutcalendar.calendrier.Calendrier
import com.iutcalendar.calendrier.CurrentDate
import com.iutcalendar.data.CachedData
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R
import java.text.SimpleDateFormat

class EventFragment : Fragment {
    var calendrier: Calendrier
        get() = CachedData.calendrier
        set(value) {
            CachedData.calendrier = value
        }
    private var date: CurrentDate
    private val fileUpdate
        get() = FileGlobal.getFileCalendar(requireContext())
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var updateTextView: TextView? = null
    private lateinit var recycleView: RecyclerView

    constructor() {
        date = CurrentDate()
        // Required empty public constructor
    }

    constructor(date: CurrentDate) {
        this.date = date
        Log.d("Event", "creation fragment event for $date")
    }

    private fun updateUI() {
        updateRecycleView()
        updateLabel()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateRecycleView() {
        if (activity != null) {

            val eventToday = calendrier.clone().getEventsOfDay(date)
            val onClickEvent = { index: Int ->
                if (context != null && activity != null) {
                    val ev = eventToday[index]
                    val dialog = DialogPopupEvent(
                        requireContext(),
                        ev,
                        activity
                    ) { shouldRestart ->
                        if (shouldRestart && activity is PageEventActivity)
                            (activity as PageEventActivity).initPageViewEvent()
                        else
                            recycleView.adapter?.notifyItemChanged(index)
                    }
                    dialog.show()
                }
            }
            recycleView.adapter = EventAdapter(eventToday, requireActivity().application, onClickEvent)
        }
    }

    private fun updateLabel() {
        updateTextView?.let { updateText ->
            if (PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getBoolean("show_update", true)
            ) {
                updateText.visibility = View.VISIBLE
                if (fileUpdate.exists()) {
                    CurrentDate().apply {   //affichage la dernière maj
                        timeInMillis = fileUpdate.lastModified()
                        runForDate({
                            updateText.text = resources.getString(
                                R.string.last_update,
                                SimpleDateFormat(
                                    "HH:mm",
                                    SettingsApp.locale
                                ).format(fileUpdate.lastModified())
                            )
                        }, {
                            updateText.text = resources.getString(
                                R.string.last_update,
                                "error" //impossible que last_update soit demain
                            )
                        }) {
                            updateText.text = resources.getString(
                                R.string.last_update,
                                SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm",
                                    SettingsApp.locale
                                ).format(fileUpdate.lastModified())
                            )
                        }
                    }

                } else {
                    Log.d("File", "calendar file doesn't existe")
                    updateText.text = getString(R.string.no_last_update)
                }
            } else {
                updateText.visibility = View.GONE
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_event, container, false).also { view ->
            recycleView = view.findViewById(R.id.recycleView)
            updateTextView = view.findViewById(R.id.updateText)
            if (activity != null && activity is PageEventActivity) {
                val parentActivity = activity as PageEventActivity?
                swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
                swipeRefreshLayout.setOnRefreshListener {
                    parentActivity?.update {
                        swipeRefreshLayout.isRefreshing = false
                        updateLabel()
                    }
                }
                recycleView.layoutManager = LinearLayoutManager(activity)
            }
            updateUI()
        }
    }
}