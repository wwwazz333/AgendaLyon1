package com.iutcalendar.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.PopupMenu
import com.iutcalendar.event.changement.EventChangementHistoryActivity
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.settings.SettingsActivity
import com.univlyon1.tools.agenda.R

class MenuItemClickActivities(private val context: Context) : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
    private val activity: Activity = context as Activity

    //TODO optimize transition

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val intent: Intent = when (item.itemId) {
            R.id.calendrier -> Intent(context, PageEventActivity::class.java)
            R.id.settings -> Intent(context, SettingsActivity::class.java)
            R.id.history -> Intent(context, EventChangementHistoryActivity::class.java)
            else -> return false
        }
        activity.finish()
        context.startActivity(intent)
        return true
    }
}