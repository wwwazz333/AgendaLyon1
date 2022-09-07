package com.iutcalendar.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.univlyon1.tools.agenda.R

object DialogMessage {
    fun showWarning(context: Context?, title: String?, msg: String?, onPositiveButton: () -> Unit = { }) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_error)
        alertDialog.setPositiveButton(
            context!!.getString(R.string.ok)
        ) { dialog: DialogInterface, _: Int -> onPositiveButton(); dialog.dismiss() }
        alertDialog.show()
    }

    fun showInfo(context: Context?, title: String?, msg: String?, onOkListener: () -> Unit = {}) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_info)
        alertDialog.setPositiveButton(
            context!!.getString(R.string.ok)
        ) { dialog: DialogInterface, _: Int ->
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
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialog.show()
    }

    fun showChooseDarkOrLightMode(ctx: Context, msg: String, darkMode: () -> Unit, lightMode: () -> Unit) {
        val alertDialog = AlertDialog.Builder(ctx)
        alertDialog.setTitle(ctx.getString(R.string.QuelTheme))
        alertDialog.setMessage(msg)
        alertDialog.setIcon(R.drawable.ic_help)
        alertDialog.setPositiveButton(ctx.getString(R.string.DarkTheme))
        { dialog: DialogInterface, _: Int ->
            darkMode()
            dialog.dismiss()
        }
        alertDialog.setNeutralButton(ctx.getString(R.string.LightTheme)) { dialog: DialogInterface, _: Int ->
            lightMode()
            dialog.dismiss()

        }
        alertDialog.show()
    }
}