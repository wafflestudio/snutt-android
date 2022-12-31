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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.search.lectureApiWithOverlapDialog
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
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
    val focusManager = LocalFocusManager.current

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
    val tableColorTheme = currentTable?.theme ?: TimetableColorTheme.SNUTT
    val isCustom = editingLectureDetail.isCustom

    var deleteLectureDialogState by remember { mutableStateOf(false) }
    var resetLectureDialogState by remember { mutableStateOf(false) }
    var editExitDialogState by remember { mutableStateOf(false) }
    var editingClassTimeIndex by remember { mutableStateOf(0) }
    var deleteTimeDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogState by remember { mutableStateOf(false) }
    var lectureOverlapDialogMessage by remember { mutableStateOf("") }

    /* 바텀시트 관련 */
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
        if (!sheetState.isVisible) {
            bottomSheetContent = { Box(modifier = Modifier.size(1.dp)) }
        }
    }

    /* 뒤로가기 핸들링 */
    BackHandler {
        if (sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        } else if (editMode) {
            if (vm.isAddMode()) { // 새 커스텀 강의 추가일 때는 뒤로가기 하면 바로 나가기
                navController.popBackStack()
            } else editExitDialogState = true
        } else if (viewMode) {
            vm.setViewMode(false)
            navController.popBackStack()
        } else {
            navController.popBackStack()
        }
    }

    /* TODO (진행중)
     * 시간 및 장소 item 추가했을 때 애니메이션 적용하기 (LazyColumn 의 기능 모방)
     * 추가시 애니메이션은 되는데 삭제시는 방법을 고민중
     */
    var classTimeAnimationState by remember {
        mutableStateOf(MutableTransitionState(true))
    }

    /* 웹뷰 관련 */
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

    CompositionLocalProvider(
        LocalReviewWebView provides reviewWebViewContainer,
    ) {
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
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (editMode) {
                                        editExitDialogState = true
                                    } else if (vm.isAddMode()) {
                                        vm.setAddMode(false)
                                        vm.unsetEditMode()
                                        navController.popBackStack()
                                    } else if (viewMode) {
                                        vm.setViewMode(false)
                                        navController.popBackStack()
                                    } else {
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
                            Margin(height = 4.dp)
                        }
                    }
                    Margin(height = 10.dp)
                    Column(modifier = Modifier.background(SNUTTColors.White900)) {
                        Margin(height = 4.dp)
                        if (isCustom.not()) {
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
                                    scope.launch {
                                        focusManager.clearFocus()
                                        sheetState.show()
                                    }
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
                            if (isCustom) {
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
                            } else {
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
                                                bottomSheetContentSetter.invoke {
                                                    ReviewWebView(0.95f)
                                                }
                                                sheetState.show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (isCustom.not() && vm.isViewMode().not()) {
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
            Text(text = lectureOverlapDialogMessage, style = SNUTTTypography.body1)
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

@Composable
fun DayTimePickerSheet(
    classTime: ClassTimeDto,
    onDismiss: () -> Unit,
    onConfirm: (ClassTimeDto) -> Unit,
) {
    val modalState = LocalModalState.current
    val context = LocalContext.current
    val dayList = remember {
        context.resources.getStringArray(R.array.week_days).map {
            it + context.getString(R.string.settings_timetable_config_week_day)
        }
    }
    var dayIndex by remember { mutableStateOf(classTime.day) }

    val amPmList = remember { listOf(context.getString(R.string.morning), context.getString(R.string.afternoon)) }
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
            Text(text = stringResource(R.string.settings_timetable_config_week_day), style = SNUTTTypography.button)
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
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_start_time_label), style = SNUTTTypography.button)
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
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_end_time_label), style = SNUTTTypography.button)
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
