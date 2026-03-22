package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowLeftBold
import com.wafflestudio.snutt2.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.DrawerIcon
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.RingingAlarmIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalCompactState
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalRemoteConfig
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.showTitleChangeDialog
import com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor.TimetableDropdownOverlay
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import com.wafflestudio.snutt2.views.logged_in.table_lectures.refactor.TableLectureItemNew
import kotlinx.coroutines.launch

@Composable
fun TimetablePage(
    onNavigateToSearch: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val table = LocalTableState.current.table
    val tableTrimParam = LocalTableState.current.trimParam
    val remoteConfig = LocalRemoteConfig.current
    val composableStates = ComposableStatesWithScope(scope)
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>()
    val newSemesterNotify by tableListViewModel.newSemesterNotify.collectAsState(false)
    val sessionlessLectures = table.lectureList.map { it.toLocalLecture() }.filter { it.lectureSessions.isEmpty() }
    val isSessionlessLectureExists = !sessionlessLectures.isEmpty()
    val isVisitedSessionlessLectureList by timetableViewModel.isVisitedSessionlessLectureList.collectAsState(false)
    val vacancyNotificationBannerEnabled by remoteConfig.vacancyNotificationBannerEnabled.collectAsState(
        false,
    )
    val analyticsLogger = LocalAnalyticsLogger.current
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(
        targetValue = if (dropdownMenuExpanded) 0f else -45f,
        animationSpec = spring(),
    )
    val scrollState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf {
            scrollState.firstVisibleItemScrollOffset > 0
        }
    }
    var scrollUnlocked by remember { mutableStateOf(false) }

    val isHintVisible by produceState(
        initialValue = false,
        isVisitedSessionlessLectureList,
        isSessionlessLectureExists,
        scrollUnlocked,
    ) {
        value =
            !isVisitedSessionlessLectureList &&
                isSessionlessLectureExists &&
                !scrollUnlocked
    }

    var timetableHeightPx by remember { mutableStateOf(0) }
    var vacancyBannerHeightPx by remember { mutableStateOf(0) }
    val connection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val velocity = available.y

                return if (velocity <= -50 || scrollUnlocked) {
                    scrollUnlocked = true
                    Offset.Zero
                } else {
                    available
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y > 0) available else Velocity.Zero
            }
        }
    }

    LaunchedEffect(
        isHintVisible,
    ) {
        if (scrollUnlocked) {
            timetableViewModel.visitSessionlessLectureList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.White900)
                .logImpression(AnalyticsScreen.TimetableHome),
        ) {
            TopBar(
                title = {
                    Text(
                        text = table.title,
                        style = SNUTTTypography.h2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clicks {
                                showTitleChangeDialog(
                                    table.title,
                                    table.id,
                                    composableStates,
                                    tableListViewModel::changeTableName,
                                )
                            },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.timetable_credit,
                            getCreditSumFromLectureList(table.lectureList),
                        ),
                        style = SNUTTTypography.body2,
                        maxLines = 1,
                        color = SNUTTColors.Gray200,
                    )
                },
                navigationIcon = {
                    IconWithAlertDot(newSemesterNotify) { centerAlignedModifier ->
                        DrawerIcon(
                            modifier = centerAlignedModifier
                                .size(30.dp)
                                .clicks { scope.launch { drawerState.open() } },
                        )
                    }
                },
                actions = {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(30.dp)
                            .clicks { navController.navigate(NavigationDestination.Bookmark) },
                    )
                    ExitIcon(
                        modifier = Modifier
                            .size(30.dp)
                            .graphicsLayer { rotationZ = iconRotation }
                            .clicks { dropdownMenuExpanded = true },
                    )
                },
            )
            Box {
                if (isScrolled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SNUTTColors.Black.copy(alpha = 0.05f),
                                        SNUTTColors.Transparent,
                                    ),
                                ),
                            ),
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            timetableHeightPx = it.size.height
                        }
                        .nestedScroll(connection),
                    state = scrollState,
                    userScrollEnabled = isSessionlessLectureExists,
                ) {
                    item {
                        if (vacancyNotificationBannerEnabled) {
                            VacancyBanner(
                                modifier = Modifier.onGloballyPositioned {
                                    vacancyBannerHeightPx = it.size.height
                                },
                                onClick = {
                                    navController.navigate(NavigationDestination.VacancyNotification)
                                },
                            )
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    with(LocalDensity.current) {
                                        timetableHeightPx.toDp() - vacancyBannerHeightPx.toDp()
                                    },
                                ),
                        ) {
                            TimeTable(
                                table = LocalTableState.current.table,
                                trimParam = LocalTableState.current.trimParam,
                                tableLectureCustomOptions = LocalTableState.current.tableLectureCustomOptions,
                                previewTheme = LocalTableState.current.previewTheme,
                                compactMode = LocalCompactState.current,
                                navigator = LocalNavController.current,
                                selectedLecture = null,
                            )
                        }
                    }
                    item {
                        Divider(thickness = 2.dp)
                    }
                    items(sessionlessLectures) {
                        TableLectureItemNew(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
                            lecture = it,
                            onClickLecture = { lecture ->
                                // FIXME: 임시 코드
                                lectureDetailViewModel.initializeEditingLectureDetail(LectureDto.fromLocalLecture(lecture), ModeType.Normal, timetableViewModel.currentTable.value)
                                navController.navigate(NavigationDestination.LectureDetail) {
                                    launchSingleTop = true
                                }
                            },
                        )
                        Row(Modifier.padding(vertical = 5.dp)) {
                            Divider(thickness = 1.dp, color = SNUTTColors.Black050)
                        }
                    }
                    if (sessionlessLectures.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.size(52.dp))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.padding(bottom = 10.dp),
            visible = isHintVisible,
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing,
                ),
            ),
        ) {
            HomeSessionlessLectureHint()
        }

        if (dropdownMenuExpanded) {
            TimetableDropdownOverlay(
                onDismiss = { dropdownMenuExpanded = false },
                onClickAddBySearch = onNavigateToSearch,
                onClickAddManually = {
                    lectureDetailViewModel.initializeEditingLectureDetail(
                        LectureDto.Default,
                        ModeType.Editing(true),
                    )
                    navController.navigate(NavigationDestination.LectureDetail)
                },
                onClickTableLecturesListIcon = {
                    navController.navigate(NavigationDestination.LecturesOfTable)
                },
                onClickVacancyIcon = {
                    navController.navigate(NavigationDestination.VacancyNotification)
                },
            )
        }
    }
}

@Composable
fun VacancyBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(SNUTTColors.BannerBlue)
            .clicks { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RingingAlarmIcon(
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(R.string.vacancy_banner_text),
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.AllWhite,
                ),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "NEW",
                style = SNUTTTypography.h5.copy(
                    fontSize = 8.sp,
                    color = SNUTTColors.AllWhite,
                ),
            )
        }
    }
}


@Composable
fun HomeSessionlessLectureHint(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val yOffsetTop by infiniteTransition.animateValue(
        initialValue = (-3).dp,
        targetValue = 2.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = EaseIn,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        typeConverter = Dp.VectorConverter,
    )
    val yOffsetBottom by infiniteTransition.animateValue(
        initialValue = (-3).dp,
        targetValue = 2.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = EaseIn,
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(30),
        ),
        typeConverter = Dp.VectorConverter,
    )
    val borderColor = SNUTTColors.SNUTTTheme
    Column(
        modifier = Modifier.offset(y = (40).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val blurRadius = 8.dp.toPx()

                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            style = android.graphics.Paint.Style.STROKE
                            color = borderColor.toArgb()
                            this.strokeWidth = strokeWidth
                            maskFilter = android.graphics.BlurMaskFilter(
                                blurRadius,
                                android.graphics.BlurMaskFilter.Blur.NORMAL,
                            )
                        }

                        val halfStroke = strokeWidth / 2
                        val rect = android.graphics.RectF(
                            halfStroke,
                            halfStroke,
                            size.width - halfStroke,
                            size.height - halfStroke,
                        )

                        canvas.nativeCanvas.drawRoundRect(
                            rect,
                            7.2.dp.toPx(),
                            7.2.dp.toPx(),
                            paint,
                        )
                    }
                }
                .background(
                    color = SNUTTColors.DropdownMenuBackground,
                    shape = RoundedCornerShape(7.2.dp),
                )
                .border(
                    width = 1.dp,
                    color = SNUTTColors.SNUTTTheme,
                    shape = RoundedCornerShape(7.2.dp),
                ),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 7.5.dp, horizontal = 16.dp),
                text = stringResource(R.string.timetable_sessionless_lecture_list_hint),
                style = SNUTTTypography.body1,
                color = SNUTTColors.SNUTTDarkMintBlue,
            )
        }
        ArrowLeftBold(
            modifier = Modifier
                .rotate(-90F)
                .offset(x = 5.dp + yOffsetTop),
            colorFilter = ColorFilter.tint(SNUTTColors.Hint1),
        )
        ArrowLeftBold(
            modifier = Modifier
                .rotate(-90F)
                .offset(x = 40.dp + yOffsetBottom),
            colorFilter = ColorFilter.tint(SNUTTColors.Hint2),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeSessionlessLectureHintPreview() {
    HomeSessionlessLectureHint()
}
