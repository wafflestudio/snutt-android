package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.search.lectureApiWithOverlapDialog
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LectureDetailCustomPage() {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = LocalNavController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val focusManager = LocalFocusManager.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModelNew>(backStackEntry)
    val editMode by vm.editMode.collectAsState()
    val editingLectureDetail by vm.editingLectureDetail.collectAsState()
    val currentTable by vm.currentTable.collectAsState()
    val tableColorTheme = currentTable?.theme ?: TimetableColorTheme.SNUTT

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    var editExitDialogState by remember { mutableStateOf(false) }
//    var editTimeDialogState by remember { mutableStateOf(false) }
    var editingClassTimeIndex by remember { mutableStateOf(0) }
    var deleteTimeDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogMessage by remember { mutableStateOf("") }

    val dialogState = LocalModalState.current

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var bottomSheetContent by remember {
        mutableStateOf<@Composable ColumnScope.() -> Unit>({
            Box(modifier = Modifier.size(1.dp))
        })
    }
    val bottomSheetContentSetter: (@Composable ColumnScope.() -> Unit) -> Unit = {
        bottomSheetContent = it
    }
    LaunchedEffect(sheetState.isVisible) {
        // 숨겨질 때, 내부 content를 초기화 해 주지 않으면 다른 sheet를 띄울 때 어색한 모습이 된다. (높이 널뛰기)
        if (!sheetState.isVisible) {
            bottomSheetContent = { Box(modifier = Modifier.size(1.dp)) }
        }
    }

    BackHandler(enabled = editMode) {
        if (sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        } else if (vm.isAddMode()) { // 새 커스텀 강의 추가일 때는 뒤로가기 하면 바로 나가기
            navController.popBackStack()
        } else editExitDialogState = true
    }

    /* TODO (진행중)
     * 시간 및 장소 item 추가했을 때 애니메이션 적용하기 (LazyColumn 의 기능 모방)
     * 추가시 애니메이션은 되는데 삭제시는 방법을 고민중
     */
    var classTimeAnimationState by remember {
        mutableStateOf(MutableTransitionState(true))
    }

    ModalBottomSheetLayout(
        sheetContent = bottomSheetContent,
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)
        // gesturesEnabled 가 없다! 그래서 드래그해서도 닫아진다..
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.Gray100)
                .clicks { focusManager.clearFocus() }
        ) {
            TopBar(
                title = {
                    Text(
                        text = stringResource(R.string.lecture_detail_app_bar_title),
                        style = SNUTTTypography.h2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    ArrowBackIcon(
                        modifier = Modifier.clicks {
                            // '직접 강좌 추가하기' 로 진입했으면 < 아이콘 누를 때 바로 pop
                            if (vm.isAddMode()) {
                                vm.setAddMode(false)
                                vm.unsetEditMode()
                                navController.popBackStack()
                            } else {
                                if (editMode) editExitDialogState = true
                                else navController.popBackStack()
                            }
                        },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }, actions = {
                Text(
                    text = if (editMode) stringResource(R.string.lecture_detail_top_bar_complete)
                    else stringResource(R.string.lecture_detail_top_bar_edit),
                    style = SNUTTTypography.body1,
                    modifier = Modifier
                        .clicks {
                            if (editMode.not()) vm.setEditMode()
                            else {
                                scope.launch {
                                    lectureApiWithOverlapDialog(
                                        apiOnProgress,
                                        apiOnError,
                                        onLectureOverlap = { message ->
                                            lectureOverlapDialogMessage = message
                                            lectureOverlapDialogState = true
                                        }
                                    ) {
                                        if (vm.isAddMode()) {
                                            vm.createLecture2()
                                            vm.unsetEditMode()
                                            vm.setAddMode(false)
                                                /* FIXME
                                                     * 안드로이드는 여기서 그대로 detailPage 에 남는다.
                                                     *
                                                     * 하지만 @POST("/tables/{id}/lecture") api 는
                                                     * tableDTO만 주기 때문에, 새로 추가된 커스텀 강의가 부여받은 id를
                                                     * 알 수가 없다. (알려면 lectureList에서 find해야 되는데 nullable문제 및 compare 기준 문제 존재)
                                                     * 그래서 바로 편집을 누르면 id를 모르는 강의에 대해 PUT을 하기 때문에 403이 난다.
                                                     *
                                                     * 그런데 기존 앱은 코드가 잘못 짜여져 있어서, 완료 후 바로 편집을 누르고
                                                     * 완료를 다시 누르면 수정이 되는 게 아니라 또 추가가 된다. (계속 POST api를 쏜다)
                                                     *
                                                     * ios는 완료를 누르면 창이 닫히고 lecturesOfTable로 돌아가도록 돼 있다.
                                                     * ios를 따라갈것인지, 서버 응답을 tableDto에서 lectureDto로 바꿔달라고 할 지 결정
                                                     */
                                            scope.launch(Dispatchers.Main) { navController.popBackStack() }
                                        } else {
                                            vm.updateLecture2()
                                            vm.initializeEditingLectureDetail(
                                                editingLectureDetail
                                            )
                                            vm.unsetEditMode()
                                        }
                                    }
                                }
                            }
                        }
                        .padding(end = 16.dp)
                )
            }
            )
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                Margin(height = 10.dp)
                Column(modifier = Modifier.background(SNUTTColors.White900)) {
                    Margin(height = 4.dp)
                    LectureDetailItem(title = stringResource(R.string.lecture_detail_lecture_title)) {
                        EditText(
                            value = editingLectureDetail.course_title,
                            onValueChange = {
                                vm.editEditingLectureDetail(editingLectureDetail.copy(course_title = it))
                            },
                            enabled = editMode,
                            modifier = Modifier.fillMaxWidth(),
                            underlineEnabled = false,
                            textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                            hint = if (editMode) stringResource(R.string.lecture_detail_lecture_title_hint)
                            else stringResource(R.string.lecture_detail_hint_nothing),
                        )
                    }
                    LectureDetailItem(title = stringResource(R.string.lecture_detail_instructor)) {
                        EditText(
                            value = editingLectureDetail.instructor,
                            onValueChange = {
                                vm.editEditingLectureDetail(editingLectureDetail.copy(instructor = it))
                            },
                            enabled = editMode,
                            modifier = Modifier.fillMaxWidth(), underlineEnabled = false,
                            textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                            hint = if (editMode) stringResource(R.string.lecture_detail_instructor_hint)
                            else stringResource(R.string.lecture_detail_hint_nothing),
                        )
                    }
                    LectureDetailItem(
                        title = stringResource(R.string.lecture_detail_color),
                        modifier = Modifier.clicks {
                            if (editMode) {
                                navController.navigate(NavigationDestination.LectureColorSelector)
                            }
                        }
                    ) {
                        Row {
                            ColorBox(
                                editingLectureDetail.colorIndex,
                                editingLectureDetail.color,
                                tableColorTheme,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            AnimatedVisibility(visible = editMode) {
                                ArrowRight(
                                    modifier = Modifier.size(16.dp),
                                    colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                                )
                            }
                        }
                    }
                    LectureDetailItem(title = stringResource(R.string.lecture_detail_credit)) {
                        EditText(
                            value = editingLectureDetail.credit.toString(),
                            onValueChange = {
                                vm.editEditingLectureDetail(editingLectureDetail.copy(credit = it.stringToLong()))
                            },
                            enabled = editMode,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            underlineEnabled = false,
                            textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                            hint = "0",
                        )
                    }
                    Margin(height = 4.dp)
                }
                Margin(height = 10.dp)
                Column(modifier = Modifier.background(SNUTTColors.White900)) {
                    Margin(height = 4.dp)
                    LectureDetailRemark(
                        title = stringResource(R.string.lecture_detail_remark),
                        editMode = editMode
                    ) {
                        EditText(
                            value = editingLectureDetail.remark,
                            onValueChange = {
                                vm.editEditingLectureDetail(editingLectureDetail.copy(remark = it))
                            },
                            enabled = editMode,
                            modifier = Modifier.fillMaxWidth(),
                            underlineEnabled = false,
                            textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                            hint = if (editMode) stringResource(R.string.lecture_detail_remark_hint)
                            else stringResource(R.string.lecture_detail_hint_nothing),
                        )
                    }
                }
                Margin(height = 10.dp)
                Column(modifier = Modifier.background(SNUTTColors.White900)) {
                    Text(
                        text = stringResource(R.string.lecture_detail_class_time),
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 14.dp),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                    )
                    editingLectureDetail.class_time_json.forEachIndexed { idx, classTime ->
                        LectureDetailTimeAndLocation(
                            timeText = SNUTTStringUtils.getClassTimeText(classTime),
                            locationText = classTime.place,
                            editTime = {
                                bottomSheetContentSetter.invoke {
                                    DayTimePickerSheet(
                                        classTime = classTime,
                                        onDismiss = { scope.launch { sheetState.hide() } },
                                        onConfirm = { editedClassTime ->
                                            vm.editEditingLectureDetail(
                                                editingLectureDetail.copy(
                                                    class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                                        .also {
                                                            it[idx] = editedClassTime
                                                        }
                                                )
                                            )
                                            scope.launch { sheetState.hide() }
                                        }
                                    )
                                }
                                scope.launch { sheetState.show() }
                            },
                            onLocationTextChange = { changedLocation ->
                                vm.editEditingLectureDetail(
                                    editingLectureDetail.copy(
                                        class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                            .also {
                                                it[idx] = classTime.copy(place = changedLocation)
                                            }
                                    )
                                )
                            },
                            onClickDeleteIcon = {
                                editingClassTimeIndex = idx
                                deleteTimeDialogState = true
                            },
                            onLongClick = {
                                editingClassTimeIndex = idx
                                deleteTimeDialogState = true
                            },
                            editMode = editMode,
                            isCustom = true,
                            visible = if (idx == editingLectureDetail.class_time_json.lastIndex) classTimeAnimationState
                            else MutableTransitionState(true)
                        )
                    }
                    AnimatedVisibility(
                        visible = editMode
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clicks {
                                    classTimeAnimationState =
                                        MutableTransitionState(false).apply { targetState = true }
                                    vm.editEditingLectureDetail(
                                        editingLectureDetail.copy(
                                            class_time_json = editingLectureDetail.class_time_json
                                                .toMutableList()
                                                .also { it.add(Defaults.defaultClassTimeDto) }
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.lecture_detail_add_class_time),
                                textAlign = TextAlign.Center,
                                style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                            )
                        }
                    }
                    Margin(height = 7.dp)
                }
                AnimatedVisibility(
                    visible = editMode.not()
                ) {
                    Column {
                        Margin(height = 30.dp)
                        Box(modifier = Modifier.background(Color.White)) {
                            LectureDetailButton(
                                title = stringResource(R.string.lecture_detail_delete_button),
                                textStyle = SNUTTTypography.body1.copy(
                                    fontSize = 15.sp,
                                    color = SNUTTColors.Red
                                )
                            ) {
                                deleteLectureDialogState = true
                            }
                        }
                    }
                }
                Margin(height = 30.dp)
            }
        }
    }

    // 강의 삭제 다이얼로그
    if (deleteLectureDialogState) {
        CustomDialog(
            onDismiss = { deleteLectureDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        vm.removeLecture2()
                        scope.launch(Dispatchers.Main) {
                            navController.popBackStack()
                        }
                        deleteLectureDialogState = false
                    }
                }
            },
            title = stringResource(R.string.lecture_detail_delete_dialog_title)
        ) {
            Text(
                text = stringResource(R.string.lecture_detail_delete_dialog_message),
                style = SNUTTTypography.body2
            )
        }
    }

    // 편집 취소하고 나가기 다이얼로그
    if (editExitDialogState) {
        CustomDialog(
            onDismiss = { editExitDialogState = false },
            onConfirm = {
                vm.abandonEditingLectureDetail()
                editExitDialogState = false
                vm.unsetEditMode()
            },
            title = stringResource(R.string.lecture_detail_exit_edit_dialog_message)
        ) {}
    }

    // 강의 시간대 삭제 다이얼로그
    if (deleteTimeDialogState) {
        CustomDialog(
            onDismiss = { deleteTimeDialogState = false },
            onConfirm = {
                vm.editEditingLectureDetail(
                    editingLectureDetail.copy(
                        class_time_json = editingLectureDetail.class_time_json.toMutableList()
                            .also {
                                it.removeAt(editingClassTimeIndex)
                            }
                    )
                )
                deleteTimeDialogState = false
            },
            title = stringResource(R.string.lecture_detail_delete_class_time_message)
        ) {}
    }

    // 강의 겹침 다이얼로그
    if (lectureOverlapDialogState) {
        CustomDialog(
            onDismiss = { lectureOverlapDialogState = false },
            onConfirm = {
                scope.launch {
                    if (vm.isAddMode()) {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                vm.createLecture2(is_forced = true)
                                vm.unsetEditMode()
                                vm.setAddMode(false)
                                scope.launch(Dispatchers.Main) { navController.popBackStack() }
                            }
                        }
                    } else {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                vm.updateLecture2(is_forced = true)
                                vm.initializeEditingLectureDetail(editingLectureDetail)
                                vm.unsetEditMode()
                            }
                        }
                    }
                    lectureOverlapDialogState = false
                }
            },
            title = stringResource(id = R.string.lecture_overlap_error_message)
        ) {
            Text(text = lectureOverlapDialogMessage, style = SNUTTTypography.body2)
        }
    }
}

@Composable
fun DayTimePickerSheet(
    classTime: ClassTimeDto,
    onDismiss: () -> Unit,
    onConfirm: (ClassTimeDto) -> Unit,
) {
    val modalState = LocalModalState.current
    val context = LocalContext.current
    val dayList = remember { context.resources.getStringArray(R.array.week_days).map { it + "요일" } }
    var dayIndex by remember { mutableStateOf(classTime.day) }

    val amPmList = remember { listOf("오전", "오후") }
    val hourList = remember { List(12) { if (it == 0) "12" else it.toString() } }
    val minuteList = remember { List(12) { "%02d".format(it * 5) } }

    var startTime: Time12 by remember { mutableStateOf(classTime.startTime12()) }
    var endTime: Time12 by remember { mutableStateOf(classTime.endTime12()) }

    var editingStartTime by remember { mutableStateOf(false) }
    var editingEndTime by remember { mutableStateOf(false) }

    /* 시작 시간이 끝나는 시간보다 같거나 더 나중일 때, 경계값 신경써서 조정하는 함수 */
    val checkBoundary = {
        if (startTime >= endTime) {
            // 시작 시간을 끝나는 시간보다 나중으로 수정했으면, 끝나는 시간을 5분 뒤로 설정
            if (editingStartTime) {
                if (startTime.isLast()) {
                    startTime = startTime.prev()
                    endTime = startTime.next()
                } else endTime = startTime.next()
                // 끝나는 시간을 시작 시간보다 앞서게 수정했으면, 시작 시간을 5분 앞으로 설정
            } else if (editingEndTime) {
                if (endTime.isFirst()) {
                    endTime = endTime.next()
                    startTime = endTime.prev()
                } else startTime = endTime.prev()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(15.dp)
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks { onDismiss() },
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(
                        classTime.copy(
                            day = dayIndex,
                            start_time = startTime.toString24(),
                            end_time = endTime.toString24(),
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "요일", style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempDayIndex by mutableStateOf(dayIndex)
                    modalState
                        .set(
                            onDismiss = {
                                dayIndex = tempDayIndex
                                modalState.hide()
                            },
                            width = 150.dp,
                        ) {
                            Picker(
                                list = dayList,
                                initialCenterIndex = dayIndex,
                                columnHeightDp = 45.dp,
                                onValueChanged = { tempDayIndex = it }
                            ) {
                                Text(
                                    text = dayList[it],
                                    style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                )
                            }
                        }.show()
                },
            ) {
                Text(text = dayList[dayIndex], style = SNUTTTypography.button)
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "시작", style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempStartTime by mutableStateOf(startTime.copy())
                    editingStartTime = true
                    modalState
                        .set(
                            onDismiss = {
                                startTime = tempStartTime
                                checkBoundary()
                                editingStartTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = startTime.amPm,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(amPm = it)
                                        }
                                    ) {
                                        Text(
                                            text = amPmList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = hourList,
                                        initialCenterIndex = startTime.hour,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(hour = it)
                                        }
                                    ) {
                                        Text(
                                            text = hourList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = minuteList,
                                        initialCenterIndex = startTime.minute / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(minute = it * 5)
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            ) {
                Text(
                    text = startTime.toString(),
                    style = SNUTTTypography.button
                )
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "종료", style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempEndTime by mutableStateOf(endTime.copy())
                    editingEndTime = true
                    modalState
                        .set(
                            onDismiss = {
                                endTime = tempEndTime
                                checkBoundary()
                                editingEndTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = endTime.amPm,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(amPm = it)
                                        }
                                    ) {
                                        Text(
                                            text = amPmList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = hourList,
                                        initialCenterIndex = endTime.hour,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(hour = it)
                                        }
                                    ) {
                                        Text(
                                            text = hourList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = minuteList,
                                        initialCenterIndex = endTime.minute / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(minute = it * 5)
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            ) {
                Text(
                    text = endTime.toString(),
                    style = SNUTTTypography.button
                )
            }
        }
    }
}
