package com.wafflestudio.snutt2.feature.home.drawer

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.ui.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.LogoIcon
import com.wafflestudio.snutt2.ui.components.compose.RedDot
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toFormattedString

/**
 * 논의
 * 이건 Screen 은 아닌데 Screen 체급이라 어떻게 이름붙일 지 좀 고민
 */
@Composable
fun HomeDrawerContent(
    modifier: Modifier,
    uiState: HomeDrawerUiState,
    onToggleExpand: (drawerItemIndex: Int) -> Unit,
    onClickExitIcon: () -> Unit,
    onClickCreateNewTable: () -> Unit,
    onClickCreateNewTableOfCourseBook: (courseBook: CourseBook) -> Unit,
    onSelectTable: (tableId: String) -> Unit,
    onClickCopyIcon: (TableSummary) -> Unit,
    onClickMoreIcon: (tableSummary: TableSummary) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmChangeTableTitle: (TableSummary, String) -> Unit,
    onConfirmDeleteTable: (tableSummary: TableSummary) -> Unit,
) {
    val context = LocalContext.current

    HomeDrawerDialogs(
        uiState = uiState,
        onDismiss = onDismissDialog,
        onConfirmChangeTableTitle = onConfirmChangeTableTitle,
        onConfirmDeleteTable = onConfirmDeleteTable,
    )

    Column(
        modifier = modifier
            .background(SNUTTColors.White900)
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoIcon(modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h2,
            )
            Spacer(modifier = Modifier.weight(1f))
            ExitIcon(
                modifier = Modifier.clicks {
                    onClickExitIcon()
                },
            )
        }
        Divider(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
            color = SNUTTColors.Gray100,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.timetable_app_bar_title),
                style = SNUTTTypography.body1,
                color = SNUTTColors.Gray200,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "+",
                modifier = Modifier.clicks {
                    onClickCreateNewTable()
                },
                style = SNUTTTypography.subtitle1,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        LazyColumn {
            itemsIndexed(
                items = uiState.courseBookDrawerItemList,
                key = { _, it ->
                    it.item.courseBook.year * 10 + it.item.courseBook.semester
                },
            ) { idx, (courseBookDrawerItem, expanded) ->
                val rotation by animateFloatAsState(if (expanded) -180f else 0f)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clicks {
                            onToggleExpand(idx)
                        },
                ) {
                    Text(
                        text = courseBookDrawerItem.courseBook.toFormattedString(context),
                        style = SNUTTTypography.h3,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    ArrowDownIcon(
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(rotation),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (courseBookDrawerItem.showNewCoursebookDot) {
                        RedDot()
                    }
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        courseBookDrawerItem.tableList.forEach {
                            CourseBookDrawerItem(
                                tableSummary = it,
                                isSelectedTable = (uiState.selectedTable?.id == it.id),
                                onSelectTable = onSelectTable,
                                onClickCopyIcon = onClickCopyIcon,
                                onClickMoreIcon = onClickMoreIcon,
                            )
                        }
                        if (courseBookDrawerItem.tableList.isEmpty()) {
                            CreateTableItem(
                                onClick = {
                                    onClickCreateNewTableOfCourseBook(courseBookDrawerItem.courseBook)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateTableItem(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(25.dp))
        Text(
            text = stringResource(R.string.home_drawer_timetable_add_button),
            style = SNUTTTypography.body1,
        )
    }
}

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1400)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1400)
@Composable
private fun HomeDrawerContent_Default() {
    SnuttPreviewSurface {
        HomeDrawerContent(
            modifier = Modifier,
            uiState = HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = CourseBook(1, 2026),
                        showNewCoursebookDot = false,
                        tableList = listOf(TableSummary.Default, TableSummary.Default),
                    ).toDataWithState(true),
                    CoursebookDrawerItem(
                        courseBook = CourseBook(1, 2025),
                        showNewCoursebookDot = false,
                        tableList = emptyList(),
                    ).toDataWithState(false),
                    CoursebookDrawerItem(
                        courseBook = CourseBook(1, 2024),
                        showNewCoursebookDot = false,
                        tableList = emptyList(),
                    ).toDataWithState(false),
                ),
                TableSummary.Default,
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty,
                dialogState = HomeDrawerUiState.DialogState.None,
            ),
            onToggleExpand = {},
            onClickExitIcon = {},
            onClickCreateNewTable = {},
            onClickCreateNewTableOfCourseBook = {},
            onSelectTable = {},
            onClickCopyIcon = {},
            onClickMoreIcon = {},
            onDismissDialog = {},
            onConfirmChangeTableTitle = { _, _ -> },
            onConfirmDeleteTable = {},
        )
    }
}
