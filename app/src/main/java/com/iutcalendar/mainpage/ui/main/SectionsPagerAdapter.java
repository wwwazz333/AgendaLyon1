package com.iutcalendar.mainpage.ui.main;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.iutcalendar.EventFragment;
import com.iutcalendar.calendrier.Calendrier;
import com.iutcalendar.calendrier.CurrentDate;
import com.iutcalendar.data.DataGlobal;
import com.iutcalendar.data.FileGlobal;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final Calendrier calendrier;
    private final CurrentDate dateCalendrier;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Calendrier calendrier, CurrentDate dateCalendrier) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.calendrier = calendrier;
        this.dateCalendrier = dateCalendrier;
        Log.d("Page", "end createion");
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Log.d("Position", "curr : " + dateCalendrier.toString());
        CurrentDate dateToLaunche = new CurrentDate(dateCalendrier).getDateOfDayOfWeek(position);
        Log.d("Position", dateToLaunche.toString() + " at " + position);


        return new EventFragment(calendrier, dateCalendrier, position, FileGlobal.getFileDownload(mContext));
    }



    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(DataGlobal.DAYS_OF_WEEK[position]);
    }

    @Override
    public int getCount() {
        return DataGlobal.DAYS_OF_WEEK.length;
    }
}