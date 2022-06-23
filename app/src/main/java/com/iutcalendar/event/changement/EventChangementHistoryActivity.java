package com.iutcalendar.event.changement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.calendar.iutcalendar.R;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.menu.MenuItemClickActivities;
import com.iutcalendar.settings.SettingsApp;

import java.util.Collections;
import java.util.List;

public class EventChangementHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));
        setContentView(R.layout.activity_event_changement_history);

        RecyclerView recyclerView = findViewById(R.id.recycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("History", EventChangmentManager.getInstance(getApplicationContext()).getChangmentList().toString());
        List<EventChangment> eventChangmentList = EventChangmentManager.getInstance(getApplicationContext()).getChangmentList();
        Collections.reverse(eventChangmentList);
        recyclerView.setAdapter(new EventChangmentRecycleView(this, eventChangmentList));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.History));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return new MenuItemClickActivities(this).onMenuItemClick(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PageEventActivity.class);
        startActivity(intent);
    }
}