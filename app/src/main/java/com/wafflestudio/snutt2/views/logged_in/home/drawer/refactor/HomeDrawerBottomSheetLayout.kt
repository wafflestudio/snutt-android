package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor.bottom_sheet.CreateTableBottomSheet
import com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor.bottom_sheet.MoreActionSheet

@Composable
fun HomeDrawerBottomSheetLayout(
    uiState: HomeDrawerUiState,
    sheetState: ModalBottomSheetState,
    onCloseSheet: () -> Unit,
    onCreateNewTable: (coursebook: CourseBook, title: String) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetContent = {
            when (uiState) {
                is HomeDrawerUiState.Loading -> {}
                is HomeDrawerUiState.Loaded -> when (uiState.homeDrawerBottomSheetType) {
                    HomeDrawerBottomSheetType.Hidden -> {}
                    HomeDrawerBottomSheetType.SelectTheme -> {}
                    HomeDrawerBottomSheetType.CreateNewTheme -> {}
                    is HomeDrawerBottomSheetType.CreateNewTable -> {
                        CreateTableBottomSheet(
                            sheetState = sheetState,
                            sheetType = uiState.homeDrawerBottomSheetType,
                            onCloseSheet = onCloseSheet,
                            onSubmit = onCreateNewTable
                        )
                    }

                    is HomeDrawerBottomSheetType.MoreAction -> {
                        MoreActionSheet(uiState.homeDrawerBottomSheetType.tableSummary)
                    }
                }

            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
    ) {
        content()
    }
}