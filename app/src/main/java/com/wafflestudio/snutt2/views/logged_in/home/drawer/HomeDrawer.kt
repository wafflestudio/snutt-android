package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeDrawer() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = LocalDrawerState.current
    val bottomSheet = LocalBottomSheetState.current
    val table = LocalTableState.current.table

    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val allCourseBook by tableListViewModel.allCourseBook.collectAsState()
    val courseBooksWhichHaveTable by tableListViewModel.courseBooksWhichHaveTable.collectAsState(
        initial = listOf(),
    )
    val tableListOfEachCourseBook by tableListViewModel.tableListOfEachCourseBook.collectAsState(
        initial = mapOf(),
    )

    Column(
        modifier = Modifier
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
            ExitIcon(modifier = Modifier.clicks { scope.launch { drawerState.close() } })
        }
        Divider(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp), color = SNUTTColors.Gray100,
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
                    bottomSheet.setSheetContent {
                        CreateTableBottomSheet(
                            scope,
                            allCourseBook,
                            CourseBookDto(table.semester, table.year),
                            onConfirm = tableListViewModel::createTable,
                        )
                    }
                    scope.launch {
                        bottomSheet.show()
                    }
                },
                style = SNUTTTypography.subtitle1,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        LazyColumn {
            items(courseBooksWhichHaveTable) { courseBook ->
                var expanded by remember(table, courseBook) { mutableStateOf(courseBook.year == table.year && courseBook.semester == table.semester) }
                val rotation by animateFloatAsState(if (expanded) -180f else 0f)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clicks { expanded = expanded.not() },
                ) {
                    Text(
                        text = courseBook.toFormattedString(context),
                        style = SNUTTTypography.h3,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    ArrowDownIcon(
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(rotation),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (tableListOfEachCourseBook[courseBook].isNullOrEmpty()) {
                        RedDot()
                    }
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        tableListOfEachCourseBook[courseBook]?.forEach {
                            DrawerTableItem(it, scope)
                        }
                        // 가장 최근 학기에 시간표가 없을 때, "+ 시간표 추가하기" 를 누르면 시간표 추가 바텀시트 보여주기
                        if (tableListOfEachCourseBook[courseBook].isNullOrEmpty()) {
                            CreateTableItem {
                                bottomSheet.setSheetContent {
                                    CreateTableBottomSheet(
                                        scope,
                                        allCourseBook,
                                        courseBook,
                                        true,
                                        tableListViewModel::createTable,
                                    )
                                }
                                scope.launch {
                                    bottomSheet.show()
                                }
                            }
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

@Preview
@Composable
fun HomeDrawerPreview() {
}
