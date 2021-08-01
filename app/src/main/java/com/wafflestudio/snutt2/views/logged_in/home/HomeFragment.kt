package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentHomeBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.reduceDragSensitivity
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var tablesAdapter: TableListAdapter

    private lateinit var pageAdapter: HomeStateAdapter


    private val timetableViewModel: TimetableViewModel by activityViewModels()

    private val tableListViewModel: TableListViewModel by activityViewModels()

    private val homeViewModel: HomeViewModel by activityViewModels()

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var apiOnError: ApiOnError

    private val fragmentIndexMap = listOf(
        R.id.action_timetable,
        R.id.action_search,
        R.id.action_reviews,
        R.id.action_settings
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

        homeViewModel.refreshData()

        pageAdapter = HomeStateAdapter(this)

        binding.contents.adapter = pageAdapter

        binding.bottomNavigation.setOnItemSelectedListener {
            binding.contents.currentItem = fragmentIndexMap.indexOf(it.itemId)
            true
        }

        // 스크롤 감도를 낮춘다.
        binding.contents.reduceDragSensitivity(6)

        binding.contents.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.selectedItemId = fragmentIndexMap[position]
            }
        })

        binding.coursebookSelectButton.throttledClicks()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapMaybe {
                val current = tableListViewModel.selectedCourseBooks.get().value
                val list = tableListViewModel.courseBooks.get()
                dialogController.showSelectorDialog(
                    R.string.home_drawer_selector_dialog_title,
                    list,
                    list.indexOf(current),
                ) { it.toFormattedString(requireContext()) }
            }
            .bindEvent(this) {
                tableListViewModel.setSelectedCourseBook(it)
            }

        tableListViewModel.selectedCourseBooks.asObservable()
            .bindUi(this) {
                binding.coursebookTitle.text = it.value?.toFormattedString(requireContext()) ?: "-"
            }

        tablesAdapter = TableListAdapter(
            onCreateItem = {
                dialogController.showTextDialog(
                    R.string.home_drawer_create_table_dialog_title,
                    hint = R.string.home_drawer_create_table_dialog_hint
                )
                    .flatMapCompletable {
                        tableListViewModel.createTable(it)
                    }
                    .bindUi(
                        this,
                        onError = apiOnError
                    )
            },
            onSelectItem = {
                binding.root.close()
                tableListViewModel.changeSelectedTable(it.id)
            },
            onDuplicateItem = {
                tableListViewModel.copyTable(it.id)
                    .bindUi(
                        this,
                        onError = apiOnError
                    )
            },
            onShowMoreItem = {
                TableModifyFragment(
                    it,
                    onThemeChange = {
                        TableThemeSheet(it).show(childFragmentManager, "theme")
                        binding.root.close()
                    }
                ).show(childFragmentManager, "modify_${it.hashCode()}")
            },
            selectedTableId = timetableViewModel.currentTimetable.map { it.id },
            bindable = this
        )

        binding.tablesContent.adapter = tablesAdapter

        tableListViewModel.selectedCourseBookTableList
            .bindUi(this) { list ->
                tablesAdapter.submitList(
                    list.map<SimpleTableDto, TableListAdapter.Data> { TableListAdapter.Data.Table(it) }
                        .toMutableList()
                        .apply { add(TableListAdapter.Data.Add) }
                        .toList()
                )
            }
    }
}
