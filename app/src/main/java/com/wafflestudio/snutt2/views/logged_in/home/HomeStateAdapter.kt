package com.wafflestudio.snutt2.views.logged_in.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewsFragment
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchFragment
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsFragment
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableFragment

class HomeStateAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    private val fragmentMap = listOf(
        TimetableFragment(),
        SearchFragment(),
        ReviewsFragment(),
        SettingsFragment(),
    )

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return fragmentMap[position]
    }
}
