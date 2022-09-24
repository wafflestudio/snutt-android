package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeDrawer() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = LocalDrawerState.current
    val tableContext = TableContext.current
    val showBottomSheet = ShowBottomSheet.current
    val hideBottomSheet = HideBottomSheet.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()

    val tableMap by tableListViewModel.tableListOfEachCourseBook.collectAsState()
    val allCourseBook by tableListViewModel.allCourseBook.collectAsState()
    val courseBooksWhichHasTable by tableListViewModel.courseBooksWithTable.collectAsState(
        initial = listOf()
    )
    val tableListOfEachCourseBook = courseBooksWhichHasTable.associateWith { courseBook ->
        tableMap.values.filter { table ->
            table.year == courseBook.year && table.semester == courseBook.semester
        }
    }

    var changeTitleDialogState by remember { mutableStateOf(false) }
    var deleteTableDialogState by remember { mutableStateOf(false) }
    var changeThemeBottomSheetState by remember { mutableStateOf(false) }
    var showMoreBottomSheetState by remember { mutableStateOf(false) }

    var addNewTableDialogState by remember { mutableStateOf(false) }
    var specificSemester by remember { mutableStateOf(false) }
    var selectedCourseBook by remember {
        mutableStateOf(CourseBookDto(tableContext.table.year, tableContext.table.semester))
    }
    // drawer 에서 (...) 버튼을 누르면, 해당 table 의 정보를 여기에 저장.
    // bottomSheet 에서 이름 변경 선택 후 dialog confirm 시 이 정보를 vm 에게 전달
    var showMoreClickedTable by remember { mutableStateOf(Defaults.defaultSimpleTableDto) }

    Column(modifier = Modifier.padding(20.dp)) {
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
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
            color = SNUTTColors.Gray100
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
                        CourseBookDto(tableContext.table.semester, tableContext.table.year)
                    specificSemester = false
                    addNewTableDialogState = true
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            courseBooksWhichHasTable
                .sorted()
                .forEach { courseBook ->
                    var expanded by remember { mutableStateOf(courseBook.year == tableContext.table.year && courseBook.semester == tableContext.table.semester) }
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
                                .rotate(rotation)
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
                                    selected = it.id == tableContext.table.id,
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
                                        showMoreClickedTable = it
                                        showMoreBottomSheetState = true
                                    }
                                )
                            }
                            if (tableListOfEachCourseBook[courseBook].isNullOrEmpty()) {
                                CreateTableItem {
                                    specificSemester = true
                                    selectedCourseBook = courseBook
                                    addNewTableDialogState = true
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
                }
            }
            changeTitleDialogState = false
        }, value = tableNewTitle, onValueChange = { tableNewTitle = it })
    }

    // drawer의 각 시간표 '더 보기' 버튼
    if (showMoreBottomSheetState) {
        LaunchedEffect(Unit) {
            showBottomSheet(150.dp) {
                ShowMoreBottomSheetContent(onChangeTitle = {
                    changeTitleDialogState = true
                }, onDeleteTable = {
                    scope.launch {
                        if (tableListViewModel.checkTableDeletableNew(showMoreClickedTable.id)) {
                            deleteTableDialogState = true
                        } else context.toast(context.getString(R.string.home_drawer_delete_table_unable_alert_message))
                    }
                }, onChangeTheme = {
                    scope.launch {
                        if (tableListViewModel.checkTableThemeChangeableNew(showMoreClickedTable.id)) {
                            changeThemeBottomSheetState = true
                        } else context.toast(context.getString(R.string.home_drawer_change_theme_unable_alert_message))
                    }
                })
            }
            showMoreBottomSheetState = false
        }
    }

    // drawer의 각 시간표 삭제 다이얼로그
    if (deleteTableDialogState) {
        DeleteTableDialog(onDismiss = { deleteTableDialogState = false }, onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    tableListViewModel.deleteTableNew(showMoreClickedTable.id)
                    context.toast(context.getString(R.string.home_drawer_delete_table_success_alert_message))
                }
            }
            scope.launch {
                hideBottomSheet(false)
                deleteTableDialogState = false
            }
        })
    }

    // drawer의 각 시간표 테마 설정
    if (changeThemeBottomSheetState) {
        scope.launch {
            launch { drawerState.close() }
            coroutineScope {
                hideBottomSheet(true)
                showBottomSheet(500.dp) {
                    ChangeThemeBottomSheetContent(onLaunch = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                timetableViewModel.setPreviewTheme(tableContext.table.theme)
                            }
                        }
                    }, onPreview = { idx ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                timetableViewModel.setPreviewTheme(
                                    TimetableColorTheme.fromInt(idx)
                                )
                            }
                        }
                    }, onApply = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                timetableViewModel.updateTheme()
                                scope.launch { hideBottomSheet(false) }
                            }
                        }
                    }, onDispose = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                timetableViewModel.setPreviewTheme(null) // FIXME : 애니메이션 다 끝나고 적용돼서 너무 느리다!
                            }
                        }
                    })
                }
            }
            changeThemeBottomSheetState = false
        }
    }

    // 새로운 시간표 추가 다이럴로그
    if (addNewTableDialogState) {
        var newTableTitle by remember { mutableStateOf("") }
        CustomDialog(
            onDismiss = { addNewTableDialogState = false }, onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    tableListViewModel.createTableNew(selectedCourseBook, newTableTitle)
                    // TODO: 새로 만들면 바로 그 시간표로 이동하면 좋지 않을까? (create의 응답으로 tableId가 와야 한다)
                    addNewTableDialogState = false
                }
            }
        }, title = stringResource(R.string.home_drawer_create_table_dialog_title)
            ) {
                Column {
                    if (specificSemester.not()) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Picker(
                            list = allCourseBook,
                            initialValue = allCourseBook.find { it.year == selectedCourseBook.year && it.semester == selectedCourseBook.semester }
                                ?: allCourseBook.first(),
                            onValueChanged = { index ->
                                selectedCourseBook = allCourseBook[index]
                            },
                            PickerItemContent = { index ->
                                CourseBookPickerItem(
                                    name = allCourseBook[index].toFormattedString(
                                        context
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    EditText(
                        value = newTableTitle,
                        onValueChange = { newTableTitle = it },
                        hint = stringResource(
                            R.string.home_drawer_create_table_dialog_hint
                        ),
                    )
                }
            }
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
                        R.string.home_drawer_table_credit,
                        tableDto.totalCredit ?: 0L
                    ),
                    style = SNUTTTypography.body2,
                    color = SNUTTColors.Gray200,
                    maxLines = 1,
                )
            }
            DuplicateIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks { onDuplicate(tableDto) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            MoreIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks { onShowMore() }
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
            Text(text = stringResource(R.string.home_drawer_timetable_add_button))
        }
    }

    @Composable
    private fun CourseBookPickerItem(name: String) {
        Text(
            text = name, fontSize = 14.sp, textAlign = TextAlign.Center
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
                .background(Color.White)
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
                    WriteIcon(modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "이름 변경")
                }
            }
            Box(modifier = Modifier.clicks { onDeleteTable() }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    TrashIcon(modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "시간표 삭제")
                }
            }
            Box(modifier = Modifier.clicks { onChangeTheme() }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    PaletteIcon(modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "시간표 색상 테마 변경")
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
            Text(stringResource(R.string.table_delete_alert_message))
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
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(R.string.home_drawer_table_theme_change),
                    modifier = Modifier.padding(10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.clicks { onApply() }) {
                    Text(
                        text = stringResource(R.string.home_select_theme_confirm),
                        modifier = Modifier.padding(10.dp)
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
                Text(text = name, textAlign = TextAlign.Center)
            }
        }
    }

    @Preview
    @Composable
    fun HomeDrawerPreview() {
    }
    