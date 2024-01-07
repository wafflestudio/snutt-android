package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
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
    val composableStates = ComposableStatesWithScope(scope)
    val tableListViewModel: TableListViewModel = hiltViewModel()
    val timetableViewModel: TimetableViewModel = hiltViewModel()

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
            icon = { PaletteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_theme_change),
        ) {
            scope.launch(Dispatchers.Main) {
                if (tableListViewModel.checkTableThemeChangeable(table.id)) {
                    bottomSheet.hide()
                    drawerState.close()

                    bottomSheet.setSheetContent {
                        ChangeThemeBottomSheet(
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
                            onDispose = {
                                scope.launch {
                                    timetableViewModel.setPreviewTheme(null)
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
