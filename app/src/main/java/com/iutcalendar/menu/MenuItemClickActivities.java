package com.iutcalendar.menu;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.calendar.iutcalendar.R;
import com.iutcalendar.mainpage.PageEventActivity;
import com.iutcalendar.settings.SettingsActivity;

public class MenuItemClickActivities implements MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {

    private final Context context;

    public MenuItemClickActivities(Context context) {
        this.context = context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.calendrier:
                intent = new Intent(context, PageEventActivity.class);
                break;
            case R.id.settings:
                intent = new Intent(context, SettingsActivity.class);
                break;
            case R.id.history:
                intent = new Intent(context, SettingsActivity.class);
                break;
            default:
                return false;
        }
        context.startActivity(intent);
        return true;
    }
}
