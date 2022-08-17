package com.iutcalendar.event.changement

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.univlyon1.tools.agenda.R

class ChangeDialog(context: Context, nombreChangements: Int) : Dialog(context) {
    private val nombreChangements: Int

    init {
        setContentView(R.layout.changement_dialog)
        this.nombreChangements = nombreChangements
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val fragmentContainerView = findViewById<FragmentContainerView>(R.id.fragmentHistory)
        val args = Bundle()
        args.putInt(HistoryFragment.NOMBRE_EVENT, nombreChangements)
        fragmentContainerView.getFragment<Fragment>().arguments = args
    }
}