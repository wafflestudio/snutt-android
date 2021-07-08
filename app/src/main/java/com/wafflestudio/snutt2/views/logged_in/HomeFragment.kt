package com.wafflestudio.snutt2.views.logged_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentHomeBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.views.logged_in.home.ReviewsFragment
import com.wafflestudio.snutt2.views.logged_in.home.SearchFragment
import com.wafflestudio.snutt2.views.logged_in.home.SettingsFragment
import com.wafflestudio.snutt2.views.logged_in.home.TimetableFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val fragmentMap = mapOf(
        R.id.action_timetable to TimetableFragment(),
        R.id.action_search to SearchFragment(),
        R.id.action_reviews to ReviewsFragment(),
        R.id.action_settings to SettingsFragment(),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigation.setOnItemSelectedListener {
            childFragmentManager.commit {
                replace(
                    R.id.contents,
                    fragmentMap[it.itemId] ?: return@setOnItemSelectedListener false
                )
            }
            true
        }
    }
}
