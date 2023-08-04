package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VacancyPage(
    vacancyViewModel: VacancyViewModel
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val modalState = LocalModalState.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val vacancyLectures by vacancyViewModel.vacancyLectures.collectAsState()
    val isRefreshing by vacancyViewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(isRefreshing, { vacancyViewModel.refreshVacancyLectures() })
    val selectedLectures = vacancyViewModel.selectedLectures
    val deleteEnabled by remember {
        derivedStateOf { vacancyViewModel.isEditMode && selectedLectures.size > 0 }
    }
    val transition = updateTransition(vacancyViewModel.isEditMode, label = "")
    val collapsedContentHeight = (LocalConfiguration.current.screenHeightDp - 56).dp
    val expandedContentHeight = (LocalConfiguration.current.screenHeightDp - 56 + 60).dp
    val contentHeight by transition.animateDp(label = "") { isEditMode -> // 리스트+삭제버튼 길이(topbar 56dp, 삭제버튼 60dp)
        if (isEditMode) collapsedContentHeight
        else expandedContentHeight
    }
    val lazyListState = rememberLazyListState()
    var introDialogState by remember { mutableStateOf(vacancyViewModel.firstVacancyVisit.value) }
    val scrollWithButtonAppearing by remember {
        derivedStateOf { vacancyViewModel.isEditMode && lazyListState.isScrolledToEnd() }
    }

    val onBackPressed = {
        if (vacancyViewModel.isEditMode) {
            vacancyViewModel.toggleEditMode()
        } else {
            if (navController.currentDestination?.route == NavigationDestination.VacancyNotification) {
                navController.popBackStack()
            }
        }
    }
    BackHandler {
        onBackPressed()
    }

    LaunchedEffect(Unit) {
        launchSuspendApi(apiOnProgress, apiOnError) {
            vacancyViewModel.getVacancyLectures()
        }
    }

    LaunchedEffect(scrollWithButtonAppearing) {
        while (scrollWithButtonAppearing && lazyListState.layoutInfo.totalItemsCount > 0 && contentHeight > collapsedContentHeight) {
            lazyListState.animateScrollToItem(
                lazyListState.layoutInfo.totalItemsCount - 1
            )
        }
    }

    Box {
        Column(
            modifier = Modifier.background(SNUTTColors.White900)
        ) {
            TopBar(
                title = {
                    Text(
                        text = stringResource(R.string.vacancy_app_bar_title),
                        style = SNUTTTypography.h2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    QuestionCircleIcon(
                        modifier = Modifier
                            .size(18.dp)
                            .clicks {
                                introDialogState = true
                            }
                    )
                },
                navigationIcon = {
                    ArrowBackIcon(
                        modifier = Modifier
                            .size(30.dp)
                            .clicks { onBackPressed() },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                    )
                },
                actions = {
                    if (vacancyLectures.isNotEmpty()) {
                        Text(
                            text = if (!vacancyViewModel.isEditMode)
                                stringResource(R.string.vacancy_app_bar_edit)
                            else
                                stringResource(R.string.vacancy_app_bar_cancel),
                            style = SNUTTTypography.body1,
                            modifier = Modifier
                                .clicks {
                                    scope.launch {
                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                            vacancyViewModel.toggleEditMode()
                                        }
                                    }
                                }
                        )
                    }
                }
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top, unbounded = true)
                    .height(contentHeight)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .then(
                            if (vacancyViewModel.isEditMode.not())
                                Modifier.pullRefresh(pullRefreshState)
                            else
                                Modifier
                        )
                ) {
                    if (vacancyLectures.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(if (isDarkMode()) R.drawable.img_vacancy_empty_dark else R.drawable.img_vacancy_empty),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(180.dp)
                                    .fillMaxSize()
                            )
                            Margin(height = 14.dp)
                            Row(
                                modifier = Modifier
                                    .clicks { introDialogState = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                QuestionCircleIcon(
                                    modifier = Modifier.size(12.dp),
                                    colorFilter = ColorFilter.tint(SNUTTColors.DARKER_GRAY)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = stringResource(R.string.vacancy_empty_detail),
                                    textDecoration = TextDecoration.Underline,
                                    style = SNUTTTypography.subtitle2.copy(
                                        fontSize = 12.sp,
                                        color = SNUTTColors.DARKER_GRAY
                                    )
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .matchParentSize(),
                            state = lazyListState
                        ) {
                            items(
                                items = vacancyLectures,
                                key = { it.id }
                            ) {
                                val lectureId = it.id
                                VacancyListItem(
                                    lectureDto = it,
                                    editing = vacancyViewModel.isEditMode,
                                    checked = selectedLectures.contains(lectureId),
                                    onClick = {
                                        if (vacancyViewModel.isEditMode) {
                                            vacancyViewModel.toggleLectureSelected(lectureId)
                                        }
                                    },
                                )
                            }
                        }
                    }
                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
                WebViewStyleButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        modalState.set(
                            title = context.getString(R.string.vacancy_delete_selected_title),
                            positiveButton = context.getString(R.string.common_ok),
                            negativeButton = context.getString(R.string.common_cancel),
                            onDismiss = { modalState.hide() },
                            onConfirm = {
                                scope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        vacancyViewModel.deleteSelectedLectures()
                                        vacancyViewModel.toggleEditMode()
                                    }
                                }
                                modalState.hide()
                            },
                            content = {
                                Text(
                                    text = context.getString(R.string.vacancy_delete_selected_message),
                                    style = SNUTTTypography.body1
                                )
                            },
                        ).show()
                    },
                    enabled = deleteEnabled,
                    disabledColor = SNUTTColors.VacancyGray
                ) {
                    Text(
                        text = stringResource(R.string.vacancy_delete_selected),
                        style = SNUTTTypography.h3.copy(
                            color = SNUTTColors.AllWhite,
                        )
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !vacancyViewModel.isEditMode,
            modifier = Modifier
                .align(Alignment.BottomEnd),
            enter = slideInVertically {
                with(density) { 10.dp.roundToPx() }
            } + fadeIn(),
            exit = slideOutVertically {
                with(density) { 10.dp.roundToPx() }
            } + fadeOut()
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(end = 20.dp, bottom = 30.dp)
                    .height(45.dp),
                text = {
                    Text(
                        text = stringResource(R.string.vacancy_floating_button),
                        style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite),
                        maxLines = 1
                    )
                },
                contentColor = SNUTTColors.SNUTTVacancy,
                onClick = {
                    val sugangSnuUrl = "https://sugang.snu.ac.kr/sugang/ca/ca102.action?workType=F"
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(sugangSnuUrl))
                    context.startActivity(intent)
                },
                elevation = FloatingActionButtonDefaults.elevation(3.dp, 3.dp)
            )
        }
        if (introDialogState) {
            VacancyIntroDialog(
                onDismiss = {
                    introDialogState = false
                    scope.launch {
                        vacancyViewModel.setVacancyVisited()
                    }
                }
            )
        }
    }
}

fun LazyListState.isScrolledToEnd(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.last()
    return lastVisibleItem.offset + lastVisibleItem.size == this.layoutInfo.viewportEndOffset
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VacancyIntroDialog(
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val width = (LocalConfiguration.current.screenWidthDp * 0.8).dp
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .width(width)
                    .height(width * (640f / 600))
                    .background(SNUTTColors.White900),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TipCloseIcon(
                        modifier = Modifier
                            .size(15.dp)
                            .clicks {
                                onDismiss()
                            },
                        colorFilter = ColorFilter.tint(if (pagerState.currentPage != 3) SNUTTColors.VacancyGray else SNUTTColors.Black900)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                ) {
                    HorizontalPager(
                        count = 4,
                        modifier = Modifier
                            .padding(horizontal = 13.dp)
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        state = pagerState,
                    ) { page ->
                        Image(
                            painter = painterResource(
                                if (isDarkMode()) {
                                    when (page) {
                                        0 -> R.drawable.img_vacancy_intro_dark_0
                                        1 -> R.drawable.img_vacancy_intro_dark_1
                                        2 -> R.drawable.img_vacancy_intro_dark_2
                                        else -> R.drawable.img_vacancy_intro_dark_3
                                    }
                                } else {
                                    when (page) {
                                        0 -> R.drawable.img_vacancy_intro_0
                                        1 -> R.drawable.img_vacancy_intro_1
                                        2 -> R.drawable.img_vacancy_intro_2
                                        else -> R.drawable.img_vacancy_intro_3
                                    }
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.offset(y = (-7).dp)
                        )
                    }
                    if (pagerState.currentPage > 0) {
                        ArrowBackIcon(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(30.dp)
                                .clicks {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                            colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray)
                        )
                    }
                    if (pagerState.currentPage < 3) {
                        RightArrowIcon(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(30.dp)
                                .clicks {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                            colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray)
                        )
                    }
                }
//                Margin(height = 38.dp)
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.padding(bottom = 36.dp),
                    activeColor = when (pagerState.currentPage) {
                        0 -> SNUTTColors.Red
                        1 -> SNUTTColors.Grass
                        2 -> SNUTTColors.Orange
                        else -> SNUTTColors.Sky
                    },
                    inactiveColor = SNUTTColors.Gray10,
                    indicatorHeight = 6.dp,
                    indicatorWidth = 6.dp
                )
            }
        }
    }
}
