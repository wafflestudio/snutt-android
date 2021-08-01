package com.wafflestudio.snutt2.views.logged_in.lecture_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RootGraphDirections
import com.wafflestudio.snutt2.components.DividerItemDecoration
import com.wafflestudio.snutt2.databinding.FragmentLectureListBinding
import com.wafflestudio.snutt2.lib.android.defaultNavOptions
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableLecturesFragment : BaseFragment() {
    private lateinit var binding: FragmentLectureListBinding

    private val vm: SelectedTimetableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLectureListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        val adapter = LectureListAdapter({
            routeLectureCreate()
        }, {
            routeLectureDetail(it)
        })

        binding.contents.adapter = adapter

        binding.contents.addItemDecoration(
            DividerItemDecoration(requireContext(), R.drawable.lecture_divider)
        )

        vm.lastViewedTable.asObservable().filterEmpty()
            .map { it.lectureList }
            .distinctUntilChanged()
            .bindUi(this) { list ->
                adapter.submitList(
                    list.map<LectureDto, LectureListAdapter.Data> {
                        LectureListAdapter.Data.Lecture(
                            it
                        )
                    }.toMutableList().apply { add(LectureListAdapter.Data.Add) }
                )
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
