package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.rxSingle
import kotlin.RuntimeException

enum class HomeItem(@DrawableRes val icon: Int) {
    Timetable(R.drawable.ic_timetable),
    Search(R.drawable.ic_search),
    Review(R.drawable.ic_review),
    Settings(R.drawable.ic_setting)
}

data class TableContext(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val theme: TimetableColorTheme
)

val HomeDrawerStateContext = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}
val LocalTableContext = compositionLocalOf<TableContext> {
    throw RuntimeException("")
}
val UncheckedNotificationContext = compositionLocalOf<Boolean> {
    throw RuntimeException("")
}

@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(HomeItem.Timetable) }
    var unCheckedNotification by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val selectedTimetableViewModel = hiltViewModel<SelectedTimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()

    rxSingle { homeViewModel.getUncheckedNotificationsExist() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { unCheckedNotification = it },
            onError = { }
        )

    val (table, theme) =
        Observables.combineLatest(
            selectedTimetableViewModel.lastViewedTable.asObservable().filterEmpty(),
            selectedTimetableViewModel.selectedPreviewTheme.asObservable()
        ).distinctUntilChanged()
            .subscribeAsState(initial = Pair(Defaults.defaultTableDto, Optional(null)))
            .value

    val trimParam = selectedTimetableViewModel.trimParam
        .asObservable().distinctUntilChanged()
        .subscribeAsState(initial = TableTrimParam.Default).value
    val selectedCourseBook = tableListViewModel.selectedCourseBooks.asObservable().filterEmpty()
        .subscribeAsState(initial = CourseBookDto(1L, 2022)).value
    val selectedCourseBookTableList = tableListViewModel.selectedCourseBookTableList
        .subscribeAsState(initial = emptyList()).value

    val tableContext = TableContext(table, trimParam, (theme.get() ?: table.theme))

    homeViewModel.refreshData()

    ModalDrawer(
        drawerContent = {
            HomeDrawer(
                selectedCourseBook,
                selectedCourseBookTableList,
                onClickItem = {
                    scope.launch { drawerState.close() }
                    tableListViewModel.changeSelectedTable(it)
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = currentScreen == HomeItem.Timetable
    ) {
        CompositionLocalProvider(
            HomeDrawerStateContext provides drawerState
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentScreen) {
                        HomeItem.Timetable ->
                            CompositionLocalProvider(
                                LocalTableContext provides tableContext,
                                UncheckedNotificationContext provides unCheckedNotification
                            ) { TimetablePage() }
                        HomeItem.Search -> SearchPage()
                        HomeItem.Review -> ReviewPage()
                        HomeItem.Settings -> SettingsPage()
                    }
                }

                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Timetable },
                    ) {
                        Text(text = "timetable")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Search },
                    ) {
                        Text(text = "search")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Review },
                    ) {
                        Text(text = "review")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Settings },
                    ) {
                        Text(text = "settings")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
