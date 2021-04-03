package com.wafflestudio.snutt2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wafflestudio.snutt2.ui.*

/**
 * Created by makesource on 2016. 1. 16..
 */
class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        when (position) {
            0 -> return TableFragment.newInstance(0)
            1 -> return SearchFragment.newInstance(1)
            2 -> return MyLectureFragment.newInstance(2)
            3 -> return NotificationFragment.newInstance(3)
            4 -> return SettingsFragment.newInstance(4)
        }
        throw IllegalStateException("WTF")
    }

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    companion object {
        /**
         * A [FragmentPagerAdapter] that returns a fragment corresponding to
         * one of the sections/tabs/pages.
         */
        private const val NUM_ITEMS = 5
    }
}
