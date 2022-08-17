package com.iutcalendar.event.changement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.univlyon1.tools.agenda.R
import java.util.*

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Test", "coucou")
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val textView = view.findViewById<TextView>(R.id.text)
        recyclerView.layoutManager = LinearLayoutManager(context)
        Log.d("History", EventChangementManager.getInstance(context).changementList.toString())
        var eventChangementList: List<EventChangement?> = LinkedList(EventChangementManager.getInstance(context).changementList)
        if (arguments != null && arguments!!.getInt(NOMBRE_EVENT, 0) > 0) {
            eventChangementList = eventChangementList.subList(0, arguments!!.getInt(NOMBRE_EVENT, 0))
        }
        if (eventChangementList.isEmpty()) {
            textView.setText(R.string.NoEDTChange)
        } else {

            recyclerView.adapter = EventChangementRecycleView(context, eventChangementList.reversed())//reversed : pour le plus récent en haut, car enregistrer dans l'autre sense
        }
        return view
    }

    companion object {
        const val NOMBRE_EVENT = "nombre_event"
    }
}