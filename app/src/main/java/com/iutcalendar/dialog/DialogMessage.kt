package com.iutcalendar.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.univlyon1.tools.agenda.R

object DialogMessage {
    fun showWarning(context: Context?, title: String?, msg: String?) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_error)
        alertDialog.setPositiveButton(
            context!!.getString(R.string.ok)
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog.show()
    }

    fun showInfo(context: Context?, title: String?, msg: String?, onOkListener: () -> Unit) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_info)
        alertDialog.setPositiveButton(
            context!!.getString(R.string.ok)
        ) { dialog: DialogInterface, which: Int ->
            onOkListener()
            dialog.dismiss()
        }
        alertDialog.show()
    }

    fun showAide(context: Context?, title: String?, msg: String?) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_help)
        alertDialog.setPositiveButton(
            context!!.getString(R.string.ok)
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog.show()
    }
}