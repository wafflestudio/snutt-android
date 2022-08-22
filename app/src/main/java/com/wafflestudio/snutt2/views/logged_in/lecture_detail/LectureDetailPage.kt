@file:OptIn(ExperimentalPagerApi::class)

package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.views.HomePageController
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.ReviewUrlController
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.ReviewPage
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LectureDetailPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = NavControllerContext.current
    val reviewUrlController = ReviewUrlController.current
    val homePageController = HomePageController.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModel>(backStackEntry)

    // TODO: 각각 필드 비어있으면 회색 hint 적용 (editText 개선 후)

    // ColorSelector 갔다 와도 유지돼야 해서 rememberSaveable 사용 (그냥 이것도 viewModel 로?)
    var editMode by rememberSaveable { mutableStateOf(false) }
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
                            // main thread에서 navigate해야 한다고 함.
                            scope.launch(Dispatchers.Main) {
                                navController.navigate(NavigationDestination.Home)
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

    var resetLectureDialogState by remember { mutableStateOf(false) }
    if (resetLectureDialogState) {
        CustomDialog(
            onDismiss = { resetLectureDialogState = false },
            onConfirm = {
                vm.resetLecture2()
                    .subscribeBy(
                        onError = {},
                        onSuccess = {
                            vm.initializeSelectedLectureFlow(it.lectureList.find { it.id == lectureState.id })
                            editMode = false
                            resetLectureDialogState = false
                        }
                    )
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
                vm.abandonEditedSelectedLectureFlow()
                editExitDialogState = false
                editMode = false
            },
            title = "편집을 취소하시겠습니까?"
        ) {}
    }
    // 편집 모드에서 뒤로가기 누르면 편집 취소 dialog 띄우기
    // 변경점(개선점): 기존 앱은 여기서 확인 누르면 아예 detailFragment 에서 나가버려서 불편했다.
    BackHandler(enabled = editMode) {
        editExitDialogState = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = "강의 상세 보기")
            },
            navigationIcon = {
                ArrowBackIcon()
            },
            actions = {
                Text(
                    text = if (editMode) "완료" else "편집",
                    modifier = Modifier
                        .clicks {
                            if (editMode.not()) editMode = true
                            else {
                                vm
                                    .updateLecture2()
                                    .subscribeBy(
                                        onError = {}, // TODO: onApiError
                                        onSuccess = {
                                            vm.initializeSelectedLectureFlow(lectureState)
                                            editMode = false
                                        }
                                    )
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
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
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
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                LectureDetailItem(title = "학과") {
                    EditText(
                        value = lectureState.department ?: "",
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(department = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학년") {
                    EditText(
                        value = lectureState.academic_year ?: "",
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(academic_year = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학점") {
                    EditText(
                        value = lectureState.credit.toString(),
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(credit = it.toLong()))
                        },
                        enabled = editMode,
                        keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "분류") {
                    EditText(
                        value = lectureState.classification ?: "",
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(classification = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "구분") {
                    EditText(
                        value = lectureState.category ?: "",
                        onValueChange = {
                            vm.editSelectedLectureFlow(lectureState.copy(category = it))
                        },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                LectureDetailItem(title = "강좌번호") { // TODO: editMode에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = lectureState.course_number ?: "")
                }
                LectureDetailItem(title = "분반번호") { // TODO: editMode에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = lectureState.lecture_number ?: "")
                }
                LectureDetailItem(title = "인원") {
                    Text(text = "TODO")
                }
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
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "시간 및 장소")
                }
                lectureState.class_time_json.forEachIndexed { idx, classTime ->
                    // TODO: stringUtil 정리
                    val time = SNUTTUtils.numberToWday(classTime.day) + " " +
                        SNUTTUtils.numberToTime(classTime.start) + "~" +
                        SNUTTUtils.numberToEndTimeAdjusted(classTime.start, classTime.len)

                    LectureDetailTimeAndLocation( // TODO: Long click (커스텀에서만)
                        time = time,
                        location = classTime.place,
                        onLocationTextChange = {
                            vm.editSelectedLectureFlow(
                                // 서버 강의는 시간대 삭제가 안 되니까 idx 그대로 갖다 써도 된다. (FIXME: 추후 변경 가능)
                                lectureState.copy(
                                    class_time_json = lectureState.class_time_json.toMutableList()
                                        .apply {
                                            this[idx] = classTime.copy(place = it)
                                        }
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
                    LectureDetailButton(title = "강의계획서") {
                        vm.getCourseBookUrl()
                            .subscribeBy(
                                onError = {}, // TODO: apiOnError
                                onSuccess = { result ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                                    context.startActivity(intent)
                                }
                            )
                    }
                    LectureDetailButton(title = "강의평") {
                        vm.getReviewContentsUrl() // FIXME: staging 에서는 원래 403 에러가 나는가?
                            .subscribeBy(
                                onError = {},
                                onSuccess = {
                                    navController.popBackStack()
                                    reviewUrlController.update(it) // TODO: ReviewPage 만들 때 재확인
                                    scope.launch {
                                        homePageController.animateScrollToPage(ReviewPage)
                                    }
                                }
                            )
                    }
                }
            }
            Margin(height = 10.dp)
            LectureDetailButton(title = if (editMode) "초기화" else "삭제") {
                if (editMode) resetLectureDialogState = true
                else deleteLectureDialogState = true
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
    time: String,
    location: String,
    onLocationTextChange: (String) -> Unit,
    editMode: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
                Text(text = "시간", modifier = Modifier.width(76.dp))
                Text(text = time) // TODO : editMode에 따라 색 변경 (편집 불가)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "장소", modifier = Modifier.width(76.dp))
                EditText(
                    value = location,
                    onValueChange = onLocationTextChange,
                    enabled = editMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // TODO: 이건 커스텀 디테일에만 들어간다
//        AnimatedVisibility(visible = editMode) {
//            Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.Center) {
//                TipCloseIcon(Modifier.size(16.dp))
//            }
//        }
    }
}

@Composable
private fun Margin(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(Color(0xfff2f2f2)) // TODO: Color 정리
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
private fun String.stringToLong(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        0
    }
}

// class LectureDtoNavType : NavType<LectureDto>(isNullableAllowed = false) {
//    override fun get(bundle: Bundle, key: String): LectureDto? {
//        return bundle.getParcelable(key)
//    }
//
//    override fun parseValue(value: String): LectureDto {
//        return Gson().fromJson(value, LectureDto::class.java)
//    }
//
//    override fun put(bundle: Bundle, key: String, value: LectureDto) {
//        bundle.putParcelable(key, value)
//    }
// }
