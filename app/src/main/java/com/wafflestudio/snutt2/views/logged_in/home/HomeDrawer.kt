package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeDrawer() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = LocalDrawerState.current
    val sheetState = LocalBottomSheetState.current
    val sheetContentSetter = LocalBottomSheetContentSetter.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()

    val table by timetableViewModel.currentTable.collectAsState()
    val blockedTable = table ?: Defaults.defaultTableDto
    val allCourseBook by tableListViewModel.allCourseBook.collectAsState()
    val courseBooksWhichHaveTable by tableListViewModel.courseBooksWhichHaveTable.collectAsState(
        initial = listOf()
    )
    val tableListOfEachCourseBook by tableListViewModel.tableListOfEachCourseBook.collectAsState(
        initial = mapOf()
    )

    var changeTitleDialogState by remember { mutableStateOf(false) }
    var deleteTableDialogState by remember { mutableStateOf(false) }
    var specificSemester by remember { mutableStateOf(false) }
    var selectedCourseBook by remember {
        mutableStateOf(CourseBookDto(blockedTable.year, blockedTable.semester))
    }
    // drawer 에서 (...) 버튼을 누르면, 해당 table 의 정보를 여기에 저장.
    // bottomSheet 에서 이름 변경 선택 후 dialog confirm 시 이 정보를 vm 에게 전달
    var showMoreClickedTable by remember { mutableStateOf(Defaults.defaultSimpleTableDto) }

    // FIXME: 새 시간표 만드는 바텀시트를 띄우는 코드. 이걸 쓰는 곳이 두 군데라서 재사용 하면 좋을 것 같아 따로 빼 놨는데 모양이 좋지 않다.
    val showCreateTableBottomSheet = {
        sheetContentSetter.invoke {
            var newTableTitle by remember { mutableStateOf("") }
            CreateNewTableBottomSheet(
                newTitle = newTableTitle,
                onEditTextChange = { newTableTitle = it },
                onPickerChange = { selectedCourseBook = it },
                onCancel = { scope.launch { sheetState.hide() } },
                onComplete = {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            tableListViewModel.createTableNew(
                                selectedCourseBook,
                                newTableTitle
                            )
                            // TODO: 새로 만들면 바로 그 시간표로 이동하면 좋지 않을까? (create의 응답으로 tableId가 와야 한다)
                            scope.launch {
                                sheetState.hide()
                                keyboardManager?.hide()
                            }
                        }
                    }
                },
                specificSemester, allCourseBook, selectedCourseBook
            )
        }
        scope.launch { sheetState.show() }
    }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxSize()
            .padding(20.dp)
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
                modifier = Modifier.clicks { scope.launch { drawerState.close() } },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }
        Divider(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp), color = SNUTTColors.Gray100
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
                style = SNUTTTypography.subtitle1,
                fontSize = 24.sp,
                modifier = Modifier.clicks {
                    selectedCourseBook =
                        CourseBookDto(blockedTable.semester, blockedTable.year)
                    specificSemester = false
                    showCreateTableBottomSheet.invoke()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        LazyColumn {
            items(courseBooksWhichHaveTable) { courseBook ->
                var expanded by remember { mutableStateOf(courseBook.year == blockedTable.year && courseBook.semester == blockedTable.semester) }
                val rotation by animateFloatAsState(if (expanded) -180f else 0f)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clicks { expanded = expanded.not() }
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
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (tableListOfEachCourseBook[courseBook].isNullOrEmpty()) {
                        RedDot()
                    }
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        tableListOfEachCourseBook[courseBook]?.forEach {
                            TableItem(
                                tableDto = it,
                                selected = it.id == blockedTable.id,
                                onSelect = { selectedTableId ->
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            tableListViewModel.changeSelectedTableNew(
                                                selectedTableId
                                            )
                                            drawerState.close()
                                        }
                                    }
                                },
                                onDuplicate = { table ->
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            tableListViewModel.copyTableNew(table.id)
                                            context.toast(
                                                context.getString(
                                                    R.string.home_drawer_copy_success_message,
                                                    table.title
                                                )
                                            )
                                        }
                                    }
                                },
                                onShowMore = {
                                    // 시간표 (더 보기) bottomSheet
                                    showMoreClickedTable = it
                                    sheetContentSetter.invoke {
                                        ShowMoreBottomSheetContent(onChangeTitle = {
                                            changeTitleDialogState = true
                                        }, onDeleteTable = {
                                            scope.launch {
                                                if (tableListViewModel.checkTableDeletableNew(
                                                        showMoreClickedTable.id
                                                    )
                                                ) {
                                                    deleteTableDialogState = true
                                                } else context.toast(context.getString(R.string.home_drawer_delete_table_unable_alert_message))
                                            }
                                        }, onChangeTheme = {
                                            // 테마 변경 bottomSheet
                                            scope.launch {
                                                if (tableListViewModel.checkTableThemeChangeableNew(
                                                        showMoreClickedTable.id
                                                    )
                                                ) {
                                                    sheetState.snapTo(ModalBottomSheetValue.Hidden)
                                                    drawerState.close()

                                                    sheetContentSetter.invoke {
                                                        ChangeThemeBottomSheetContent(onLaunch = {
                                                            scope.launch {
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.setPreviewTheme(
                                                                        blockedTable.theme
                                                                    )
                                                                }
                                                            }
                                                        }, onPreview = { idx ->
                                                            scope.launch {
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.setPreviewTheme(
                                                                        TimetableColorTheme.fromInt(
                                                                            idx
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }, onApply = {
                                                            scope.launch {
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.updateTheme()
                                                                    scope.launch { sheetState.hide() }
                                                                }
                                                            }
                                                        }, onDispose = {
                                                            scope.launch {
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.setPreviewTheme(
                                                                        null
                                                                    ) // FIXME : 애니메이션 다 끝나고 적용돼서 너무 느리다!
                                                                }
                                                            }
                                                        })
                                                    }
                                                    sheetState.show()
                                                } else context.toast(context.getString(R.string.home_drawer_change_theme_unable_alert_message))
                                            }
                                        })
                                    }
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }
                        // 가장 최근 학기에 시간표가 없을 때, "+ 시간표 추가하기" 를 누르면 시간표 추가 바텀시트 보여주기
                        if (tableListOfEachCourseBook[courseBook].isNullOrEmpty()) {
                            CreateTableItem {
                                specificSemester = true
                                selectedCourseBook = courseBook
                                showCreateTableBottomSheet.invoke()
                            }
                        }
                    }
                }
            }
        }
    }

    // 시간표 이름 변경 다이얼로그
    if (changeTitleDialogState) {
        var tableNewTitle by remember { mutableStateOf(showMoreClickedTable.title) }
        ChangeTableTitleDialog(onDismiss = { changeTitleDialogState = false }, onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    tableListViewModel.changeNameTableNew(showMoreClickedTable.id, tableNewTitle)
                    changeTitleDialogState = false
                    scope.launch { sheetState.hide() }
                }
            }
        }, value = tableNewTitle, onValueChange = { tableNewTitle = it })
    }

    // drawer의 각 시간표 삭제 다이얼로그
    if (deleteTableDialogState) {
        DeleteTableDialog(onDismiss = { deleteTableDialogState = false }, onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    tableListViewModel.deleteTableNew(showMoreClickedTable.id)
                    deleteTableDialogState = false
                    context.toast(context.getString(R.string.home_drawer_delete_table_success_alert_message))
                    scope.launch { sheetState.hide() }
                }
            }
        })
    }
}

@Composable
private fun TableItem(
    tableDto: SimpleTableDto,
    selected: Boolean,
    onSelect: (String) -> Unit,
    onDuplicate: (SimpleTableDto) -> Unit,
    onShowMore: () -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clicks { onSelect(tableDto.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            VividCheckedIcon(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (selected) 1f else 0f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = tableDto.title,
                style = SNUTTTypography.body1,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.home_drawer_table_credit, tableDto.totalCredit ?: 0L
                ),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black300),
                maxLines = 1,
            )
        }
        DuplicateIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks { onDuplicate(tableDto) },
            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
        )
        Spacer(modifier = Modifier.width(10.dp))
        MoreIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks { onShowMore() },
            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
        )
    }
}

@Composable
private fun CreateTableItem(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(25.dp))
        Text(
            text = stringResource(R.string.home_drawer_timetable_add_button),
            style = SNUTTTypography.body1
        )
    }
}

@Composable
private fun CourseBookPickerItem(name: String) {
    Text(
        text = name, style = SNUTTTypography.button,
    )
}

@Composable
private fun ShowMoreBottomSheetContent(
    onChangeTitle: () -> Unit,
    onDeleteTable: () -> Unit,
    onChangeTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.clicks { onChangeTitle() }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                WriteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.home_drawer_table_title_change),
                    style = SNUTTTypography.body1,
                )
            }
        }
        Box(modifier = Modifier.clicks { onDeleteTable() }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                TrashIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.home_drawer_table_delete),
                    style = SNUTTTypography.body1,
                )
            }
        }
        Box(modifier = Modifier.clicks { onChangeTheme() }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                PaletteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.home_drawer_table_theme_change),
                    style = SNUTTTypography.body1,
                )
            }
        }
    }
}

@Composable
private fun ChangeTableTitleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.home_drawer_change_name_dialog_title)
    ) {
        EditText(
            value = value, onValueChange = onValueChange
        )
    }
}

@Composable
private fun DeleteTableDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.home_drawer_table_delete)
    ) {
        Text(stringResource(R.string.table_delete_alert_message), style = SNUTTTypography.body2)
    }
}

@Composable
private fun ChangeThemeBottomSheetContent(
    onLaunch: () -> Unit,
    onPreview: (Int) -> Unit,
    onApply: () -> Unit,
    onDispose: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLaunch()
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose { onDispose() }
    }

    val themeList = listOf(
        stringResource(R.string.home_select_theme_snutt) to painterResource(R.drawable.theme_preview_snutt),
        stringResource(R.string.home_select_theme_modern) to painterResource(R.drawable.theme_preview_modern),
        stringResource(R.string.home_select_theme_autumn) to painterResource(R.drawable.theme_preview_autumn),
        stringResource(R.string.home_select_theme_pink) to painterResource(R.drawable.theme_preview_pink),
        stringResource(R.string.home_select_theme_ice) to painterResource(R.drawable.theme_preview_ice),
        stringResource(R.string.home_select_theme_grass) to painterResource(R.drawable.theme_preview_grass),
    )

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(
                text = stringResource(R.string.home_drawer_table_theme_change),
                modifier = Modifier.padding(10.dp),
                style = SNUTTTypography.body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clicks { onApply() }) {
                Text(
                    text = stringResource(R.string.home_select_theme_confirm),
                    modifier = Modifier.padding(10.dp),
                    style = SNUTTTypography.body1,
                )
            }
        }
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            themeList.forEachIndexed { themeIdx, nameAndIdPair ->
                ThemeItem(
                    name = nameAndIdPair.first,
                    painter = nameAndIdPair.second,
                    modifier = Modifier.clicks { onPreview(themeIdx) }
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
private fun CreateNewTableBottomSheet(
    newTitle: String,
    onEditTextChange: (String) -> Unit,
    onPickerChange: (CourseBookDto) -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    specificSemester: Boolean,
    allCourseBook: List<CourseBookDto>,
    selectedCourseBook: CourseBookDto,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(25.dp)
            .clicks {}
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "취소", style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onCancel.invoke()
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "완료", style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onComplete.invoke()
                }
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "새로운 시간표 만들기",
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Gray600)
        )
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = newTitle,
            onValueChange = { onEditTextChange(it) },
            hint = "시간표 제목을 입력하세요",
            underlineColor = if (specificSemester.not()) SNUTTColors.SNUTTTheme else SNUTTColors.Gray200,
            underlineColorFocused = if (specificSemester.not()) SNUTTColors.SNUTTTheme else SNUTTColors.Black900,
            underlineWidth = 2.dp,
        )
        Spacer(modifier = Modifier.height(25.dp))
        if (specificSemester.not()) {
            Spacer(modifier = Modifier.height(5.dp))
            Picker(
                list = allCourseBook,
                initialCenterIndex = allCourseBook.indexOfFirst { it.year == selectedCourseBook.year && it.semester == selectedCourseBook.semester },
                onValueChanged = { index ->
                    onPickerChange(allCourseBook[index])
                },
                PickerItemContent = {
                    CourseBookPickerItem(name = allCourseBook[it].toFormattedString(context))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ThemeItem(
    name: String,
    painter: Painter,
    modifier: Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Image(
            painter = painter, contentDescription = "", modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = name, textAlign = TextAlign.Center, style = SNUTTTypography.body1)
        }
    }
}

@Preview
@Composable
fun HomeDrawerPreview() {
}
