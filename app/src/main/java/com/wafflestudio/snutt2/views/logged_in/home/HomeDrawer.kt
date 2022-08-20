package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeDrawer(
    onClickTableItem: (String) -> Unit,
    selectedTable: TableDto,
    closeDrawer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val selectedTimetableViewModel = hiltViewModel<SelectedTimetableViewModel>()

    val selectedCourseBook = tableListViewModel.selectedCourseBooks.asObservable().filterEmpty()
        .subscribeAsState(initial = CourseBookDto(1L, 2020)).value
    val tableListOfSelectedCourseBook = tableListViewModel.selectedCourseBookTableList
        .subscribeAsState(initial = emptyList()).value
    var courseBookList by remember { mutableStateOf(listOf(selectedCourseBook)) }


    var courseBookSelectDialogState by remember { mutableStateOf(false) }
    var pickerSelectedCourseBook = selectedCourseBook
    LaunchedEffect(courseBookSelectDialogState) {
        courseBookList = tableListViewModel.getCourseBooks()
    }
    if (courseBookSelectDialogState) {
        CustomDialog(
            onDismiss = {
                courseBookSelectDialogState = false
            },
            onConfirm = {
                tableListViewModel.setSelectedCourseBook(pickerSelectedCourseBook)
                courseBookSelectDialogState = false
            },
            title = stringResource(R.string.home_drawer_semester_select_dialog_title),
        ) {
            Picker(
                list = courseBookList,
                value = selectedCourseBook,
                onValueChanged = { pickerSelectedCourseBook = it },
                PickerItemContent = { index ->
                    CourseBookPickerItem(name = courseBookList[index].toFormattedString(context))
                }
            )
        }
    }

    var addNewTableDialogState by remember { mutableStateOf(false) }
    if (addNewTableDialogState) {
        var newTableTitle by remember { mutableStateOf("") }
        CustomDialog(
            onDismiss = { addNewTableDialogState = false },
            onConfirm = {
                tableListViewModel.createTable(newTableTitle)
                    .subscribeBy(onError = {})  // TODO: onError
                addNewTableDialogState = false
            },
            title = stringResource(R.string.home_drawer_create_table_dialog_title)
        ) {
            EditText(
                value = newTableTitle,
                onValueChange = { newTableTitle = it },
                hint = stringResource(
                    R.string.home_drawer_create_table_dialog_hint
                )
            )
        }
    }

    val showBottomSheet = ShowBottomSheet.current
    val hideBottomSheet = HideBottomSheet.current

    // drawer 에서 (...) 버튼을 누르면, 해당 table 의 정보를 여기에 저장.
    // bottomSheet 에서 이름 변경 선택 후 dialog confirm 시 이 정보를 vm 에게 전달
    var showMoreClickedTable by remember { mutableStateOf(Defaults.defaultSimpleTableDto) }

    var changeTitleDialogState by remember { mutableStateOf(false) }
    if (changeTitleDialogState) {
        var tableNewTitle by remember { mutableStateOf(showMoreClickedTable.title) }
        ChangeTableTitleDialog(
            onDismiss = { changeTitleDialogState = false },
            onConfirm = {
                tableListViewModel.changeNameTable(showMoreClickedTable.id, tableNewTitle)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onError = {}) // TODO: apiOnError

                changeTitleDialogState = false
            },
            value = tableNewTitle,
            onValueChange = { tableNewTitle = it }
        )
    }

    var deleteTableDialogState by remember { mutableStateOf(false) }
    if (deleteTableDialogState) {
        DeleteTableDialog(
            onDismiss = { deleteTableDialogState = false },
            onConfirm = {
                tableListViewModel.deleteTable(showMoreClickedTable.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            context.toast(context.getString(R.string.home_drawer_delete_table_success_alert_message))
                        },
                        onError = {}    // TODO: apiOnError
                    )
                scope.launch {
                    hideBottomSheet(false)
                    deleteTableDialogState = false
                }
            }
        )
    }

    var changeThemeBottomSheetState by remember { mutableStateOf(false) }
    if (changeThemeBottomSheetState) {
        scope.launch {
            launch { closeDrawer.invoke() }
            coroutineScope {
                hideBottomSheet(true)
                showBottomSheet(500.dp) {
                    ChangeThemeBottomSheetContent(
                        onLaunch = {
                            selectedTimetableViewModel.setSelectedPreviewTheme(
                                selectedTimetableViewModel.lastViewedTable.get().value?.theme
                            )
                        },
                        onPreview = { idx ->
                            selectedTimetableViewModel.setSelectedPreviewTheme(
                                TimetableColorTheme.fromInt(idx)
                            )
                        },
                        onApply = {
                            selectedTimetableViewModel.updateTheme(
                                showMoreClickedTable.id,
                                selectedTimetableViewModel.selectedPreviewTheme.get().value!!
                            )
                                .subscribeBy(
                                    onComplete = {
                                        // 바뀐 테마가 시간표에 적용되면 sheet 내리기 (FIXME: 애니메이션 안 나타남)
                                        scope.launch {
                                            hideBottomSheet(false)
                                        }
                                    },
                                    onError = { }  // TODO: apiOnError
                                )
                        },
                        onDispose = {
                            selectedTimetableViewModel.setSelectedPreviewTheme(null)    // FIXME : 애니메이션 다 끝나고 적용돼서 너무 느리다!
                        }
                    )
                }
            }
            changeThemeBottomSheetState = false
        }

    }

    var showMoreBottomSheetState by remember { mutableStateOf(false) }
    if (showMoreBottomSheetState) {
        scope.launch {
            showBottomSheet(150.dp) {
                ShowMoreBottomSheetContent(
                    onChangeTitle = {
                        changeTitleDialogState = true
                    },
                    onDeleteTable = {
                        if (tableListViewModel.checkTableDeletable(showMoreClickedTable.id)) {
                            deleteTableDialogState = true
                        } else context.toast(context.getString(R.string.home_drawer_delete_table_unable_alert_message))
                    },
                    onChangeTheme = {
                        if (tableListViewModel.checkTableThemeChangeable(showMoreClickedTable.id)) {
                            changeThemeBottomSheetState = true
                        } else context.toast(context.getString(R.string.home_drawer_change_theme_unable_alert_message))
                    }
                )
            }
            showMoreBottomSheetState = false
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoIcon(modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.sign_in_logo_title))
            Spacer(modifier = Modifier.weight(1f))
            ExitIcon()
        }
        Divider(modifier = Modifier.padding(vertical = 20.dp))
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clicks {
                courseBookSelectDialogState = true
            }
        ) {
            Text(text = selectedCourseBook.toFormattedString(context))
            Spacer(modifier = Modifier.width(6.dp))
            ArrowDownIcon(modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 10.dp)
        ) {
            tableListOfSelectedCourseBook.forEach {
                TableItem(
                    tableDto = it,
                    selected = it.id == selectedTable.id,
                    onSelect = onClickTableItem,
                    onDuplicate = { table ->
                        tableListViewModel.copyTable(table.id)
                            .subscribeBy(      // TODO: dispose
                                onSuccess = {
                                    context.toast(
                                        context.getString(
                                            R.string.home_drawer_copy_success_message,
                                            table.title
                                        )
                                    )
                                },
                                onError = {}   // TODO: onError
                            )
                    },
                    onShowMore = {
                        showMoreClickedTable = it
                        showMoreBottomSheetState = true
                    }
                )
            }
            CreateTableItem(onClick = { addNewTableDialogState = true })
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
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Row(
            modifier = Modifier
                .weight(1f)
                .clicks { onSelect(tableDto.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckedIcon(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (selected) 1f else 0f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = tableDto.title)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    id = R.string.home_drawer_table_credit,
                    tableDto.totalCredit ?: 0L
                )
            )
        }
        DuplicateIcon(modifier = Modifier
            .size(30.dp)
            .clicks { onDuplicate(tableDto) }
        )
        Spacer(modifier = Modifier.width(10.dp))
        MoreIcon(modifier = Modifier
            .size(30.dp)
            .clicks { onShowMore() }
        )
    }
}

@Composable
private fun CreateTableItem(
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
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
        text = name,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
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
            value = value,
            onValueChange = onValueChange
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
        stringResource(R.string.home_select_theme_autumn) to painterResource(R.drawable.theme_preview_autumn),
        stringResource(R.string.home_select_theme_modern) to painterResource(R.drawable.theme_preview_modern),
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
            Box(
                modifier = Modifier.clicks { onApply() }
            ) {
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
                    modifier = Modifier.clicks { onPreview(themeIdx) })
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
            painter = painter,
            contentDescription = "",
            modifier = Modifier.size(80.dp)
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
