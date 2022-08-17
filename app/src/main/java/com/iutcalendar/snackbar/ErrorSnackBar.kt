package com.iutcalendar.snackbar

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.univlyon1.tools.agenda.R

object ErrorSnackBar {
    fun showError(view: View, text: CharSequence?) {
        val context = view.context
        val snackbar = Snackbar.make(view, text!!, BaseTransientBottomBar.LENGTH_LONG)
        snackbar.setBackgroundTint(context.getColor(R.color.redOnePlus))
        snackbar.setTextColor(context.getColor(R.color.white))
        snackbar.setActionTextColor(context.getColor(R.color.white))
        snackbar.setAction("╳") { snackbar.dismiss() }
        snackbar.show()
    }
}