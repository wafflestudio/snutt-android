package com.wafflestudio.snutt2.views.logged_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentHomeBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.views.logged_in.home.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: TableListAdapter

    private val timetableViewModel: TimetableViewModel by activityViewModels()

    private val courseBookAndTablesViewModel: CourseBookAndTablesViewModel by activityViewModels()

    @Inject
    lateinit var dialogController: DialogController

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

        childFragmentManager.commit {
            replace(
                R.id.contents,
                fragmentMap[R.id.action_timetable]!!
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timetableViewModel.fetchLastViewedTable()
        courseBookAndTablesViewModel.refresh()

        binding.bottomNavigation.setOnItemSelectedListener {
            childFragmentManager.commit {
                replace(
                    R.id.contents,
                    fragmentMap[it.itemId] ?: return@setOnItemSelectedListener false
                )
            }
            true
        }

        Observable.combineLatest(
            binding.coursebookSelectButton.throttledClicks(),
            courseBookAndTablesViewModel.courseBooks,
            { _, courseBooks -> courseBooks }
        )
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapMaybe { list ->
                dialogController.showSelectorDialog(
                    R.string.home_drawer_selector_dialog_title, list
                ) { it.toFormattedString(requireContext()) }
            }
            .bindEvent(this) {
                courseBookAndTablesViewModel.selectCurrentCourseBook(it)
            }

        courseBookAndTablesViewModel.selectedCourseBooks.bindUi(this) {
            binding.coursebookTitle.text = it.toFormattedString(requireContext())
        }

        adapter = TableListAdapter(
            onCreateItem = {
                dialogController.showTextDialog(R.string.home_drawer_create_table_dialog_title, hint = R.string.home_drawer_create_table_dialog_hint)
                    .bindUi(this) {
                        courseBookAndTablesViewModel.createTable(it)
                    }
            },
            onSelectItem = {
                binding.root.close()
                courseBookAndTablesViewModel.changeCurrentTable(it)
            },
            onDuplicateItem = {},
            onShowMoreItem = {},
            selectedTableId = timetableViewModel.currentTimetable.map { it.id },
            bindable = this
        )

        binding.tablesContent.adapter = adapter

        courseBookAndTablesViewModel.currentCourseBooksTable
            .bindUi(this) { list ->
                adapter.submitList(
                    list.map<TableDto, TableListAdapter.Data> { TableListAdapter.Data.Table(it) }
                        .toMutableList()
                        .apply { add(TableListAdapter.Data.Add) }
                        .toList()
                )
            }
    }
}
