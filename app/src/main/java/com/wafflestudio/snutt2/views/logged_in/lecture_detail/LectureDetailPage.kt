package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LectureDetailPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = NavControllerContext.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModelNew>(backStackEntry)

    // TODO: 각각 필드 비어있으면 회색 hint 적용 (editText 개선 후)

    val editMode by vm.editMode.collectAsState()
    val editingLectureDetail by vm.editingLectureDetail.collectAsState()
    val theme = vm.currentTable.collectAsState()

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    if (deleteLectureDialogState) {
        CustomDialog(
            onDismiss = { deleteLectureDialogState = false },
            onConfirm = {
                scope.launch {
                    vm.removeLecture2()
                    scope.launch(Dispatchers.Main) {
                        navController.popBackStack()
                    }
                    deleteLectureDialogState = false
                }
            },
            title = "강좌 삭제"
        ) {
            Text(text = "강좌를 삭제하시겠습니까?")
        }
    }

    var resetLectureDialogState by remember { mutableStateOf(false) }
    if (resetLectureDialogState) {
        CustomDialog(
            onDismiss = { resetLectureDialogState = false },
            onConfirm = {
                scope.launch {
                    val resetLecture = vm.resetLecture2()
                    vm.initializeEditingLectureDetail(resetLecture)
                    vm.unsetEditMode()
                    resetLectureDialogState = false
                }
            },
            title = "강좌 초기화"
        ) {
            Text(text = "강좌를 원래 상태로 초기화하시겠습니까?")
        }
    }

    var editExitDialogState by remember { mutableStateOf(false) }
    if (editExitDialogState) {
        CustomDialog(
            onDismiss = { editExitDialogState = false },
            onConfirm = {
                vm.abandonEditingLectureDetail()
                editExitDialogState = false
                vm.unsetEditMode()
            },
            title = "편집을 취소하시겠습니까?"
        ) {}
    }
    // 편집 모드에서 뒤로가기 누르면 편집 취소 dialog 띄우기
    // 변경점(개선점): 기존 앱은 여기서 확인 누르면 아예 detailFragment 에서 나가버려서 불편했다.
    BackHandler(enabled = editMode) {
        editExitDialogState = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff2f2f2)) // TODO: color
    ) {
        TopAppBar(
            title = {
                Text(text = "강의 상세 보기")
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier.clicks {
                        if (editMode) editExitDialogState = true
                        else navController.popBackStack()
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
                                scope.launch {
                                    vm.updateLecture2()
                                    vm.initializeEditingLectureDetail(editingLectureDetail)
                                    vm.unsetEditMode()
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
                        value = editingLectureDetail.course_title,
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(course_title = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "교수") {
                    EditText(
                        value = editingLectureDetail.instructor,
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(instructor = it))
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
                        ColorBox(
                            editingLectureDetail.colorIndex,
                            editingLectureDetail.color,
                            theme.value.theme
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        AnimatedVisibility(visible = editMode) {
                            ArrowRight(modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Margin(height = 4.dp)
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                LectureDetailItem(title = "학과") {
                    EditText(
                        value = editingLectureDetail.department ?: "",
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(department = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학년") {
                    EditText(
                        value = editingLectureDetail.academic_year ?: "",
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(academic_year = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학점") {
                    EditText(
                        value = editingLectureDetail.credit.toString(),
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(credit = it.stringToLong()))
                        },
                        enabled = editMode,
                        keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "분류") {
                    EditText(
                        value = editingLectureDetail.classification ?: "",
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(classification = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "구분") {
                    EditText(
                        value = editingLectureDetail.category ?: "",
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(category = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                LectureDetailItem(title = "강좌번호") { // TODO: editMode 에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = editingLectureDetail.course_number ?: "")
                }
                LectureDetailItem(title = "분반번호") { // TODO: editMode 에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = editingLectureDetail.lecture_number ?: "")
                }
                LectureDetailItem(title = "인원") {
                    Text(text = "TODO")
                }
                LectureDetailRemark(title = "비고") { //  TODO: movementMethod 는 뭘까
                    EditText(
                        value = editingLectureDetail.remark,
                        onValueChange = {
                            vm.editEditingLectureDetail(editingLectureDetail.copy(remark = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Margin(height = 4.dp)
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.background(Color.White)) {
                Margin(height = 4.dp)
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "시간 및 장소")
                }
                editingLectureDetail.class_time_json.forEachIndexed { idx, classTime ->
                    // TODO: stringUtil 정리
                    val time = SNUTTUtils.numberToWday(classTime.day) + " " +
                        SNUTTUtils.numberToTime(classTime.start) + "~" +
                        SNUTTUtils.numberToEndTimeAdjusted(classTime.start, classTime.len)

                    LectureDetailTimeAndLocation(
                        timeText = time,
                        locationText = classTime.place,
                        onLocationTextChange = { changedLocation ->
                            vm.editEditingLectureDetail(
                                editingLectureDetail.copy(
                                    class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                        .also { it[idx] = classTime.copy(place = changedLocation) }
                                )
                            )
                        },
                        editMode = editMode
                    )
                }
            }
            AnimatedVisibility(
                visible = editMode.not()
            ) {
                Column {
                    Margin(height = 30.dp)
                    Column(modifier = Modifier.background(Color.White)) {
                        LectureDetailButton(title = "강의계획서") {
                            scope.launch {
                                vm.getCourseBookUrl().let { url ->
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            }
                        }
                        LectureDetailButton(title = "강의평") {
                            scope.launch {
                                vm.getReviewContentsUrl().let {
                                    // 강의평 쪽 api 403 난다
                                }
                            }
                        }
                    }
                }
            }
            Margin(height = 10.dp)
            Box(modifier = Modifier.background(Color.White)) {
                LectureDetailButton(title = if (editMode) "초기화" else "삭제") {
                    if (editMode) resetLectureDialogState = true
                    else deleteLectureDialogState = true
                }
            }
            Margin(height = 30.dp)
        }
    }
}

@Composable
fun LectureDetailItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title, modifier = Modifier.width(76.dp))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
fun LectureDetailRemark(
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .padding(vertical = 10.dp)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title, modifier = Modifier.width(76.dp))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
fun LectureDetailButton(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clicks { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}

@Composable
fun LectureDetailTimeAndLocation(
    timeText: String,
    locationText: String,
    editTime: () -> Unit = {},
    onLocationTextChange: (String) -> Unit,
    onClickDeleteIcon: () -> Unit = {},
    onLongClick: () -> Unit = {},
    editMode: Boolean,
    isCustom: Boolean = false,
    visible: MutableTransitionState<Boolean> = MutableTransitionState(true)
) {
    AnimatedVisibility(
        visibleState = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    // TODO: 중요하지 않은 기능이지만.. 하위 editText의 clicks{} 가 상위의 tap event를 가로챈다.
                    detectTapGestures(onLongPress = { onLongClick() })
                }
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "시간",
                        modifier = Modifier.width(76.dp)
                    )
                    Text(
                        text = timeText,
                        modifier = Modifier // TODO : editMode 에 따라 색 변경 (편집 불가)
                            .fillMaxWidth()
                            .clicks {
                                if (editMode && isCustom) editTime()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "장소", modifier = Modifier.width(76.dp))
                    EditText(
                        value = locationText,
                        onValueChange = onLocationTextChange,
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (isCustom) {
                AnimatedVisibility(visible = editMode) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .clicks { onClickDeleteIcon() },
                        contentAlignment = Alignment.Center
                    ) {
                        TipCloseIcon(Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 별도의 폴더(component 패키지)로?
@Composable
fun Margin(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
fun ColorBox(
    lectureColorIndex: Long,
    lectureColor: ColorDto?, // null 이면 반드시 기존 테마.
    theme: TimetableColorTheme
) {
    Row(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp)
            .zIndex(1f)
            .border(width = (0.5f).dp, color = Color(0x26000000)) // TODO: Color
    ) {
        Box(
            modifier = Modifier
                .background(
                    // colorIndex == 0 이면 사용자 커스텀 색
                    // colorIndex > 0 이면 bgColor 는 스누티티 지정 테마 색깔, fgColor = -0x1 (디폴트 흰색)
                    if ((lectureColorIndex) > 0) Color(-0x1)
                    // 커스텀 fg 색이면 null 이 오지 않아서 원래는 !! 처리했지만..
                    else Color(lectureColor?.fgColor ?: -0x1)
                )
                .size(20.dp)
        )
        Box(
            modifier = Modifier
                .background(
                    // index > 0 : 스누티티 지정 테마 색깔.
                    if (lectureColorIndex > 0)
                        theme.getColorByIndexComposable(lectureColorIndex)
                    // 사용자 지정 bgColor, 역시 이때는 null이 오지 않아서 !! 처리를 했었다. 그냥 !! 해도 될지도
                    else Color(lectureColor?.bgColor ?: (-0x1))
                )
                .size(20.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 880)
@Composable
fun LectureDetailPagePreview() {
    LectureDetailPage()
}

// TODO: StringUtil 로 이동?
fun String.stringToLong(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        0
    }
}
