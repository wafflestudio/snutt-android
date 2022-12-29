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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LectureDetailPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = LocalNavController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val pageController = LocalHomePageController.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModelNew>(backStackEntry)
    val userViewModel = hiltViewModel<UserViewModel>()
    val viewMode = vm.isViewMode()
    val editMode by vm.editMode.collectAsState()
    val editingLectureDetail by vm.editingLectureDetail.collectAsState()
    val currentTable by vm.currentTable.collectAsState()

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    var resetLectureDialogState by remember { mutableStateOf(false) }
    var editExitDialogState by remember { mutableStateOf(false) }
    BackHandler(enabled = editMode) {
        editExitDialogState = true
    }
    BackHandler(enabled = viewMode) {
        vm.setViewMode(false)
        navController.popBackStack()
    }

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetContent by remember {
        mutableStateOf<@Composable ColumnScope.() -> Unit>({
            Box(modifier = Modifier.size(1.dp))
        })
    }
    val sheetContentSetter: (@Composable ColumnScope.() -> Unit) -> Unit = {
        sheetContent = it
    }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            sheetContent = { Box(modifier = Modifier.size(1.dp)) }
        }
    }

    val isDarkMode = isDarkMode()
    val bridge = remember {
        CloseBridge(
            onClose = { scope.launch { sheetState.hide() } }
        )
    }
    val reviewWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
                this.webView.addJavascriptInterface(
                    bridge,
                    "Snutt"
                )
            }
        }
    BackHandler(enabled = sheetState.isVisible) {
        scope.launch { sheetState.hide() }
    }

    CompositionLocalProvider(
        LocalReviewWebView provides reviewWebViewContainer,
    ) {
        ModalBottomSheetLayout(
            sheetContent = sheetContent,
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)
            // gesturesEnabled 가 없다! 그래서 드래그해서도 닫아진다..
        ) {
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
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (editMode) editExitDialogState = true
                                    else {
                                        if (viewMode) vm.setViewMode(false)
                                        navController.popBackStack()
                                    }
                                },
                            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                        )
                    },
                    actions = {
                        if (vm.isViewMode().not()) {
                            Text(
                                text = if (editMode) stringResource(R.string.lecture_detail_top_bar_complete)
                                else stringResource(R.string.lecture_detail_top_bar_edit),
                                style = SNUTTTypography.body1,
                                modifier = Modifier
                                    .clicks {
                                        if (editMode.not()) vm.setEditMode()
                                        else {
                                            scope.launch {
                                                launchSuspendApi(apiOnProgress, apiOnError) {
                                                    vm.updateLecture2()
                                                    vm.initializeEditingLectureDetail(
                                                        editingLectureDetail
                                                    )
                                                    vm.unsetEditMode()
                                                }
                                            }
                                        }
                                    }
                                    .padding(end = 16.dp)
                            )
                        }
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
                                    vm.editEditingLectureDetail(
                                        editingLectureDetail.copy(
                                            course_title = it
                                        )
                                    )
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
                                modifier = Modifier.fillMaxWidth(),
                                underlineEnabled = false,
                                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                                hint = if (editMode) stringResource(R.string.lecture_detail_instructor_hint)
                                else stringResource(R.string.lecture_detail_hint_nothing),
                            )
                        }
                        if (viewMode.not()) {
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
                                        currentTable?.theme!!
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
                            Margin(height = 4.dp)
                        }
                    }
                    Margin(height = 10.dp)
                    Column(modifier = Modifier.background(SNUTTColors.White900)) {
                        Margin(height = 4.dp)
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_department)) {
                            EditText(
                                value = editingLectureDetail.department ?: "",
                                onValueChange = {
                                    vm.editEditingLectureDetail(editingLectureDetail.copy(department = it))
                                },
                                enabled = editMode,
                                modifier = Modifier.fillMaxWidth(),
                                underlineEnabled = false,
                                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                                hint = stringResource(R.string.lecture_detail_hint_nothing),
                            )
                        }
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_academic_year)) {
                            EditText(
                                value = editingLectureDetail.academic_year ?: "",
                                onValueChange = {
                                    vm.editEditingLectureDetail(
                                        editingLectureDetail.copy(
                                            academic_year = it
                                        )
                                    )
                                },
                                enabled = editMode,
                                modifier = Modifier.fillMaxWidth(),
                                underlineEnabled = false,
                                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                                hint = stringResource(R.string.lecture_detail_hint_nothing),
                            )
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
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_classification)) {
                            EditText(
                                value = editingLectureDetail.classification ?: "",
                                onValueChange = {
                                    vm.editEditingLectureDetail(
                                        editingLectureDetail.copy(
                                            classification = it
                                        )
                                    )
                                },
                                enabled = editMode,
                                modifier = Modifier.fillMaxWidth(),
                                underlineEnabled = false,
                                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                                hint = stringResource(R.string.lecture_detail_hint_nothing),
                            )
                        }
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_category)) {
                            EditText(
                                value = editingLectureDetail.category ?: "",
                                onValueChange = {
                                    vm.editEditingLectureDetail(editingLectureDetail.copy(category = it))
                                },
                                enabled = editMode,
                                modifier = Modifier.fillMaxWidth(),
                                underlineEnabled = false,
                                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                                hint = stringResource(R.string.lecture_detail_hint_nothing),
                            )
                        }
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_course_number)) {
                            Text(
                                text = editingLectureDetail.course_number ?: "",
                                style = SNUTTTypography.body1.copy(
                                    fontSize = 15.sp,
                                    color = if (editMode) SNUTTColors.Gray200 else SNUTTColors.Black900
                                )
                            )
                        }
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_lecture_number)) {
                            Text(
                                text = editingLectureDetail.lecture_number ?: "",
                                style = SNUTTTypography.body1.copy(
                                    fontSize = 15.sp,
                                    color = if (editMode) SNUTTColors.Gray200 else SNUTTColors.Black900
                                )
                            )
                        }
                        LectureDetailItem(title = stringResource(R.string.lecture_detail_quota)) {
                            Text(
                                text = editingLectureDetail.quota.toString(),
                                style = SNUTTTypography.body1.copy(
                                    fontSize = 15.sp,
                                    color = if (editMode) SNUTTColors.Gray200 else SNUTTColors.Black900
                                )
                            )
                        }
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
                        Margin(height = 4.dp)
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
                                onLocationTextChange = { changedLocation ->
                                    vm.editEditingLectureDetail(
                                        editingLectureDetail.copy(
                                            class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                                .also {
                                                    it[idx] =
                                                        classTime.copy(place = changedLocation)
                                                }
                                        )
                                    )
                                },
                                editMode = editMode
                            )
                        }
                        Margin(height = 7.dp)
                    }
                    AnimatedVisibility(
                        visible = editMode.not()
                    ) {
                        Column {
                            Margin(height = 30.dp)
                            Column(modifier = Modifier.background(SNUTTColors.White900)) {
                                LectureDetailButton(title = stringResource(R.string.lecture_detail_syllabus_button)) {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vm.getCourseBookUrl().let { url ->
                                                val intent =
                                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            }
                                        }
                                    }
                                }
                                LectureDetailButton(title = stringResource(R.string.lecture_detail_review_button)) {
                                    scope.launch {
                                        val job: CompletableJob = Job()
                                        scope.launch {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                reviewWebViewContainer.openPage(vm.getReviewContentsUrl() + "&on_back=close")
                                                job.complete()
                                            }
                                        }
                                        joinAll(job)
                                        scope.launch {
                                            sheetContentSetter.invoke {
                                                ReviewWebView(0.95f)
                                            }
                                            sheetState.show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (vm.isViewMode().not()) {
                        Margin(height = 10.dp)
                        Box(modifier = Modifier.background(Color.White)) {
                            LectureDetailButton(
                                title = if (editMode) stringResource(R.string.lecture_detail_reset_button) else stringResource(
                                    R.string.lecture_detail_delete_button
                                ),
                                textStyle = SNUTTTypography.body1.copy(
                                    fontSize = 15.sp,
                                    color = SNUTTColors.Red
                                )
                            ) {
                                if (editMode) resetLectureDialogState = true
                                else deleteLectureDialogState = true
                            }
                        }
                    }
                    Margin(height = 30.dp)
                }
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
            Text(text = stringResource(R.string.lecture_detail_delete_dialog_message), style = SNUTTTypography.body1)
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

    // 강의 초기화 다이얼로그
    if (resetLectureDialogState) {
        CustomDialog(
            onDismiss = { resetLectureDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        val resetLecture = vm.resetLecture2()
                        vm.initializeEditingLectureDetail(resetLecture)
                        vm.unsetEditMode()
                        resetLectureDialogState = false
                    }
                }
            },
            title = stringResource(R.string.lecture_detail_reset_dialog_title)
        ) {
            Text(text = stringResource(R.string.lecture_detail_reset_dialog_message), style = SNUTTTypography.body2)
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
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
            modifier = Modifier.width(76.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
fun LectureDetailRemark(
    title: String,
    editMode: Boolean,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .padding(vertical = 10.dp)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
            modifier = Modifier.width(76.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
fun LectureDetailButton(
    title: String,
    textStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth()
            .height(45.dp)
            .clicks { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = textStyle)
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
                        text = stringResource(R.string.lecture_detail_time),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                        modifier = Modifier.width(76.dp)
                    )
                    Text(
                        text = timeText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clicks {
                                if (editMode && isCustom) editTime()
                            },
                        style = SNUTTTypography.body1.copy(
                            fontSize = 15.sp,
                            color = if (isCustom.not() && editMode) SNUTTColors.Gray200 else SNUTTColors.Black900
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.lecture_detail_place),
                        modifier = Modifier.width(76.dp),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = locationText,
                        onValueChange = onLocationTextChange,
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                        underlineEnabled = false,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                        hint = stringResource(R.string.lecture_detail_hint_nothing),
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
                        TipCloseIcon(modifier = Modifier.size(16.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
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
            .border(width = (0.5f).dp, color = SNUTTColors.Black250)
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

fun String.stringToLong(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        0
    }
}
