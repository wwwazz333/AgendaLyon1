package com.iutcalendar.event.changement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.menu.MenuItemClickActivities;
import com.iutcalendar.settings.SettingsApp;
import com.univlyon1.tools.agenda.R;

import java.util.Collections;
import java.util.List;

public class EventChangementHistoryActivity extends AppCompatActivity {

    //TODO en faire un fragment pour le mettre dans la notif d'ouverture quand il y à des changements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsApp.adapteTheme(this);
        SettingsApp.setLocale(getResources(), DataGlobal.getLanguage(getApplicationContext()));
        setContentView(R.layout.activity_event_changement_history);

//        RecyclerView recyclerView = findViewById(R.id.recycleView);
//        TextView textView = findViewById(R.id.text);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        Log.d("History", EventChangmentManager.getInstance(getApplicationContext()).getChangmentList().toString());
//        List<EventChangment> eventChangementList = EventChangmentManager.getInstance(getApplicationContext()).getChangmentList();
//        if (eventChangementList.isEmpty()) {
//            textView.setText(R.string.NoEDTChange);
//        } else {
//            Collections.reverse(eventChangementList);
//            recyclerView.setAdapter(new EventChangmentRecycleView(this, eventChangementList));
//        }


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