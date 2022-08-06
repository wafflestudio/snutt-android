package com.wafflestudio.snutt2.views.logged_in.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentHomeBinding
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.android.HomePage
import com.wafflestudio.snutt2.lib.android.HomePagerController
import com.wafflestudio.snutt2.lib.android.ReviewUrlController
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.itemSelected
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupDialog
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.rxMaybe
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

    private val popupViewModel: PopupViewModel by activityViewModels()

    @Inject
    lateinit var homePagerController: HomePagerController

    @Inject
    lateinit var reviewUrlController: ReviewUrlController

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var snuttUrls: SnuttUrls

    @Inject
    lateinit var apiOnError: ApiOnError

    @Inject
    lateinit var prefStorage: PrefStorage

    @Inject
    lateinit var popupState: PopupState

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
                    if (homePagerController.homePageState.value == HomePage.Timetable)
                        requireActivity().finish()
                    else
                        homePagerController.update(HomePage.Timetable)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.refreshData()

        if (popupState.getAndUpdatePopupState().not()) {
            popupViewModel.fetchPopup()
                .subscribeBy(
                    onSuccess = {
                        PopupDialog(
                            context = requireContext(),
                            onClickHideFewDays = { popupViewModel.invalidateShownPopUp(it) },
                            url = it.url,
                        ).show()
                    }, onError = {}
                )
        }

        pageAdapter = HomeStateAdapter(this)

        binding.contents.adapter = pageAdapter
        binding.contents.isUserInputEnabled = false

        homePagerController.homePageState.asObservable()
            .bindUi(this) {
                binding.contents.currentItem = it.pageNum
                binding.bottomNavigation.selectedItemId = listOf(
                    R.id.action_timetable,
                    R.id.action_search,
                    R.id.action_reviews,
                    R.id.action_settings
                )[it.pageNum]
                binding.root.setDrawerLockMode(
                    if (it.pageNum != 0) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                    else DrawerLayout.LOCK_MODE_UNLOCKED
                )
            }

        binding.bottomNavigation.setItemOnTouchListener(R.id.action_reviews) { _, _ ->
            if (homePagerController.homePageState.value == HomePage.Review) {
                reviewUrlController.update(snuttUrls.getReviewMain())
            }
            false
        }

        binding.bottomNavigation.itemSelected()
            .bindUi(this) {
                homePagerController.update(
                    when (it.itemId) {
                        R.id.action_timetable -> HomePage.Timetable
                        R.id.action_search -> HomePage.Search
                        R.id.action_reviews -> HomePage.Review
                        R.id.action_settings -> HomePage.Setting
                        else -> throw IllegalStateException("")
                    }
                )
            }

        Observables.combineLatest(
            settingsViewModel.trimParam.asObservable(),
            selectedTimetableViewModel.lastViewedTable.asObservable()
        )
            .debounce(1000, TimeUnit.MILLISECONDS)
            .bindUi(this) {
                TimetableWidgetProvider.refreshWidget(requireContext())
            }

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

        binding.coursebookSelectButton.throttledClicks()
            .flatMapMaybe {
                rxMaybe {
                    try {
                        return@rxMaybe tableListViewModel.getCourseBooks()
                    } catch (e: Throwable) {
                        apiOnError(e)
                        return@rxMaybe null
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapMaybe { list ->
                val current = tableListViewModel.selectedCourseBooks.get().value
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
