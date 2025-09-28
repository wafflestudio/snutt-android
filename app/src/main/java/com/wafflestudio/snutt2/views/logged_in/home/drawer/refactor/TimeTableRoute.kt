package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun TimeTableRoute(
    drawerViewModel: HomeDrawerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    // HomeDrawer 관련
    val uiState by drawerViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
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

                is HomeDrawerUiEvent.CloseDrawer -> {
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        }
    }

    HomeDrawerBottomSheetLayout(
        uiState = uiState,
        sheetState = sheetState,
        onCloseSheet = {
            scope.launch {
                sheetState.hide()
            }
        },
        onCreateNewTable = { coursebook, title ->
            drawerViewModel.createNewTable(coursebook, title)
        }
    ) {
        ModalDrawer(
            drawerContent = {
                HomeDrawerContent(
                    modifier = Modifier,
                    uiState = uiState,
                    onToggleExpand = drawerViewModel::toggleCourseBookDrawerItem,
                    onClickExitIcon = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onClickCreateNewTable = drawerViewModel::openCreateNewTableSheet,
                    onClickCreateNewTableOfCourseBook = drawerViewModel::openCreateNewTableOfSpecificCourseBookSheet,
                    onSelectTable = drawerViewModel::selectTable,
                    onClickCopyIcon = drawerViewModel::copyTable,
                    onClickMoreIcon = drawerViewModel::openMoreActionBottomSheet
                )
            },
            drawerState = drawerState,
        ) {
            TimeTableScreen(
                uiState = uiState,
                onClickDrawerIcon = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
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