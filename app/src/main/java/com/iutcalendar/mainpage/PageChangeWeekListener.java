package com.iutcalendar.mainpage;

import android.util.Log;
import androidx.viewpager.widget.ViewPager;
import com.iutcalendar.data.DataGlobal;

public class PageChangeWeekListener implements ViewPager.OnPageChangeListener {

    private final PageEventActivity parent;
    private boolean canSwitch, superCan;

    public PageChangeWeekListener(PageEventActivity parent) {
        this.parent = parent;
        this.canSwitch = false;
        this.superCan = false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (superCan && (position == 0 || position == DataGlobal.DAYS_OF_WEEK.length - 1) && positionOffset == 0f) {
            Log.d("Page", "week : " + position);
            canSwitch = false;
            superCan = false;
            if(position == 0) {
                parent.switchToPrevWeekAlt();
//                parent.setCurrDate(parent.getCurrDate().getDateOfDayOfWeek(6));
            } else {
                parent.switchToNextWeekAlt();
//                parent.setCurrDate(parent.getCurrDate().getDateOfDayOfWeek(0));
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        canSwitch = position == 0 || position == DataGlobal.DAYS_OF_WEEK.length - 1;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (canSwitch) {
            superCan = true;
        }
    }
}
