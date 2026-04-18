package com.wafflestudio.snutt2.feature.home.drawer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.feature.home.drawer.bottom_sheet.CreateTableBottomSheet
import com.wafflestudio.snutt2.feature.home.drawer.bottom_sheet.MoreActionSheet
import com.wafflestudio.snutt2.feature.home.drawer.bottom_sheet.SelectThemeSheetContent
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.HomeDrawerLoggingEffect
import com.wafflestudio.snutt2.logging.compose.LocalAnalyticsLogger
import com.wafflestudio.snutt2.ui.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

/**
 * 논의
 * 바텀시트가 있는 Route 에서, 바텀시트 관련 모든 것은 여기에서 관리한다.
 * 1. UiState 의 필드로, 표시할 바텀시트의 종류를 나타낼 sealed class 를 둔다.
 * 2. sealed class 의 각 구체 클래스에는 바텀시트의 초기 렌더링에 필요하거나, 바텀시트만의 상태를 나타내는 값들을 둔다.
 * 3. UiState의 이 필드의 종류에 따라 분기하고 sheetContent 를 그린다. (필요한 경우, sheetShape 등도 분기)
 * 4. sheetState 는 Route 에서 주입받는다. rememberModalBottomSheetState 를 해서 상태를 관리하는 곳도 Route
 */
@Composable
fun HomeDrawerBottomSheetLayout(
    uiState: HomeDrawerUiState,
    sheetState: ModalBottomSheetState,
    onDismiss: () -> Unit,
    onCreateNewTable: (coursebook: CourseBook, title: String) -> Unit,
    onClickChangeTableName: (tableSummary: TableSummary) -> Unit,
    onClickSetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickUnsetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickShare: (tableSummary: TableSummary) -> Unit,
    onClickSetTheme: (tableSummary: TableSummary) -> Unit,
    onClickDeleteTable: (tableSummary: TableSummary) -> Unit,
    drawerState: DrawerState,
    onToggleCourseBookDrawerItemExpand: (drawerItemIndex: Int) -> Unit,
    onClickExitIcon: () -> Unit,
    onClickCreateNewTable: () -> Unit,
    onClickCreateNewTableOfCourseBook: (courseBook: CourseBook) -> Unit,
    onSelectTable: (tableId: String) -> Unit,
    onClickCopyIcon: (TableSummary) -> Unit,
    onClickMoreIcon: (tableSummary: TableSummary) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmChangeTableTitle: (TableSummary, String) -> Unit,
    onConfirmDeleteTable: (tableSummary: TableSummary) -> Unit,
    onClickPreviewTheme: (TableTheme) -> Unit,
    onClickApplyTheme: () -> Unit,
    onClickDisposeTheme: () -> Unit,
    onClickAddTheme: () -> Unit,
    content: @Composable () -> Unit,
) {
    val analyticsLogger = LocalAnalyticsLogger.current
    HomeDrawerLoggingEffect(drawerState)
    ModalBottomSheetLayout(
        sheetContent = {
            when (uiState.homeDrawerBottomSheetType) {
                HomeDrawerBottomSheetType.Empty -> {
                    ModalBottomSheetPlaceholder()
                }

                is HomeDrawerBottomSheetType.SelectTheme -> {
                    LaunchedEffect(Unit) {
                        analyticsLogger.logScreen(AnalyticsScreen.ThemePreview)
                    }
                    SelectThemeSheetContent(
                        sheetType = uiState.homeDrawerBottomSheetType,
                        onClickPreviewTheme = onClickPreviewTheme,
                        onApply = onClickApplyTheme,
                        onDismiss = onClickDisposeTheme,
                        onClickAddTheme = onClickAddTheme,
                    )
                }

                HomeDrawerBottomSheetType.CreateNewTheme -> {
                    ModalBottomSheetPlaceholder()
                }

                is HomeDrawerBottomSheetType.CreateNewTable -> {
                    CreateTableBottomSheet(
                        sheetState = sheetState,
                        sheetType = uiState.homeDrawerBottomSheetType,
                        onDismiss = onDismiss,
                        onSubmit = onCreateNewTable,
                    )
                }

                is HomeDrawerBottomSheetType.MoreAction -> {
                    MoreActionSheet(
                        tableSummary = uiState.homeDrawerBottomSheetType.tableSummary,
                        onClickChangeTableName = onClickChangeTableName,
                        onClickSetPrimary = onClickSetPrimary,
                        onClickUnsetPrimary = onClickUnsetPrimary,
                        onClickShare = onClickShare,
                        onClickSetTheme = onClickSetTheme,
                        onClickDeleteTable = onClickDeleteTable,
                    )
                }
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
    ) {
        ModalDrawer(
            drawerContent = {
                HomeDrawerContent(
                    modifier = Modifier,
                    uiState = uiState,
                    onToggleExpand = onToggleCourseBookDrawerItemExpand,
                    onClickExitIcon = onClickExitIcon,
                    onClickCreateNewTable = onClickCreateNewTable,
                    onClickCreateNewTableOfCourseBook = onClickCreateNewTableOfCourseBook,
                    onSelectTable = onSelectTable,
                    onClickCopyIcon = onClickCopyIcon,
                    onClickMoreIcon = onClickMoreIcon,
                    onDismissDialog = onDismissDialog,
                    onConfirmChangeTableTitle = onConfirmChangeTableTitle,
                    onConfirmDeleteTable = onConfirmDeleteTable,
                )
            },
            drawerState = drawerState,
        ) {
            content()
        }
    }
}
