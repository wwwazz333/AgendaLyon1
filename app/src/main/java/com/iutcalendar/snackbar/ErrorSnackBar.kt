package com.iutcalendar.snackbar

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.univlyon1.tools.agenda.R

object ErrorSnackBar {
    fun showError(view: View, text: CharSequence?) {
        val context = view.context
        val snackBar = Snackbar.make(view, text!!, BaseTransientBottomBar.LENGTH_LONG)
        snackBar.setBackgroundTint(context.getColor(R.color.redOnePlus))
        snackBar.setTextColor(context.getColor(R.color.white))
        snackBar.setActionTextColor(context.getColor(R.color.white))
        snackBar.setAction("╳") { snackBar.dismiss() }
        snackBar.show()
    }
}