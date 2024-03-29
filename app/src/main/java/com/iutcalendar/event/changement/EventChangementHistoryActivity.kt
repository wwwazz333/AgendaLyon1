package com.iutcalendar.event.changement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.iutcalendar.data.DataGlobal
import com.iutcalendar.menu.MenuItemClickActivities
import com.iutcalendar.settings.SettingsApp
import com.univlyon1.tools.agenda.R

class EventChangementHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_changement_history)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.History)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return MenuItemClickActivities(this).onMenuItemClick(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_activities, menu)
        return super.onCreateOptionsMenu(menu)
    }
}