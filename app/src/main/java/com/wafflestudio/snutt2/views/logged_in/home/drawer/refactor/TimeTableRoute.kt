package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.shareScreenshot
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimeTableScreenNew
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimeTableUiEvent
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimeTableViewModelNew
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TimeTableRoute(
    drawerViewModel: HomeDrawerViewModel = hiltViewModel(),
    timeTableViewModel: TimeTableViewModelNew = hiltViewModel(),
    onNavigateBottomSheetThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateLectureDetail: (LocalLecture) -> Unit,
    onBottomNavigate: (HomeItem) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // FIXME: 뷰모델 refresh 함수 주석 참조
    LaunchedEffect(Unit) {
        timeTableViewModel.refresh()
    }

    // HomeDrawer 관련
    val drawerUiState by drawerViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    // TimeTable 관련
    val timeTableUiState by timeTableViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        drawerViewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is HomeDrawerUiEvent.OpenBottomSheet -> {
                    scope.launch {
                        sheetState.show()
                    }
                }

                is HomeDrawerUiEvent.CloseBottomSheet -> {
                    scope.launch {
                        sheetState.hide()
                    }
                }

                is HomeDrawerUiEvent.ChangeBottomSheet -> {
                    scope.launch {
                        sheetState.hide()
                        snapshotFlow { sheetState.currentValue }.first { it == ModalBottomSheetValue.Hidden }
                        drawerViewModel.onChangeSheetType(uiEvent.bottomSheetType)
                        drawerViewModel.uiState.first { it is HomeDrawerUiState.Loaded && it.homeDrawerBottomSheetType == uiEvent.bottomSheetType }
                        withFrameNanos {}
                        withFrameNanos {}
                        withFrameNanos {} // FIXME: 안전한가??
                        sheetState.show()
                    }
                }

                is HomeDrawerUiEvent.CloseDrawer -> {
                    scope.launch {
                        drawerState.close()
                    }
                }

                is HomeDrawerUiEvent.OpenShareScreenshotBottomSheet -> {
                    scope.launch {
                        shareScreenshot(
                            uiEvent.tableDto,
                            uiEvent.tableTrimParam,
                            context,
                        )
                    }
                }

                is HomeDrawerUiEvent.ShowToast -> {
                    context.toast(uiEvent.displayMessage)
                }

                is HomeDrawerUiEvent.NavigateToThemeDetail -> {
                    onNavigateBottomSheetThemeDetail()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        timeTableViewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is TimeTableUiEvent.ShowToast -> {
                    context.toast(uiEvent.displayMessage)
                }
            }
        }
    }

    HomeDrawerBottomSheetLayout(
        uiState = drawerUiState,
        sheetState = sheetState,
        onDismiss = {
            scope.launch {
                timeTableViewModel.resetPreviewTheme()
                sheetState.hide()
            }
        },
        onCreateNewTable = drawerViewModel::createNewTable,
        onClickChangeTableName = drawerViewModel::openChangeTableNameDialog,
        onClickSetPrimary = drawerViewModel::setPrimaryTable,
        onClickUnsetPrimary = drawerViewModel::unsetPrimaryTable,
        onClickShareTable = drawerViewModel::openShareTableBottomSheet,
        onClickSetTheme = drawerViewModel::onClickSetThemeSheet,
        onClickDeleteTable = drawerViewModel::openDeleteTableDialog,
        drawerState = drawerState,
        onToggleCourseBookDrawerItemExpand = drawerViewModel::toggleCourseBookDrawerItem,
        onClickExitIcon = {
            scope.launch {
                drawerState.close()
            }
        },
        onClickCreateNewTable = drawerViewModel::openCreateNewTableSheet,
        onClickCreateNewTableOfCourseBook = drawerViewModel::openCreateNewTableOfSpecificCourseBookSheet,
        onSelectTable = drawerViewModel::selectTable,
        onClickCopyIcon = drawerViewModel::copyTable,
        onClickMoreIcon = drawerViewModel::openMoreActionBottomSheet,
        onDismissDialog = drawerViewModel::dismissDialog,
        onConfirmChangeTableTitle = drawerViewModel::changeTableTitle,
        onConfirmDeleteTable = drawerViewModel::deleteTable,
        onClickPreviewTheme = { theme ->
            drawerViewModel.setPreviewTheme(theme)
            timeTableViewModel.setPreviewTheme(theme)
        },
        onClickApplyTheme = drawerViewModel::applyTheme,
        onClickDisposeTheme = {
            scope.launch {
                timeTableViewModel.resetPreviewTheme()
                sheetState.hide()
            }
        },
        onClickAddTheme = drawerViewModel::navigateToThemeDetail,
    ) {
        TimeTableScreenNew(
            uiState = timeTableUiState,
            onClickDrawerIcon = { scope.launch { drawerState.open() } },
            onClickTableTitle = timeTableViewModel::showTableTitleChangeDialog,
            onClickTableLecturesListIcon = onNavigateLecturesOfTable,
            onClickNotificationIcon = onNavigateNotification,
            onClickVacancyBanner = onNavigateVacancyNotification,
            onClickLectureCell = onNavigateLectureDetail,
            onDismissDialog = timeTableViewModel::dismissDialog,
            onConfirmChangeTableTitle = timeTableViewModel::changeTableTitle,
            onBottomNavigate = onBottomNavigate,
        )
    }
}
