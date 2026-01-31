package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.MoreActionItem
import com.wafflestudio.snutt2.components.compose.PaletteIcon
import com.wafflestudio.snutt2.components.compose.PeopleIcon
import com.wafflestudio.snutt2.components.compose.PeopleOffIcon
import com.wafflestudio.snutt2.components.compose.ShareIcon
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.components.compose.WriteIcon
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.shareScreenshot
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.showTableDeleteDialog
import com.wafflestudio.snutt2.views.logged_in.home.showTitleChangeDialog
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TableMoreActionBottomSheet(
    table: SimpleTableDto,
    scope: CoroutineScope,
) {
    val context = LocalContext.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val bottomSheet = LocalBottomSheetState.current
    val drawerState = LocalDrawerState.current
    val analyticsLogger = LocalAnalyticsLogger.current
    val composableStates = ComposableStatesWithScope(scope)
    val tableListViewModel: TableListViewModel = hiltViewModel()
    val timetableViewModel: TimetableViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val tableTrimParam by userViewModel.trimParam.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = { WriteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_title_change),
        ) {
            showTitleChangeDialog(
                table.title,
                table.id,
                composableStates,
                tableListViewModel::changeTableName,
            )
        }
        if (table.isPrimary) {
            MoreActionItem(
                icon = {
                    PeopleOffIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                },
                text = stringResource(R.string.home_drawer_table_set_not_primary),
            ) {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        tableListViewModel.setTableNotPrimary(table.id)
                        tableListViewModel.fetchTableMap()
                        bottomSheet.hide()
                    }
                }
            }
        } else {
            MoreActionItem(
                icon = {
                    PeopleIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                },
                text = stringResource(R.string.home_drawer_table_set_primary),
            ) {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        tableListViewModel.setTablePrimary(table.id)
                        tableListViewModel.fetchTableMap()
                        bottomSheet.hide()
                    }
                }
            }
        }
        MoreActionItem(
            icon = { ShareIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_share_table_image),
        ) {
            scope.launch(Dispatchers.Main) {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    shareScreenshot(
                        tableListViewModel.searchTableById(table.id),
                        tableTrimParam,
                        context,
                    )
                }
                analyticsLogger.logScreen(AnalyticsScreen.TimetableShare) // 안드로이드에는 TimetableShare 화면이 따로 없지만, iOS와의 통일성을 위해 공유 버튼 클릭 시 로깅한다.
            }
        }
        MoreActionItem(
            icon = { PaletteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_theme_change),
        ) {
            scope.launch(Dispatchers.Main) {
                if (tableListViewModel.checkTableThemeChangeable(table.id)) {
                    bottomSheet.hide()
                    drawerState.close()

                    bottomSheet.setSheetContent {
                        SelectThemeBottomSheet(
                            onPreview = { theme ->
                                scope.launch {
                                    timetableViewModel.setPreviewTheme(theme)
                                }
                            },
                            onApply = {
                                scope.launch {
                                    launchSuspendApi(
                                        apiOnProgress,
                                        apiOnError,
                                    ) {
                                        timetableViewModel.updateTheme()
                                        bottomSheet.hide()
                                    }
                                }
                            },
                            onDismiss = {
                                scope.launch {
                                    timetableViewModel.setPreviewTheme(null)
                                    bottomSheet.hide()
                                }
                            },
                        )
                    }
                    bottomSheet.show()
                } else {
                    context.toast(context.getString(R.string.home_drawer_change_theme_unable_alert_message))
                }
            }
        }
        MoreActionItem(
            icon = { TrashIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_delete),
        ) {
            scope.launch {
                if (tableListViewModel.checkTableDeletable()) {
                    showTableDeleteDialog(table.id, composableStates) { tableId ->
                        tableListViewModel.deleteTableAndSwitchIfNeeded(tableId)
                    }
                } else {
                    context.toast(context.getString(R.string.home_drawer_delete_table_unable_alert_message))
                }
            }
        }
    }
}
