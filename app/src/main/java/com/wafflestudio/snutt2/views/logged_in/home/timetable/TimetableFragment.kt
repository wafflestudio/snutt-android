package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentTimetableBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimetableFragment : BaseFragment() {

    private lateinit var binding: FragmentTimetableBinding

    private val vm: TimetableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.currentTimetable
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.lectures = it.lectureList
            }

        vm.trimParam
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.trimParam = it
            }

        binding.timetable.setOnLectureClickListener {
            routeLectureDetail(it)
        }

        binding.drawerButton.throttledClicks()
            .bindUi(this) {
                (requireParentFragment().view as? DrawerLayout)?.open()
            }

        binding.lectureListButton.throttledClicks()
            .bindUi(this) {
                routeLectureList()
            }

        binding.shareButton.throttledClicks()
            .bindUi(this) {
                // TODO: share
            }

        binding.notificationsButton.throttledClicks()
            .bindUi(this) {
                routeNotifications()
            }

    }

    private fun routeLectureDetail(lecture: LectureDto) {
        val action =
            if (lecture.isCustom) HomeFragmentDirections.actionHomeFragmentToCustomLectureDetailFragment(
                lecture
            )
            else HomeFragmentDirections.actionHomeFragmentToLectureDetailFragment(
                lecture
            )
        findNavController().navigate(action)
    }

    private fun routeNotifications() {
        findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
    }

    private fun routeLectureList() {
        findNavController().navigate(R.id.action_homeFragment_to_tableLecturesFragment)
    }
}
