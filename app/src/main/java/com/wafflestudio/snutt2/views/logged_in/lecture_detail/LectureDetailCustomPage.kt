package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
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
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.search.lectureApiWithOverlapDialog
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LectureDetailCustomPage() {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = LocalNavController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

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
    var editTimeDialogState by remember { mutableStateOf(false) }
    var editingClassTimeIndex by remember { mutableStateOf(0) }
    var deleteTimeDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogMessage by remember { mutableStateOf("") }
    BackHandler(enabled = editMode) {
        if (vm.isAddMode()) navController.popBackStack() // 새 커스텀 강의 추가일 때는 뒤로가기 하면 바로 나가기
        else editExitDialogState = true
    }

    /* TODO (진행중)
     * 시간 및 장소 item 추가했을 때 애니메이션 적용하기 (LazyColumn 의 기능 모방)
     * 추가시 애니메이션은 되는데 삭제시는 방법을 고민중
     */
    var classTimeAnimationState by remember {
        mutableStateOf(MutableTransitionState(true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.Gray100)
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
                    }
                )
            }, actions = {
            Text(
                text = if (editMode) stringResource(R.string.lecture_detail_top_bar_complete)
                else stringResource(R.string.lecture_detail_top_bar_edit),
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
                                        vm.initializeEditingLectureDetail(editingLectureDetail)
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
                            ArrowRight(modifier = Modifier.size(16.dp))
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
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                LectureDetailRemark(title = stringResource(R.string.lecture_detail_remark)) {
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
            Column(modifier = Modifier.background(Color.White)) {
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
                            editingClassTimeIndex = idx
                            editTimeDialogState = true
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
            Text(text = stringResource(R.string.lecture_detail_delete_dialog_message))
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

    // 강의 시간대 편집 다이얼로그
    if (editTimeDialogState) {
        DayAndTimeSelectorDialog(
            classTime = editingLectureDetail.class_time_json[editingClassTimeIndex],
            onDismiss = { editTimeDialogState = false },
            onConfirm = { editedClassTime ->
                vm.editEditingLectureDetail(
                    editingLectureDetail.copy(
                        class_time_json = editingLectureDetail.class_time_json.toMutableList()
                            .also {
                                it[editingClassTimeIndex] = editedClassTime
                            }
                    )
                )
                editTimeDialogState = false
            }
        )
    }

    // 강의 시간대 삭제 다이얼로그
    if (deleteTimeDialogState) {
        CustomDialog(
            onDismiss = { deleteTimeDialogState = false }, onConfirm = {
            vm.editEditingLectureDetail(
                editingLectureDetail.copy(
                    class_time_json = editingLectureDetail.class_time_json.toMutableList()
                        .also {
                            it.removeAt(editingClassTimeIndex)
                        }
                )
            )
            deleteTimeDialogState = false
        }, title = stringResource(R.string.lecture_detail_delete_class_time_message)
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
                Text(text = lectureOverlapDialogMessage)
            }
        }
    }

    @Composable
    fun DayAndTimeSelectorDialog(
        classTime: ClassTimeDto,
        onDismiss: () -> Unit,
        onConfirm: (ClassTimeDto) -> Unit,
    ) {
        val weekDayList = stringArrayResource(R.array.week_days).toList()
        val timeList = stringArrayResource(R.array.time_string).toList()

        fun indexOf(time: Float): Int {
            // TODO: 시간 최소 단위를 바꾼다면(현재 30분) 변경
            return (time * 2).roundToInt()
        }

        fun timeStringAtIndex(idx: Int): String {
            return timeList[idx.coerceIn(0, timeList.size - 1)]
        }

        // 시작 시간에 따라 끝나는 시간 범위가 유동적으로 recompose 되어야 한다.
        var startTime by remember {
            // 현재 서버에서는 start=0 이 8시이다. TODO: 24시간 개선시 변경
            mutableStateOf(classTime.start + 8f)
        }
        var endTime by remember {
            // 현재 서버에서는 start=0 이 8시이다. TODO: 24시간 개선시 변경
            mutableStateOf(classTime.start + classTime.len + 8f)
        }
        var day by remember { mutableStateOf(classTime.day) }

        CustomDialog(
            onDismiss = onDismiss,
            onConfirm = {
                onConfirm(
                    classTime.copy(
                        day = day, start = startTime - 8f, // TODO: 24시간 개선시 반영
                        len = endTime - startTime
                    )
                )
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    Picker(
                        list = weekDayList,
                        initialValue = weekDayList[day],
                        onValueChanged = { day = it }
                    ) {
                        Text(text = weekDayList[it])
                    }
                }
                Box(modifier = Modifier.weight(2f)) {
                    Picker(
                        list = timeList.subList(
                            indexOf(8f), // 아직 시작 시간은 8시가 하한     TODO: 24시간 개선시 변경
                            indexOf(24f) // 시작 시간은 24:00 선택 불가
                        ),
                        initialValue = timeStringAtIndex(indexOf(startTime)), onValueChanged = {
                            // 콜백되는 인덱스는 08시가 index 0인 리스트 기준
                            startTime = (
                                it / 2f // TODO: 시간 최소 단위를 바꾼다면(현재 30분) 변경
                                ) + 8f // TODO: 24시간 개선시 변경
                            if (endTime <= startTime) endTime =
                                startTime + 0.5f // TODO: 시간 최소 단위 바꾸면 변경
                        }
                    ) {
                        // 콜백되는 인덱스는 08시가 index 0인 리스트 기준       TODO: 24시간 개선시 변경
                        Text(text = timeStringAtIndex(it + indexOf(8f)))
                    }
                }
                Box(modifier = Modifier.weight(2f)) {
                    Picker(
                        list = timeList.subList(
                            // 선택할 수 있는 가장 이른 end는 start 30분 뒤
                            indexOf(startTime + 0.5f), // TODO: 시간 최소 단위 바꾸면 변경
                            timeList.size
                        ),
                        initialValue = timeStringAtIndex(indexOf(endTime)), onValueChanged = {
                            // 콜백되는 인덱스는 (startTime+0.5f)가 index 0인 리스트 기준
                            endTime = (it / 2f) + startTime + 0.5f // TODO: 시간 최소 단위 바꾸면 변경
                        }
                    ) {
                        // 콜백되는 인덱스는 (startTime+0.5f)가 index 0인 리스트 기준
                        Text(text = timeStringAtIndex(it + indexOf(startTime + 0.5f))) // TODO: 시간 최소 단위 바꾸면 변경
                    }
                }
            }
        }
    }
    