package com.wafflestudio.snutt2.views.logged_in.home.share

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.LogoIcon
import com.wafflestudio.snutt2.components.compose.MoreIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.components.compose.WriteIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleSharedTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.LocalModalState
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.drawer.MoreActionItem
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import kotlinx.coroutines.launch

sealed class ShareTablePageState {
    object List : ShareTablePageState()
    data class Table(val table: TableDto) : ShareTablePageState()

    fun isStateList() = this == List
    fun isStateTable() = this is Table

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShareTablePage(sharedTableList: List<Map.Entry<CourseBookDto, List<SimpleSharedTableDto>>>?, pageState: ShareTablePageState, updatePageState: (ShareTablePageState) -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val modalState = LocalModalState.current
    val deepLinkTableId =
        (LocalHomePageController.current.homePageState.value as? HomeItem.Share)?.tableId
    val vm: ShareTableViewModel = hiltViewModel()
    val tableListViewModel: TableListViewModel = hiltViewModel()

//    var pageState: ShareTablePageState by remember { mutableStateOf(ShareTablePageState.List) }
    var sharedNewTableTitleField by remember { mutableStateOf("") }

    // HomePage에서 전달받은 intent의 link가 존재하면 다이얼로그 띄우기
    LaunchedEffect(Unit) {
        deepLinkTableId?.let {
            modalState.set(
                onDismiss = { modalState.hide() },
                onConfirm = {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            vm.createSharedTable(deepLinkTableId, sharedNewTableTitleField)
                            vm.fetchSharedTableList()
                            modalState.hide()
                        }
                    }
                },
                title = "시간표 저장",
                positiveButton = "추가",
                negativeButton = context.getString(R.string.common_cancel),
            ) {
                EditText(
                    value = sharedNewTableTitleField,
                    onValueChange = { sharedNewTableTitleField = it }
                )
            }.show()
        }
    }

    BackHandler(pageState.isStateTable()) {
        updatePageState(ShareTablePageState.List)
    }

    AnimatedContent(
        targetState = pageState,
        label = "",
        transitionSpec = {
            slideInHorizontally { fullWidth ->
                if (initialState.isStateList()) fullWidth else -fullWidth
            } with slideOutHorizontally { fullWidth ->
                if (initialState.isStateList()) -fullWidth else fullWidth
            }
        },
    ) { targetState ->
        when (targetState) {
            ShareTablePageState.List -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SNUTTColors.Gray100),
                ) {
                    TopBar(
                        title = {
                            Text(
                                text = stringResource(R.string.share_table_page_title),
                                style = SNUTTTypography.h3,
                            )
                        },
                        navigationIcon = {
                            LogoIcon(
                                modifier = Modifier.size(20.dp),
                                colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                            )
                        },
                    )
                    if (sharedTableList != null && sharedTableList.isEmpty()) ShareTableTutorial()
                    else {
                        SharedTableList(sharedTableList ?: emptyList(), onClickKebabIcon = {
                            bottomSheet.setSheetContent {
                                SharedTableActionBottomSheet(it, onChangeTitle = { title ->
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vm.changeSharedTableTitle(it.id, title)
                                            vm.fetchSharedTableList()
                                            context.toast("시간표 이름을 변경하였습니다.")
                                            modalState.hide()
                                            bottomSheet.hide()
                                        }
                                    }
                                }, onDelete = {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vm.deleteSharedTable(it.id)
                                            vm.fetchSharedTableList()
                                            context.toast("공유된 시간표를 삭제하였습니다.")
                                            modalState.hide()
                                            bottomSheet.hide()
                                        }
                                    }
                                }, onAdd = {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vm.copySharedTable(it.id)
                                            vm.fetchSharedTableList()
                                            tableListViewModel.fetchTableMap()
                                            context.toast("내 시간표로 복제하였습니다.")
                                            modalState.hide()
                                            bottomSheet.hide()
                                        }
                                    }
                                })
                            }
                            scope.launch { bottomSheet.show() }
                        }, onClickItem = { tableId, myTitle ->
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    updatePageState(ShareTablePageState.Table(vm.getSharedTableById(tableId).timetable.copy(title = myTitle)))
                                }
                            }
                        })
                    }
                }
            }

            is ShareTablePageState.Table -> {
                val table = remember {
                    (pageState as ShareTablePageState.Table).table
                }

                CompositionLocalProvider(
                    LocalTableState provides TableState(table, TableTrimParam.Default, null)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopBar(title = {
                            Text(
                                text = table.title,
                                style = SNUTTTypography.h2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }, navigationIcon = {
                            ArrowBackIcon(
                                modifier = Modifier.clicks(1000L) {
                                    updatePageState(ShareTablePageState.List)
                                },
                                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                            )
                        }) {
                            Text(
                                text = SNUTTStringUtils.getFullSemester(
                                    table.year, table.semester
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            TimeTable(selectedLecture = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedTableList(
    sharedTableList: List<Map.Entry<CourseBookDto, List<SimpleSharedTableDto>>>,
    onClickKebabIcon: (SimpleSharedTableDto) -> Unit,
    onClickItem: (String, String) -> Unit
) {
    val bottomSheet = LocalBottomSheetState.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState(),
    ) {
        items(sharedTableList) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = SNUTTStringUtils.getFullSemester(it.key.year, it.key.semester),
                    style = SNUTTTypography.h3.copy(fontWeight = FontWeight.Medium),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SNUTTColors.White900)
                    .padding(horizontal = 20.dp),
            ) {
                it.value.forEachIndexed { idx, table ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clicks {
                                onClickItem(table.id, table.title)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = table.title,
                            style = SNUTTTypography.body1,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        MoreIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    onClickKebabIcon.invoke(table)
                                },
                            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                        )
                    }
                    if (idx != it.value.lastIndex) Divider(color = Color(0x4dc4c4c4))
                }
            }
        }
    }
}

@Composable
fun ShareTableTutorial() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(R.drawable.share_table_page_tutorial_image_1),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(stringResource(R.string.share_table_page_tutorial_text_1), style = SNUTTTypography.h2)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(R.string.share_table_page_tutorial_text_2),
            style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp)
        )
        Text(
            stringResource(R.string.share_table_page_tutorial_text_3),
            style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(R.string.share_table_page_tutorial_text_4),
            style = SNUTTTypography.body2.copy(fontSize = 10.sp, color = SNUTTColors.PurpleBlue),
            modifier = Modifier.padding(start = 150.dp),
        )
        Column(modifier = Modifier.padding(start = 40.dp)) {
            Image(
                painter = painterResource(R.drawable.share_table_page_tutorial_image_2),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_5),
                style = SNUTTTypography.body2.copy(
                    fontSize = 10.sp, color = SNUTTColors.PurpleBlue
                ),
            )
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_6),
                style = SNUTTTypography.body2.copy(
                    fontSize = 10.sp, color = SNUTTColors.PurpleBlue
                ),
            )
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_7),
                style = SNUTTTypography.body2.copy(
                    fontSize = 10.sp, color = SNUTTColors.PurpleBlue
                ),
            )
        }
    }
}

@Composable
fun SharedTableActionBottomSheet(
    table: SimpleSharedTableDto,
    onChangeTitle: (String) -> Unit,
    onDelete: () -> Unit,
    onAdd: () -> Unit,
) {
    val context = LocalContext.current
    val modalState = LocalModalState.current

    var newTitleField by remember { mutableStateOf(table.title) }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        MoreActionItem(
            Icon = { WriteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_title_change)
        ) {
            newTitleField = table.title
            modalState.set(
                onDismiss = { modalState.hide() },
                onConfirm = { onChangeTitle(newTitleField) },
                title = context.getString(R.string.home_drawer_table_title_change),
                positiveButton = context.getString(R.string.common_ok),
                negativeButton = context.getString(R.string.common_cancel),
            ) {
                EditText(value = newTitleField, onValueChange = { newTitleField = it })
            }.show()
        }
        MoreActionItem(
            Icon = { TrashIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_delete),
        ) {
            modalState.set(
                onDismiss = { modalState.hide() },
                onConfirm = onDelete,
                title = context.getString(R.string.home_drawer_table_delete),
                positiveButton = context.getString(R.string.common_ok),
                negativeButton = context.getString(R.string.common_cancel),
            ) {
                Text(
                    stringResource(R.string.table_delete_alert_message),
                    style = SNUTTTypography.body2
                )
            }.show()
        }
        MoreActionItem(
            Icon = { DuplicateIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.shared_table_more_action_add_to_my_table),
        ) {
            onAdd()
        }
    }
}
