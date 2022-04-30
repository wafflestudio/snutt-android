package com.wafflestudio.snutt2.views.logged_in.table_lectures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RootGraphDirections
import com.wafflestudio.snutt2.lib.android.defaultNavOptions
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.ui.common.TopBar
import com.wafflestudio.snutt2.ui.SnuttTheme
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableLecturesFragment : BaseFragment() {
    private val vm: SelectedTimetableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnuttTheme {
                    Column {
                        TopBar(
                            onButtonClick = { findNavController().popBackStack() },
                            titleText = R.string.timetable_app_bar_title
                        )
                        vm.lastViewedTable.get().value?.lectureList?.let { list ->
                            TableLecturesList(
                                lectures = list,
                                {
                                    routeLectureCreate()
                                },
                                {
                                    routeLectureDetail(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun routeLectureCreate() {
        val action =
            RootGraphDirections.actionGlobalCustomLectureDetailFragment(
                null
            )
        findNavController().navigate(action, defaultNavOptions)
    }

    private fun routeLectureDetail(lecture: LectureDto) {
        val action =
            if (lecture.isCustom) RootGraphDirections.actionGlobalCustomLectureDetailFragment(
                lecture
            )
            else RootGraphDirections.actionGlobalLectureDetailFragment(
                lecture
            )
        findNavController().navigate(action, defaultNavOptions)
    }
}
