package com.wafflestudio.snutt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wafflestudio.snutt.ui.MyLectureFragment;
import com.wafflestudio.snutt.ui.NotificationFragment;
import com.wafflestudio.snutt.ui.SearchFragment;
import com.wafflestudio.snutt.ui.SettingsFragment;
import com.wafflestudio.snutt.ui.TableFragment;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SectionsPagerAdapter  extends FragmentPagerAdapter {
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private static final int NUM_ITEMS = 5;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch(position) {
            case 0:
                return TableFragment.newInstance(0);
            case 1:
                return SearchFragment.newInstance(1);
            case 2:
                return MyLectureFragment.newInstance(2);
            case 3:
                return NotificationFragment.newInstance(3);
            case 4:
                return SettingsFragment.newInstance(4);
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

}
