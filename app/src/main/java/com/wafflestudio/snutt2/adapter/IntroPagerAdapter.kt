package com.wafflestudio.snutt2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wafflestudio.snutt2.ui.IntroFragment

/**
 * Created by makesource on 2017. 6. 23..
 */
class IntroPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return IntroFragment.newInstance(position)
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }
}
