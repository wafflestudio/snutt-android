package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentHomeBinding
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.android.HomePage
import com.wafflestudio.snutt2.lib.android.HomePagerController
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.itemSelected
import com.wafflestudio.snutt2.lib.rx.reduceDragSensitivity
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var tablesAdapter: TableListAdapter

    private lateinit var pageAdapter: HomeStateAdapter

    private val selectedTimetableViewModel: SelectedTimetableViewModel by activityViewModels()

    private val tableListViewModel: TableListViewModel by activityViewModels()

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    @Inject
    lateinit var homePagerController: HomePagerController

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var snuttUrls: SnuttUrls

    @Inject
    lateinit var apiOnError: ApiOnError

    private val fragmentIndexMap = listOf(
        R.id.action_timetable,
        R.id.action_search,
        R.id.action_reviews,
        R.id.action_settings
    )

    private var backPressCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        backPressCallback = (
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.contents.currentItem == 0) {
                        requireActivity().finish()
                    } else {
                        binding.contents.setCurrentItem(0, true)
                    }
                }
            }
            ).also {
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
            }
    }

    override fun onPause() {
        super.onPause()
        backPressCallback?.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.refreshData()

        pageAdapter = HomeStateAdapter(this)

        binding.contents.adapter = pageAdapter
        binding.contents.isUserInputEnabled = false

        binding.bottomNavigation.itemSelected()
            // PageView 업데이트로 두 번 콜백이 불리는 것을 방지한다.
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .bindUi(this) {
                val nextItem = fragmentIndexMap.indexOf(it.itemId)
                val currentItem = binding.contents.currentItem
                val reviewItem = fragmentIndexMap.indexOf(R.id.action_reviews)

                if (nextItem == reviewItem && currentItem == reviewItem) {
                    homePagerController.updateHomePage(HomePage.Review(snuttUrls.getReviewMain()))
                } else {
                    binding.contents.currentItem = nextItem
                }
            }

        homePagerController.homePageState
            // 다른 탭에서 강의평 탭으로 진입 시 page pop 이후 리뷰 탭으로 변경이 동작할 수 있도록 딜레이를 준다.
            .delay(10, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                val pageNum = when (it) {
                    HomePage.Timetable -> 0
                    HomePage.Search -> 1
                    is HomePage.Review -> 2
                    HomePage.Setting -> 3
                }
                binding.contents.setCurrentItem(pageNum, true)
            }

        Observables.combineLatest(
            settingsViewModel.trimParam.asObservable(),
            selectedTimetableViewModel.lastViewedTable.asObservable()
        )
            .debounce(1000, TimeUnit.MILLISECONDS)
            .bindUi(this) {
                TimetableWidgetProvider.refreshWidget(requireContext())
            }

        // 스크롤 감도를 낮춘다.
        binding.contents.reduceDragSensitivity(6)

        binding.root.addDrawerListener(
            object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                    homeViewModel.refreshData()
                }

                override fun onDrawerClosed(drawerView: View) {
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            }
        )

        binding.contents.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNavigation.selectedItemId = fragmentIndexMap[position]
                    binding.root.setDrawerLockMode(
                        if (position != 0) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                        else DrawerLayout.LOCK_MODE_UNLOCKED
                    )
                }
            }
        )

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
            onDuplicateItem = { table ->
                tableListViewModel.copyTable(table.id)
                    .bindUi(
                        this,
                        onError = apiOnError,
                        onSuccess = {
                            requireContext().toast("\"${table.title}\" 강좌가 복사되었습니다.")
                        }
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
            selectedTableId = selectedTimetableViewModel.lastViewedTable.asObservable()
                .filterEmpty()
                .map { it.id },
            bindable = this
        )

        binding.closeButton.throttledClicks()
            .bindUi(this) {
                binding.root.close()
            }

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
