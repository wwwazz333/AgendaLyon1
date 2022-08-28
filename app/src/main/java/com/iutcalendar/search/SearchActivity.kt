package com.iutcalendar.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.iutcalendar.dialog.DialogMessage
import com.iutcalendar.menu.MenuItemClickActivities
import com.univlyon1.tools.agenda.R

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.Search_room_title)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.aideBtn) {
            DialogMessage.showAide(
                this@SearchActivity, getString(R.string.Search_room_title), getString(R.string.help_search_room)
            )
            return true
        }
        return MenuItemClickActivities(this).onMenuItemClick(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_action_help, menu)
        return super.onCreateOptionsMenu(menu)
    }
}