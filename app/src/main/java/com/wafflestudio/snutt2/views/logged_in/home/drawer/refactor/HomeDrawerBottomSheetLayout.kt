package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor.bottom_sheet.CreateTableBottomSheet

@Composable
fun HomeDrawerBottomSheetLayout(
    uiState: HomeDrawerUiState,
    sheetState: ModalBottomSheetState,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetContent = {
            when (uiState) {
                is HomeDrawerUiState.Loading -> {}
                is HomeDrawerUiState.Loaded ->
                    when (uiState.homeDrawerBottomSheetType) {
                        HomeDrawerBottomSheetType.Hidden -> {}
                        HomeDrawerBottomSheetType.SelectTheme -> {}
                        HomeDrawerBottomSheetType.CreateNewTheme -> {}
                        is HomeDrawerBottomSheetType.CreateNewTable -> {
                            CreateTableBottomSheet(uiState.homeDrawerBottomSheetType)
                        }

                        is HomeDrawerBottomSheetType.MoreAction -> {}
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