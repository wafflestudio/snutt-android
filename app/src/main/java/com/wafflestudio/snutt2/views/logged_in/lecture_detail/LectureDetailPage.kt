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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.components.compose.embed_map.FoldableEmbedMap
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.creditStringToLong
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getFullQuota
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getQuotaTitle
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.search.*
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.*

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalNaverMapApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun LectureDetailPage(
    vm: LectureDetailViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    vacancyViewModel: VacancyViewModel = hiltViewModel(),
    onCloseViewMode: (scope: CoroutineScope) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val navController = LocalNavController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val focusManager = LocalFocusManager.current
    val pageController = LocalHomePageController.current
    val composableStates = ComposableStatesWithScope(scope)

    val userViewModel = hiltViewModel<UserViewModel>()
    val modeType by vm.modeType.collectAsState()
    val editingLectureDetail by vm.editingLectureDetail.collectAsState()
    val currentTable by vm.currentTable.collectAsState()
    val tableColorTheme = currentTable?.theme ?: TimetableColorTheme.SNUTT
    val isCustom = editingLectureDetail.isCustom
    val bookmarkList by searchViewModel.bookmarkList.collectAsState()
    val isBookmarked = remember(bookmarkList) { bookmarkList.map { it.item.id }.contains(editingLectureDetail.lecture_id ?: editingLectureDetail.id) }
    val vacancyList by vacancyViewModel.vacancyLectures.collectAsState()
    val vacancyRegistered = vacancyList.map { it.id }.contains(editingLectureDetail.lecture_id ?: editingLectureDetail.id)
    val disableMapFeature by LocalRemoteConfig.current.disableMapFeature.collectAsState(true) // NOTE: config를 받아오기 전까지는 지도를 숨긴다.
    var creditText by remember { mutableStateOf(editingLectureDetail.credit.toString()) }
    /* 현재 LectureDto 타입의 editingLectureDetail 플로우를 변경해 가면서 API 부를 때도 쓰고 화면에 정보 표시할 때도 쓰고 있는데,
     * credit은 Long 타입이라서 학점 입력하는 editText에 빈 문자열을 넣었을 때(=다 지웠을 때) 문제가 발생한다. 그래서 credit만 별도의 MutableState<String>을 둬서 운용한다.
     * 이때 다른 정보들은 editingLectureDetail 따라서 바뀌니까 모드가 바뀌어도 따로 할 게 없는데, 얘는 편집모드->일반모드로 바뀔 때 따로 변경해 줘야 한다. 그것이 아래의 코드.
     */
    LaunchedEffect(modeType) {
        if (modeType !is ModeType.Editing) creditText = editingLectureDetail.credit.toString()
    }

    /* 바텀시트 관련 */
    val bottomSheet = bottomSheet()

    /* 뒤로가기 핸들링 */
    val onBackPressed: () -> Unit = {
        if (bottomSheet.isVisible) {
            scope.launch { bottomSheet.hide() }
        } else {
            when (modeType) {
                ModeType.Normal -> {
                    if (navController.currentDestination?.route == NavigationDestination.LectureDetail) {
                        navController.popBackStack()
                    }
                }
                is ModeType.Editing -> {
                    if ((modeType as ModeType.Editing).adding) {
                        navController.popBackStack()
                    } else {
                        showExitEditModeDialog(composableStates, onConfirm = {
                            vm.abandonEditingLectureDetail()
                        },)
                    }
                }
                ModeType.Viewing -> {
                    onCloseViewMode(scope)
                }
            }
        }
    }

    BackHandler {
        onBackPressed()
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
    val reviewBottomSheetWebViewContainer =
        remember {
            WebViewContainer(context, userViewModel.accessToken, isDarkMode).apply {
                this.webView.addJavascriptInterface(CloseBridge(onClose = { scope.launch { bottomSheet.hide() } }), "Snutt")
            }
        }

    ModalBottomSheetLayout(
        sheetContent = bottomSheet.content,
        sheetState = bottomSheet.state,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
//        onDismissScrim = {
//            scope.launch { bottomSheet.hide() }
//        }
        // gesturesEnabled 가 없다! 그래서 드래그해서도 닫아진다..
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.Gray100),
//                    .clicks { focusManager.clearFocus() }
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
                                onBackPressed()
                            },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                },
                actions = {
                    if (isCustom.not() && (modeType !is ModeType.Editing)) {
                        RingingAlarmIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            if (vacancyRegistered) {
                                                vacancyViewModel.removeVacancyLecture(
                                                    editingLectureDetail.lecture_id
                                                        ?: editingLectureDetail.id,
                                                )
                                            } else {
                                                vacancyViewModel.addVacancyLecture(
                                                    editingLectureDetail.lecture_id
                                                        ?: editingLectureDetail.id,
                                                )
                                            }
                                        }
                                    }
                                },
                            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                            marked = vacancyRegistered,
                        )
                        BookmarkIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            if (isBookmarked) {
                                                searchViewModel.deleteBookmark(
                                                    editingLectureDetail,
                                                )
                                            } else {
                                                searchViewModel.addBookmark(editingLectureDetail)
                                            }
                                            searchViewModel.getBookmarkList()
                                        }
                                    }
                                },
                            marked = isBookmarked,
                        )
                    }
                    if (modeType != ModeType.Viewing) {
                        Text(
                            text = when (modeType) {
                                is ModeType.Editing -> stringResource(R.string.lecture_detail_top_bar_complete)
                                else -> stringResource(R.string.lecture_detail_top_bar_edit)
                            },
                            style = SNUTTTypography.body1,
                            modifier = Modifier
                                .clicks {
                                    focusManager.clearFocus()
                                    if (modeType == ModeType.Normal) {
                                        vm.setEditMode()
                                    } else {
                                        checkLectureOverlap(
                                            composableStates,
                                            api = {
                                                if ((modeType as ModeType.Editing).adding) {
                                                    vm.createLecture()
                                                    scope.launch(Dispatchers.Main) { navController.popBackStack() }
                                                } else {
                                                    vm.updateLecture()
                                                }
                                            },
                                            onLectureOverlap = { message ->
                                                showLectureOverlapDialog(composableStates, message, forceAddApi = {
                                                    if ((modeType as ModeType.Editing).adding) {
                                                        vm.createLecture(is_forced = true)
                                                        scope.launch(Dispatchers.Main) { navController.popBackStack() }
                                                    } else {
                                                        vm.updateLecture(is_forced = true)
                                                    }
                                                },)
                                            },
                                        )
                                    }
                                },
                        )
                    }
                },
            )
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier
                        .background(SNUTTColors.White900)
                        .padding(vertical = 4.dp),
                ) {
                    LectureDetailItem(
                        title = stringResource(R.string.lecture_detail_lecture_title),
                        value = editingLectureDetail.course_title,
                        onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(course_title = it)) },
                        hint = if (modeType is ModeType.Editing) stringResource(R.string.lecture_detail_lecture_title_hint) else stringResource(R.string.lecture_detail_hint_nothing),
                        enabled = modeType is ModeType.Editing,
                    )
                    LectureDetailItem(
                        title = stringResource(R.string.lecture_detail_instructor),
                        value = editingLectureDetail.instructor,
                        onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(instructor = it)) },
                        hint = if (modeType is ModeType.Editing) stringResource(R.string.lecture_detail_instructor_hint) else stringResource(R.string.lecture_detail_hint_nothing),
                        enabled = modeType is ModeType.Editing,
                    )
                    if (modeType != ModeType.Viewing) {
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_color),
                        ) {
                            Row(
                                modifier = Modifier.clicks(enabled = modeType != ModeType.Normal) {
                                    navController.navigate(NavigationDestination.LectureColorSelector)
                                },
                            ) {
                                ColorBox(
                                    editingLectureDetail.colorIndex,
                                    editingLectureDetail.color,
                                    tableColorTheme,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                AnimatedVisibility(visible = modeType is ModeType.Editing) {
                                    ArrowRight(
                                        modifier = Modifier.size(16.dp),
                                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                                    )
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .background(SNUTTColors.White900)
                        .padding(vertical = 4.dp),
                ) {
                    if (isCustom.not()) {
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_department),
                            value = editingLectureDetail.department ?: "",
                            onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(department = it)) },
                            enabled = modeType is ModeType.Editing,
                        )
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_academic_year),
                            value = editingLectureDetail.academic_year ?: "",
                            onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(academic_year = it)) },
                            enabled = modeType is ModeType.Editing,
                        )
                    }
                    LectureDetailItem(
                        title = stringResource(R.string.lecture_detail_credit),
                        value = creditText,
                        onValueChange = {
                            creditText = it
                            vm.editLectureDetail(editingLectureDetail.copy(credit = it.creditStringToLong()))
                        },
                        enabled = modeType is ModeType.Editing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        hint = "0",
                    )
                    if (isCustom.not()) {
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_classification),
                            value = editingLectureDetail.classification ?: "",
                            onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(classification = it)) },
                            enabled = modeType is ModeType.Editing,
                        )
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_category),
                            value = editingLectureDetail.category ?: "",
                            onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(category = it)) },
                            enabled = modeType is ModeType.Editing,
                        )
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_course_number),
                            value = editingLectureDetail.course_number ?: "",
                            textStyle = SNUTTTypography.body1.copy(
                                fontSize = 15.sp,
                                color = if (modeType is ModeType.Editing) SNUTTColors.Gray200 else SNUTTColors.Black900,
                            ),
                        )
                        LectureDetailItem(
                            title = stringResource(R.string.lecture_detail_lecture_number),
                            value = editingLectureDetail.lecture_number ?: "",
                            textStyle = SNUTTTypography.body1.copy(
                                fontSize = 15.sp,
                                color = if (modeType is ModeType.Editing) SNUTTColors.Gray200 else SNUTTColors.Black900,
                            ),
                        )
                        LectureDetailItem(
                            title = editingLectureDetail.getQuotaTitle(context),
                            value = editingLectureDetail.getFullQuota(),
                            textStyle = SNUTTTypography.body1.copy(
                                fontSize = 15.sp,
                                color = if (modeType is ModeType.Editing) SNUTTColors.Gray200 else SNUTTColors.Black900,
                            ),
                        )
                    }
                    LectureDetailItem(
                        title = stringResource(R.string.lecture_detail_remark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 40.dp)
                            .padding(vertical = 10.dp),
                        value = editingLectureDetail.remark,
                        onValueChange = { vm.editLectureDetail(editingLectureDetail.copy(remark = it)) },
                        enabled = modeType is ModeType.Editing,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions.Default,
                        keyboardActions = KeyboardActions.Default,
                        hint = if (modeType is ModeType.Editing) stringResource(R.string.lecture_detail_remark_hint) else stringResource(R.string.lecture_detail_hint_nothing),
                        labelVerticalAlignment = Alignment.Top,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SNUTTColors.White900),
                ) {
                    Text(
                        text = stringResource(R.string.lecture_detail_class_time),
                        modifier = Modifier
                            .padding(start = 20.dp, top = 10.dp, bottom = 14.dp),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                    )
                    editingLectureDetail.class_time_json.forEachIndexed { idx, classTime ->
                        LectureDetailTimeAndLocation(
                            timeText = SNUTTStringUtils.getClassTimeText(classTime),
                            locationText = classTime.place,
                            editTime = {
                                bottomSheet.setSheetContent {
                                    DayTimePickerSheet(
                                        bottomSheet = bottomSheet,
                                        classTime = classTime,
                                        onDismiss = { scope.launch { bottomSheet.hide() } },
                                        onConfirm = { editedClassTime ->
                                            vm.editLectureDetail(
                                                editingLectureDetail.copy(
                                                    class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                                        .also {
                                                            it[idx] = editedClassTime
                                                        },
                                                ),
                                            )
                                            scope.launch { bottomSheet.hide() }
                                        },
                                    )
                                }
                                scope.launch {
                                    focusManager.clearFocus()
                                    bottomSheet.show()
                                }
                            },
                            onLocationTextChange = { changedLocation ->
                                vm.editLectureDetail(
                                    editingLectureDetail.copy(
                                        class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                            .also {
                                                it[idx] =
                                                    classTime.copy(place = changedLocation)
                                            },
                                    ),
                                )
                            },
                            onClickDeleteIcon = {
                                showDeleteClassTimeDialog(composableStates, onConfirm = {
                                    vm.editLectureDetail(
                                        editingLectureDetail.copy(
                                            class_time_json = editingLectureDetail.class_time_json.toMutableList()
                                                .also {
                                                    it.removeAt(idx)
                                                },
                                        ),
                                    )
                                },)
                            },
                            editMode = modeType is ModeType.Editing,
                            visible = if (idx == editingLectureDetail.class_time_json.lastIndex) {
                                classTimeAnimationState
                            } else {
                                MutableTransitionState(true)
                            },
                        )
                    }
                    AnimatedVisibility(
                        visible = modeType is ModeType.Editing,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clicks {
                                    classTimeAnimationState =
                                        MutableTransitionState(false).apply {
                                            targetState = true
                                        }
                                    vm.editLectureDetail(
                                        editingLectureDetail.copy(
                                            class_time_json = editingLectureDetail.class_time_json
                                                .toMutableList()
                                                .also {
                                                    it.add(it.lastOrNull() ?: ClassTimeDto.Default)
                                                },
                                        ),
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.lecture_detail_add_class_time),
                                textAlign = TextAlign.Center,
                                style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                            )
                        }
                    }
                    if (disableMapFeature.not()) {
                        AnimatedVisibility(
                            visible = modeType !is ModeType.Editing,
                        ) {
                            FoldableEmbedMap(
                                modifier = Modifier.padding(vertical = 8.dp),
                                buildings = editingLectureDetail.buildings,
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = modeType !is ModeType.Editing,
                ) {
                    Column {
                        if (isCustom) {
                            Box(modifier = Modifier.background(Color.White)) {
                                LectureDetailButton(
                                    title = stringResource(R.string.lecture_detail_delete_button),
                                    textStyle = SNUTTTypography.body1.copy(
                                        fontSize = 15.sp,
                                        color = SNUTTColors.Red,
                                    ),
                                ) {
                                    showDeleteLectureDialog(composableStates, onConfirm = {
                                        vm.removeLecture()
                                        scope.launch(Dispatchers.Main) {
                                            navController.popBackStack()
                                        }
                                    },)
                                }
                            }
                        } else {
                            Column(modifier = Modifier.background(SNUTTColors.White900)) {
                                LectureDetailButton(title = stringResource(R.string.lecture_detail_syllabus_button)) {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vm.getCourseBookUrl().let { url ->
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            }
                                        }
                                    }
                                }
                                LectureDetailButton(title = stringResource(R.string.lecture_detail_review_button)) {
                                    verifyEmailBeforeApi(
                                        composableStates,
                                        api = {
                                            val url = vm.getReviewContentsUrl()
                                            openReviewBottomSheet(
                                                url,
                                                reviewBottomSheetWebViewContainer,
                                                bottomSheet,
                                            )
                                        },
                                        onUnverified = {
                                            onCloseViewMode(scope)
                                            navController.navigateAsOrigin(NavigationDestination.Home)
                                            pageController.update(HomeItem.Review())
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
                if (isCustom.not() && modeType != ModeType.Viewing) {
                    Box(modifier = Modifier.background(Color.White)) {
                        LectureDetailButton(
                            title = if (modeType is ModeType.Editing) {
                                stringResource(R.string.lecture_detail_reset_button)
                            } else {
                                stringResource(
                                    R.string.lecture_detail_delete_button,
                                )
                            },
                            textStyle = SNUTTTypography.body1.copy(
                                fontSize = 15.sp,
                                color = SNUTTColors.Red,
                            ),
                        ) {
                            if (modeType is ModeType.Editing) {
                                showResetLectureDialog(composableStates, onConfirm = {
                                    vm.resetLecture()
                                },)
                            } else {
                                showDeleteLectureDialog(composableStates, onConfirm = {
                                    vm.removeLecture()
                                    scope.launch(Dispatchers.Main) {
                                        navController.popBackStack()
                                    }
                                },)
                            }
                        }
                    }
                }
                Margin(height = 30.dp)
            }
        }
    }
}

@Composable
private fun LectureDetailButton(
    title: String,
    textStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth()
            .height(45.dp)
            .clicks { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title, style = textStyle)
    }
}

@Composable
private fun LectureDetailTimeAndLocation(
    timeText: String,
    locationText: String,
    editTime: () -> Unit = {},
    onLocationTextChange: (String) -> Unit,
    onClickDeleteIcon: () -> Unit = {},
    editMode: Boolean,
    visible: MutableTransitionState<Boolean> = MutableTransitionState(true),
) {
    AnimatedVisibility(
        visibleState = visible,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.lecture_detail_time),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                        modifier = Modifier.width(76.dp),
                    )
                    Text(
                        text = timeText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clicks {
                                if (editMode) editTime()
                            },
                        style = SNUTTTypography.body1.copy(
                            fontSize = 15.sp,
                            color = SNUTTColors.Black900,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
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
            AnimatedVisibility(visible = editMode) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .clicks { onClickDeleteIcon() },
                    contentAlignment = Alignment.Center,
                ) {
                    TipCloseIcon(modifier = Modifier.size(16.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
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
            .height(height),
    )
}

@Composable
fun ColorBox(
    lectureColorIndex: Long,
    lectureColor: ColorDto?, // null 이면 반드시 기존 테마.
    theme: TimetableColorTheme,
) {
    Row(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp)
            .zIndex(1f)
            .border(width = (0.5f).dp, color = SNUTTColors.Black250),
    ) {
        Box(
            modifier = Modifier
                .background(
                    // colorIndex == 0 이면 사용자 커스텀 색
                    // colorIndex > 0 이면 bgColor 는 스누티티 지정 테마 색깔, fgColor = -0x1 (디폴트 흰색)
                    if ((lectureColorIndex) > 0) {
                        Color(-0x1)
                    } // 커스텀 fg 색이면 null 이 오지 않아서 원래는 !! 처리했지만..
                    else {
                        Color(lectureColor?.fgColor ?: -0x1)
                    },
                )
                .size(20.dp),
        )
        Box(
            modifier = Modifier
                .background(
                    // index > 0 : 스누티티 지정 테마 색깔.
                    if (lectureColorIndex > 0) {
                        theme.getColorByIndexComposable(lectureColorIndex)
                    } // 사용자 지정 bgColor, 역시 이때는 null이 오지 않아서 !! 처리를 했었다. 그냥 !! 해도 될지도
                    else {
                        Color(lectureColor?.bgColor ?: (-0x1))
                    },
                )
                .size(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 880)
@Composable
fun LectureDetailPagePreview() {
//    LectureDetailPage()
}
