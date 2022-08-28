package com.iutcalendar.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.PopupMenu
import com.iutcalendar.event.changement.EventChangementHistoryActivity
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.search.SearchActivity
import com.iutcalendar.settings.SettingsActivity
import com.univlyon1.tools.agenda.R

class MenuItemClickActivities(private val context: Context) : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
    private val activity: Activity = context as Activity

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val activityToLaunch: Class<*> = when (item.itemId) {
            R.id.calendrier -> PageEventActivity::class.java
            R.id.settings -> SettingsActivity::class.java
            R.id.history -> EventChangementHistoryActivity::class.java
            R.id.search -> SearchActivity::class.java
            else -> return false
        }

        context.startActivity(Intent(context, activityToLaunch))
        if (activity !is PageEventActivity || activityToLaunch == PageEventActivity::class.java) {
            activity.finish()
        }

        return true
    }
}