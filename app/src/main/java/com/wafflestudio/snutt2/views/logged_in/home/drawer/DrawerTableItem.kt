package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerTableItem(
    table: SimpleTableDto,
    scope: CoroutineScope, // 상위의 scope를 가져오지 않으면, TableItem이 삭제된 이후의 Job들이 cancel된다.
) {
    val context = LocalContext.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val bottomSheet = LocalBottomSheetState.current
    val drawerState = LocalDrawerState.current
    val selected = table.id == LocalTableState.current.table.id

    val tableListViewModel: TableListViewModel = hiltViewModel()

    Row(
        modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clicks {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            tableListViewModel.changeSelectedTable(table.id)
                            drawerState.close()
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VividCheckedIcon(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (selected) 1f else 0f),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = table.title,
                style = SNUTTTypography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.home_drawer_table_credit, table.totalCredit ?: 0L,
                ),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black300),
                maxLines = 1,
            )
        }
        DuplicateIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            tableListViewModel.copyTable(table.id)
                            context.toast(
                                context.getString(
                                    R.string.home_drawer_copy_success_message,
                                    table.title,
                                ),
                            )
                        }
                    }
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
        )
        Spacer(modifier = Modifier.width(10.dp))
        MoreIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks {
                    bottomSheet.setSheetContent {
                        TableMoreActionBottomSheet(table, scope)
                    }
                    scope.launch { bottomSheet.show() }
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
        )
    }
}
