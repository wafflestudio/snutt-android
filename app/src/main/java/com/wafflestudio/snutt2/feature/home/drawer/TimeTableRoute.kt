package com.wafflestudio.snutt2.feature.home.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.feature.home.timetable.TimeTableScreen
import com.wafflestudio.snutt2.feature.home.timetable.TimeTableUiEvent
import com.wafflestudio.snutt2.feature.home.timetable.TimeTableUiState
import com.wafflestudio.snutt2.feature.home.timetable.TimeTableViewModel
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.util.toast
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun TimeTableRoute(
    drawerViewModel: HomeDrawerViewModel = hiltViewModel(),
    timeTableViewModel: TimeTableViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
    onNavigateBottomSheetThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateLectureDetail: (LocalLecture) -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateAddLecture: () -> Unit,
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
                is HomeDrawerUiEvent.OpenDrawer -> {
                    scope.launch { drawerState.open() }
                }

                is HomeDrawerUiEvent.CloseDrawer -> {
                    scope.launch { drawerState.close() }
                }

                is HomeDrawerUiEvent.OpenBottomSheet -> {
                    scope.launch { sheetState.show() }
                }

                is HomeDrawerUiEvent.CloseBottomSheet -> {
                    scope.launch { sheetState.hide() }
                }

                is HomeDrawerUiEvent.ShowToast -> {
                    context.toast(uiEvent.displayMessage)
                }

                is HomeDrawerUiEvent.NavigateToThemeDetail -> {
                    onNavigateBottomSheetThemeDetail()
                }

                is HomeDrawerUiEvent.ShareTable -> {
                    (timeTableUiState as? TimeTableUiState.Loaded)?.let {
                        shareScreenshot(uiEvent.table, it.theme, it.tableTrimParam, context)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.targetValue }
            .distinctUntilChanged()
            .filter { it == DrawerValue.Open }
            .collect { drawerViewModel.onDrawerOpened() }
    }

    val isSheetOpen = drawerUiState.homeDrawerBottomSheetType != HomeDrawerBottomSheetType.Empty
    BackHandler(enabled = isSheetOpen) {
        drawerViewModel.closeSheet()
    }

    // FIXME: SelectTheme 시트의 상태가 drawerViewModel(시트 타입)과 timeTableViewModel(프리뷰 테마)에 걸쳐있어서,
    //  dismiss 정리가 두 VM에 분산됨. 크로스-VM 의존 구조 개선 필요.
    BottomSheetDismissEffect(sheetState) {
        drawerViewModel.onSheetDismissed()
        timeTableViewModel.resetPreviewTheme()
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
        onDismiss = drawerViewModel::closeSheet,
        onCreateNewTableTitleChange = drawerViewModel::onCreateNewTableTitleChange,
        onCreateNewTableCourseBookChange = drawerViewModel::onCreateNewTableCourseBookChange,
        onCreateNewTable = drawerViewModel::createNewTable,
        onClickChangeTableName = drawerViewModel::openChangeTableNameDialog,
        onClickSetPrimary = drawerViewModel::setPrimaryTable,
        onClickUnsetPrimary = drawerViewModel::unsetPrimaryTable,
        onClickShare = drawerViewModel::shareTable,
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
        onChangeTableNameTitleChange = drawerViewModel::onChangeTableNameTitleChange,
        onConfirmChangeTableTitle = drawerViewModel::changeTableTitle,
        onConfirmDeleteTable = drawerViewModel::deleteTable,
        onClickPreviewTheme = { theme ->
            drawerViewModel.setPreviewTheme(theme)
            timeTableViewModel.setPreviewTheme(theme)
        },
        onClickApplyTheme = drawerViewModel::applyTheme,
        onClickDisposeTheme = {
            timeTableViewModel.resetPreviewTheme()
            drawerViewModel.closeSheet()
        },
        onClickAddTheme = drawerViewModel::navigateToThemeDetail,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                TimeTableScreen(
                    uiState = timeTableUiState,
                    onClickDrawerIcon = drawerViewModel::onClickDrawerIcon,
                    onClickTableTitle = timeTableViewModel::showTableTitleChangeDialog,
                    onClickTableLecturesListIcon = onNavigateLecturesOfTable,
                    onClickVacancyBanner = onNavigateVacancyNotification,
                    onClickLectureCell = onNavigateLectureDetail,
                    onClickBookmarkIcon = onNavigateBookmark,
                    onClickAddBySearch = onNavigateSearch,
                    onClickAddManually = onNavigateAddLecture,
                    onVisitSessionlessLectureList = timeTableViewModel::visitSessionlessLectureList,
                    onDismissDialog = timeTableViewModel::dismissDialog,
                    onChangeTableNameTitleChange = timeTableViewModel::onChangeTableNameTitleChange,
                    onConfirmChangeTableTitle = timeTableViewModel::changeTableTitle,
                )
            }
            bottomBar()
        }
    }
}
