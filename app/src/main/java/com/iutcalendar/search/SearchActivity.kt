package com.iutcalendar.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.iutcalendar.mainpage.PageEventActivity
import com.iutcalendar.menu.MenuItemClickActivities
import com.univlyon1.tools.agenda.R

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.Search)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PageEventActivity::class.java)
        startActivity(intent)
    }
}