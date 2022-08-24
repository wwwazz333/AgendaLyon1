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
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.data.FileGlobal
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R
import java.io.File
import java.text.SimpleDateFormat

class EventFragment : Fragment {
    private var calendrier: Calendrier? = null
    private var date: CurrentDate? = null
    private var fileUpdate: File? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var update: TextView? = null
    private lateinit var recycleView: RecyclerView
    private var myNumber = 0

    constructor() {
        // Required empty public constructor
    }

    constructor(calendrier: Calendrier?, date: CurrentDate?, fileUpdate: File?) {
        this.calendrier = calendrier
        this.date = date
        this.fileUpdate = fileUpdate
        Log.d("Event", "creation fragment event for $date")
    }

    private fun updateUI() {
        updateRecycleView()
        updateLabel()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateRecycleView() {
        if (activity != null && calendrier != null) {
            if (date == null) Log.e("Event", "date is null") else Log.d(
                "Event",
                "date update recycle view $date for event $myNumber"
            )
            val eventToday = calendrier!!.clone().getEventsOfDay(date)
            val listener = { index: Int ->  //Event on click Event
                if (context != null && activity != null) {
                    val ev = eventToday[index]
                    val dialog = DialogPopupEvent(
                        requireContext(),
                        ev,
                        activity
                    ) {
                        requireActivity().runOnUiThread {
                            //TODO notify data change
                            updateRecycleView()
                        }
                    }
                    dialog.show()
                }
            }
            val adapter = EventAdapter(eventToday, requireActivity().application, listener)
            recycleView.adapter = adapter
        }
    }

    private fun updateLabel() {
        if (update == null) return
        if (context != null && PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("show_update", true)
        ) {
            if (calendrier != null && context != null && fileUpdate != null && FileGlobal.getFileDownload(
                    context
                ).exists()
            ) {
                //affichage la dernière maj
                val dateLastEdit = CurrentDate()
                dateLastEdit.timeInMillis = fileUpdate!!.lastModified()
                dateLastEdit.runForDate({
                    update!!.text = resources.getString(
                        R.string.last_update,
                        SimpleDateFormat(
                            "HH:mm",
                            SettingsApp.locale
                        ).format(fileUpdate!!.lastModified())
                    )
                }, {
                    update!!.text = resources.getString(
                        R.string.last_update,
                        "error" //impossible que last_update soit demain
                    )
                }) {
                    update!!.text = resources.getString(
                        R.string.last_update,
                        SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            SettingsApp.locale
                        ).format(fileUpdate!!.lastModified())
                    )
                }
            } else {
                update!!.text = getString(R.string.no_last_update)
            }
        } else {
            update!!.text = ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SettingsApp.setLocale(resources, DataGlobal.getLanguage(requireContext()))

        myNumber = number++
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        recycleView = view.findViewById(R.id.recycleView)
        update = view.findViewById(R.id.updateText)
        if (activity != null && activity is PageEventActivity) {
            val parentActivity = activity as PageEventActivity?
            swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
            swipeRefreshLayout.setOnRefreshListener {
                parentActivity!!.update {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            recycleView.layoutManager = LinearLayoutManager(activity)
        }
        updateUI()
        return view
    }

    companion object {
        private var number = 0
    }
}