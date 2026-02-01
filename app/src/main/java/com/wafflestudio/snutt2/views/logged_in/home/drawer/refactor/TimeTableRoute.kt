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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.shareScreenshot
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TimeTableRoute(
    drawerViewModel: HomeDrawerViewModel = hiltViewModel(),
    // FIXME: 리팩토링 안 된 TimeTable 쪽으로 상태 변경 시켜줄 때를 위해 임시 사용
    timetableViewModel: TimetableViewModel = hiltViewModel(),
    onNavigateBottomSheetThemeDetail: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // HomeDrawer 관련
    val uiState by drawerViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

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

    HomeDrawerBottomSheetLayout(
        uiState = uiState,
        sheetState = sheetState,
        onDismiss = {
            // 시트가 닫힐 때 수행해야 할 로직은 Route에서 주입할 수밖에 없음
            scope.launch {
                // FIXME: timetable 리팩 후 변경
                timetableViewModel.setPreviewTheme(null)
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
        onClickDrawerIcon = {
            scope.launch {
                drawerState.open()
            }
        },
        onClickPreviewTheme = { theme ->
            drawerViewModel.setPreviewTheme(theme)

            // FIXME: timetable 리팩 후 변경
            timetableViewModel.setPreviewTheme(theme)
        },
        onClickApplyTheme = drawerViewModel::applyTheme,
        onClickDisposeTheme = {
            scope.launch {
                // FIXME: timetable 리팩 후 변경
                timetableViewModel.setPreviewTheme(null)
                sheetState.hide()
            }
        },
        onClickAddTheme = drawerViewModel::navigateToThemeDetail,
    )
}

@Composable
fun TimeTableScreen(
    uiState: HomeDrawerUiState,
    onClickDrawerIcon: () -> Unit,
) {
    // FIXME: 임시
    TimetablePageTemp(false) {
        onClickDrawerIcon()
    }
}
