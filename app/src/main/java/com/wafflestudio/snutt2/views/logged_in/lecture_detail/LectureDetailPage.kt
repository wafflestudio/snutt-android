@file:OptIn(ExperimentalPagerApi::class)

package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.HomePageController
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.ReviewUrlController
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.ReviewPage
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch

@Composable
fun LectureDetailPage(lecture: LectureDto?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val vm = hiltViewModel<LectureDetailViewModel>()

    val navController = NavControllerContext.current
    val reviewUrlController = ReviewUrlController.current
    val homePageController = HomePageController.current

    var editMode by remember { mutableStateOf(false) }

    var lectureTitle by remember { mutableStateOf(lecture?.course_title) }
    var lectureInstructor by remember { mutableStateOf(lecture?.instructor) }
//    var lectureColor by remember { mutableStateOf(lecture?.color) }
    var lectureDepartment by remember { mutableStateOf(lecture?.department) }
    var lectureAcademicYear by remember { mutableStateOf(lecture?.academic_year) }
    var lectureCredit by remember { mutableStateOf(lecture?.credit) }
    var lectureClassification by remember { mutableStateOf(lecture?.classification) }
    var lectureRemark by remember { mutableStateOf(lecture?.remark) }

    LaunchedEffect(Unit) {
        vm.setLecture(lecture)
    }

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    if(deleteLectureDialogState) {
        CustomDialog(
            onDismiss = { deleteLectureDialogState = false },
            onConfirm = { /*TODO*/ },
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
                            editMode = editMode.not()
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
                        value = lectureTitle ?: "",
                        onValueChange = { lectureTitle = it },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                        hint = "예) 기초 영어"
                    )
                }
                LectureDetailItem(title = "교수") {
                    EditText(
                        value = lectureInstructor ?: "",
                        onValueChange = { lectureInstructor = it },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "색상") {

                }
            }
            Margin(height = 10.dp)
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                LectureDetailItem(title = "학과") {
                    EditText(
                        value = lectureDepartment ?: "",
                        onValueChange = { lectureDepartment = it },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학년") {
                    EditText(
                        value = lectureAcademicYear ?: "",
                        onValueChange = { lectureAcademicYear = it },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "학점") {
                    EditText(
                        value = lectureCredit.toString(),
                        onValueChange = { lectureCredit = it.stringToLong() },
                        enabled = editMode,
                        keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "분류") {
                    EditText(
                        value = lectureClassification ?: "",
                        onValueChange = { lectureClassification = it },
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LectureDetailItem(title = "구분") {
                    Text(text = lecture?.category ?: "(없음)")
                }
                LectureDetailItem(title = "강좌번호") {                 // TODO: editMode에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = lecture?.course_number ?: "")
                }
                LectureDetailItem(title = "분반번호") {                 // TODO: editMode에 따라 색 변경 로직 추가 (편집 불가)
                    Text(text = lecture?.lecture_number ?: "")
                }
                LectureDetailItem(title = "인원") {
                    Text(text = "TODO")
                }
                LectureDetailRemark(title = "비고") {                 //  TODO: movementMethod 는 뭘까
                    EditText(
                        value = lectureRemark ?: "",
                        onValueChange = { lectureRemark = it },
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
                lecture?.class_time_json?.forEach { it ->
                    val time = SNUTTUtils.numberToWday(it.day) + " " +
                        SNUTTUtils.numberToTime(it.start) + "~" +
                        SNUTTUtils.numberToEndTimeAdjusted(it.start, it.len)

                    LectureDetailTimeAndLocation(           // TODO: Long click
                        time = time,
                        location = it.place,
                        onTimeTextChange = {},
                        onLocationTextChange = {},
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
                                onError = {},    // TODO: apiOnError
                                onSuccess = { result ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                                    context.startActivity(intent)
                                }
                            )

                    }
                    LectureDetailButton(title = "강의평") {
                        vm.getReviewContentsUrl()           // FIXME: staging 에서는 원래 403 에러가 나는가?
                            .subscribeBy(
                                onError = {},
                                onSuccess = {
                                    navController.popBackStack()
                                    reviewUrlController.update(it)      // TODO: ReviewPage 만들 때 재확인
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

            }
            Margin(height = 30.dp)
        }
    }
}

@Composable
fun LectureDetailItem(
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
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
    onTimeTextChange: (String) -> Unit,
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
                Text(text = time)           // TODO : editMode에 따라 색 변경 (편집 불가)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "장소", modifier = Modifier.width(76.dp))
                EditText(value = location, onValueChange = onLocationTextChange)
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
            .background(Color(0xfff2f2f2))      // TODO: Color 정리
    )
}

@Preview(showBackground = true, widthDp = 480, heightDp = 880)
@Composable
fun LectureDetailPagePreview() {
    LectureDetailPage(lecture = null)
}

private fun String.stringToLong(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        0
    }
}
