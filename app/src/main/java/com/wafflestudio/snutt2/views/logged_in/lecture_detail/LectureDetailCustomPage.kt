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
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LectureDetailCustomPage() {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = NavControllerContext.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModel>(backStackEntry)

    val editMode by vm.editMode.collectAsState()
    val lectureState by vm.selectedLectureFlow.collectAsState()
    val theme = vm.colorTheme ?: TimetableColorTheme.SNUTT

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    if (deleteLectureDialogState) {
        CustomDialog(
            onDismiss = { deleteLectureDialogState = false },
            onConfirm = {
                vm.removeLecture2()
                    .subscribeBy(
                        onError = {},
                        onComplete = {
                            // main thread 에서 navigate 해야 한다고 함.
                            scope.launch(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                            deleteLectureDialogState = false
                        }
                    )
            },
            title = "강좌 삭제"
        ) {
            Text(text = "강좌를 삭제하시겠습니까?")
        }
    }

    var editExitDialogState by remember { mutableStateOf(false) }
    if (editExitDialogState) {
        CustomDialog(
            onDismiss = { editExitDialogState = false },
            onConfirm = {
                vm.abandonEditingSelectedLectureFlow()
                editExitDialogState = false
                vm.unsetEditMode()
            },
            title = "편집을 취소하시겠습니까?"
        ) {}
    }
    // 편집 모드에서 뒤로가기 누르면 편집 취소 dialog 띄우기
    // 변경점(개선점): 기존 앱은 여기서 확인 누르면 아예 detailFragment 에서 나가버려서 불편했다.
    BackHandler(enabled = editMode) {
        if (vm.isAddMode()) navController.popBackStack() // 새 커스텀 강의 추가일 때는 뒤로가기 하면 바로 나가기
        else editExitDialogState = true
    }

    var editTimeDialogState by remember { mutableStateOf(false) }
    var editingClassTimeIndex by remember { mutableStateOf(0) }
    if (editTimeDialogState) {
        DayAndTimeSelectorDialog(
            classTime = lectureState.class_time_json[editingClassTimeIndex],
            onDismiss = { editTimeDialogState = false },
            onConfirm = { editedClassTime ->
                vm.editSelectedLectureFlow(
                    lectureState.copy(
                        class_time_json =
                        lectureState.class_time_json.toMutableList().also {
                            it[editingClassTimeIndex] = editedClassTime
                        }
                    )
                )
                editTimeDialogState = false
            }
        )
    }

    var deleteTimeDialogState by remember { mutableStateOf(false) }
    if (deleteTimeDialogState) {
        CustomDialog(
            onDismiss = { deleteTimeDialogState = false },
            onConfirm = {
                vm.editSelectedLectureFlow(
                    lectureState.copy(
                        class_time_json =
                        lectureState.class_time_json.toMutableList().also {
                            it.removeAt(editingClassTimeIndex)
                        }
                    )
                )
                deleteTimeDialogState = false
            },
            title = "시간을 삭제하시겠습니까?"
        ) {}
    }

    /* TODO (진행중)
     * 시간 및 장소 item 추가했을 때 애니메이션 적용하기 (LazyColumn 의 기능 모방)
     * 추가시 애니메이션은 되는데 삭제시는 방법을 고민중
     */
    var classTimeAnimationState by remember {
        mutableStateOf(MutableTransitionState(true))
    }

    // TODO: 각각 필드 비어있으면 회색 hint 적용 (editText 개선 후)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff2f2f2)) // TODO: Color
    ) {
        TopAppBar(
            title = {
                Text(text = "강의 상세 보기")
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier.clicks {
                        // '직접 강좌 추가하기' 로 진입했으면 < 아이콘 누를 때 바로 pop
                        if (vm.isAddMode()) {
                            vm.setAddMode(false)
                            vm.unsetEditMode()
                        } else {
                            if (editMode) editExitDialogState = true
                            else navController.popBackStack()
                        }
                    }
                )
            },
            actions = {
                Text(
                    text = if (editMode) "완료" else "편집",
                    modifier = Modifier
                        .clicks {
                            if (editMode.not()) vm.setEditMode()
                            else {
                                if (vm.isAddMode()) {
                                    vm
                                        .createLecture2()
                                        .subscribeBy(
                                            onError = {},
                                            onSuccess = {
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

                                                vm.unsetEditMode()
                                                vm.setAddMode(false)
                                                scope.launch(Dispatchers.Main) { navController.popBackStack() }
                                            }
                                        )
                                } else {
                                    vm
                                        .updateLecture2()
                                        .subscribeBy(
                                            onError = {}, // TODO: onApiError
                                            onSuccess = {
                                                vm.initializeSelectedLectureFlow(lectureState)
                                                vm.unsetEditMode()
                                            }
                                        )
                                }
                            }
                        }
                        .padding(horizontal = 10.dp)
                )
            }
        )
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Margin(height = 10.dp)
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                LectureDetailItem(title = "강의명") {
                    EditText(
                        value = lectureState.course_title,
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(course_title = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "교수") {
                    EditText(
                        value = lectureState.instructor,
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(instructor = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(
                    title = "색상",
                    modifier = Modifier.clicks {
                        if (editMode) {
                            navController.navigate(NavigationDestination.LectureColorSelector)
                        }
                    }
                ) {
                    Row {
                        ColorBox(lectureState.colorIndex, lectureState.color, theme)
                        Spacer(modifier = Modifier.weight(1f))
                        AnimatedVisibility(visible = editMode) {
                            ArrowRight(modifier = Modifier.size(16.dp))
                        }
                    }
                }
                LectureDetailItem(title = "학점") {
                    EditText(
                        value = lectureState.credit.toString(),
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(credit = it.stringToLong()))
                        },
                        enabled = editMode,
                        keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Margin(height = 4.dp)
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                LectureDetailRemark(title = "비고") { //  TODO: movementMethod 는 뭘까
                    EditText(
                        value = lectureState.remark,
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(remark = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(start = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "시간 및 장소")
                }
                lectureState.class_time_json.forEachIndexed { idx, classTime ->
                    // TODO: stringUtil 정리
                    val time = SNUTTUtils.numberToWday(classTime.day) + " " +
                        SNUTTUtils.numberToTime(classTime.start) + "~" +
                        SNUTTUtils.numberToTime(classTime.start + classTime.len)

                    LectureDetailTimeAndLocation(
                        timeText = time,
                        locationText = classTime.place,
                        editTime = {
                            editingClassTimeIndex = idx
                            editTimeDialogState = true
                        },
                        onLocationTextChange = { changedLocation ->
                            vm.editSelectedLectureFlow(
                                lectureState.copy(
                                    class_time_json = lectureState.class_time_json.toMutableList()
                                        .also { it[idx] = classTime.copy(place = changedLocation) }
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
                        visible = if (idx == lectureState.class_time_json.lastIndex) classTimeAnimationState
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
                                vm.editSelectedLectureFlow(
                                    lectureState.copy(
                                        class_time_json =
                                        lectureState.class_time_json
                                            .toMutableList()
                                            .also { it.add(Defaults.defaultClassTimeDto) }
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+ 시간 및 장소 추가", textAlign = TextAlign.Center)
                    }
                }
            }
            AnimatedVisibility(
                visible = editMode.not()
            ) {
                Column {
                    Margin(height = 30.dp)
                    Box(modifier = Modifier.background(Color.White)) {
                        LectureDetailButton(title = "삭제") {
                            deleteLectureDialogState = true
                        }
                    }
                }
            }
            Margin(height = 30.dp)
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
                    day = day,
                    start = startTime - 8f, // TODO: 24시간 개선시 반영
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
                    initialValue = timeStringAtIndex(indexOf(startTime)),
                    onValueChanged = {
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
                    initialValue = timeStringAtIndex(indexOf(endTime)),
                    onValueChanged = {
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
