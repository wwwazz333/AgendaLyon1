package com.iutcalendar.mainpage.ui.main;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.iutcalendar.event.EventFragment;
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

    private final int countDay;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Calendrier calendrier) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.calendrier = calendrier;
        Log.d("Page", "end création SectionPagerAdapter");

        if (calendrier == null || calendrier.getFirstDay() == null) {
            this.countDay = 1;
        } else
            this.countDay = calendrier.getFirstDay().getNbrDayTo(calendrier.getLastDay()) + 1;
    }

    @Override
    public Fragment getItem(int position) {
        CurrentDate dateToLaunche;
        if (calendrier != null && calendrier.getFirstDay() != null) {
            dateToLaunche = new CurrentDate(calendrier.getFirstDay()).addDay(position);
        } else {
            dateToLaunche = new CurrentDate();
        }
        EventFragment eventFragment = new EventFragment(calendrier, dateToLaunche, FileGlobal.getFileDownload(mContext));
        return eventFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(DataGlobal.DAYS_OF_WEEK[position]);
    }

    @Override
    public int getCount() {
        return countDay;
    }
}