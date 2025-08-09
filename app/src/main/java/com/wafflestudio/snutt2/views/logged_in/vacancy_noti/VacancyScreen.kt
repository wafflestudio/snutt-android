package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.ModalProperties
import com.wafflestudio.snutt2.components.compose.QuestionCircleIcon
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TipCloseIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import kotlinx.coroutines.launch

@Composable
fun VacancyRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: VacancyViewModel = hiltViewModel(),
) {
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun VacancyScreen(
    modifier: Modifier = Modifier,
    uiState: VacancyUiState,
    onClickBack: () -> Unit,
    onShowIntroDialog: () -> Unit,
    onHideIntroDialog: () -> Unit,
    onToggleEditMode: () -> Unit,
    onRefreshVacancyLectures: () -> Unit,
    onToggleLectureSelected: (String) -> Unit,
    onShowDeleteModal: (ModalProperties) -> Unit,
    onHideDeleteModal: () -> Unit,
    onDeleteSelectedLectures: () -> Unit,
    onOpenSugangSnu: () -> Unit,
) {
    val context = LocalContext.current

    when (uiState) {
        VacancyUiState.Loading -> {}
        VacancyUiState.Error -> {}
        is VacancyUiState.Empty -> {}
        is VacancyUiState.Success -> {
            val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, onRefreshVacancyLectures)
            val density = LocalDensity.current
            Box(
                modifier = Modifier.logImpression(AnalyticsScreen.Vacancy),
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(SNUTTColors.SettingBackground),
                ) {
                    TopBar(
                        title = {
                            Text(
                                text = stringResource(R.string.vacancy_app_bar_title),
                                style = SNUTTTypography.h2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            QuestionCircleIcon(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clicks { onShowIntroDialog() },
                            )
                        },
                        navigationIcon = {
                            ArrowBackIcon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clicks { onClickBack() },
                                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                            )
                        },
                        actions = {
                            Text(
                                text = if (!uiState.isEditMode) {
                                    stringResource(R.string.vacancy_app_bar_edit)
                                } else {
                                    stringResource(R.string.vacancy_app_bar_cancel)
                                },
                                style = SNUTTTypography.body1,
                                modifier = Modifier
                                    .clicks { onToggleEditMode() },
                            )
                        },
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .then(
                                if (uiState.isEditMode.not()) {
                                    Modifier.pullRefresh(pullRefreshState)
                                } else {
                                    Modifier
                                },
                            ),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .matchParentSize(),
                        ) {
                            items(
                                items = uiState.vacancyLectures,
                                key = { it.id },
                            ) {
                                val lectureId = it.id
                                VacancyListItem(
                                    lectureDto = LectureDto.fromLecture(it), // FIXME: VacancyListItem이 LectureDto가 아닌 도메인 모델을 받게 하고 싶다.
                                    editing = uiState.isEditMode,
                                    checked = uiState.selectedLectures.contains(lectureId),
                                    onClick = {
                                        if (uiState.isEditMode) {
                                            onToggleLectureSelected(lectureId)
                                        }
                                    },
                                )
                            }
                        }
                        PullRefreshIndicator(
                            refreshing = uiState.isRefreshing,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.isEditMode,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                    ) {
                        WebViewStyleButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onShowDeleteModal(
                                    ModalProperties(
                                        title = context.getString(R.string.vacancy_delete_selected_title),
                                        positiveButton = context.getString(R.string.common_ok),
                                        negativeButton = context.getString(R.string.common_cancel),
                                        onDismiss = onHideDeleteModal,
                                        onConfirm = {
                                            onDeleteSelectedLectures()
                                            onHideDeleteModal()
                                        },
                                        content = {
                                            Text(
                                                text = context.getString(R.string.vacancy_delete_selected_message),
                                                style = SNUTTTypography.body1,
                                            )
                                        },
                                    ),
                                )
                            },
                            enabled = uiState.deleteEnabled,
                            disabledColor = SNUTTColors.VacancyGray,
                        ) {
                            Text(
                                text = stringResource(R.string.vacancy_delete_selected),
                                color = SNUTTColors.AllWhite,
                                style = SNUTTTypography.h3,
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = uiState.isEditMode.not(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset((-27).dp, (-22).dp),
                    enter = slideInVertically {
                        with(density) { 10.dp.roundToPx() }
                    } + fadeIn(),
                    exit = slideOutVertically {
                        with(density) { 10.dp.roundToPx() }
                    } + fadeOut(),
                ) {
                    SugangSnuFloatingActionButton2(
                        onClick = onOpenSugangSnu,
                    )
                }
                if (uiState.showIntroDialog) {
                    VacancyIntroDialog2(
                        onDismiss = onHideIntroDialog,
                    )
                }
            }
        }
    }
}

@Composable
fun VacancyIntroDialog2(
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

    BoxWithConstraints {
        val width = maxWidth.times(0.8f) // NOTE: Dialog의 width는 기본적으로 maxWidth * 0.82 정도라서, 그 이상의 값은 의미가 없다.
        val height = width * (640f / 600)
        Dialog(onDismissRequest = onDismiss) {
            Surface(elevation = 10.dp) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .height(height)
                        .background(SNUTTColors.White900),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TipCloseIcon(
                            modifier = Modifier
                                .size(15.dp)
                                .clicks { onDismiss() },
                            colorFilter = ColorFilter.tint(
                                if (pagerState.currentPage != 3) SNUTTColors.VacancyGray else SNUTTColors.Black900,
                            ),
                        )
                    }

                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp),
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .padding(horizontal = 17.dp)
                                .align(Alignment.Center)
                                .fillMaxWidth(),
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
                                    },
                                ),
                                contentDescription = null,
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
                                colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray),
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
                                colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray),
                            )
                        }
                    }

                    PagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier.padding(bottom = 36.dp),
                        activeColor = when (pagerState.currentPage) {
                            0 -> SNUTTColors.Red
                            1 -> SNUTTColors.Grass
                            2 -> SNUTTColors.Orange
                            else -> SNUTTColors.Sky
                        },
                        inactiveColor = if (isDarkMode()) SNUTTColors.DarkerGray else SNUTTColors.Gray,
                    )
                }
            }
        }
    }
}

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: androidx.compose.ui.graphics.Color,
    inactiveColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(6.dp)
                    .background(
                        if (pagerState.currentPage == index) activeColor else inactiveColor,
                        shape = androidx.compose.foundation.shape.CircleShape,
                    ),
            )
        }
    }
}

@Composable
fun SugangSnuFloatingActionButton2(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(110.dp, 32.dp)
            .clicks { onClick() },
        shape = RoundedCornerShape(50),
        color = SNUTTColors.SNUTTVacancy,
        elevation = 3.dp,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.vacancy_floating_button),
                style = SNUTTTypography.h5.copy(color = SNUTTColors.AllWhite),
                maxLines = 1,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenIntroPreview() {
    VacancyScreen(
        uiState = VacancyUiState.Success(
            vacancyLectures = PreviewData.sampleLectures,
            isEditMode = false,
            showIntroDialog = true,
            isRefreshing = false,
            selectedLectures = emptyList(),
            deleteEnabled = false,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onHideIntroDialog = {},
        onToggleEditMode = {},
        onRefreshVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteModal = {},
        onHideDeleteModal = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenNormalModePreview() {
    VacancyScreen(
        uiState = VacancyUiState.Success(
            vacancyLectures = PreviewData.sampleLectures,
            isEditMode = false,
            showIntroDialog = false,
            isRefreshing = false,
            selectedLectures = emptyList(),
            deleteEnabled = false,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onHideIntroDialog = {},
        onToggleEditMode = {},
        onRefreshVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteModal = {},
        onHideDeleteModal = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenEditModePreview() {
    VacancyScreen(
        uiState = VacancyUiState.Success(
            vacancyLectures = PreviewData.sampleLectures,
            isEditMode = true,
            showIntroDialog = false,
            isRefreshing = false,
            selectedLectures = emptyList(),
            deleteEnabled = false,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onHideIntroDialog = {},
        onToggleEditMode = {},
        onRefreshVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteModal = {},
        onHideDeleteModal = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenDeleteEnabledPreview() {
    VacancyScreen(
        uiState = VacancyUiState.Success(
            vacancyLectures = PreviewData.sampleLectures,
            isEditMode = true,
            showIntroDialog = false,
            isRefreshing = false,
            selectedLectures = PreviewData.sampleLectures.take(3).map { it.id },
            deleteEnabled = true,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onHideIntroDialog = {},
        onToggleEditMode = {},
        onRefreshVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteModal = {},
        onHideDeleteModal = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Preview
@Composable
private fun SugangSnuFloatingActionButtonPreview() {
    SNUTTTheme {
        SugangSnuFloatingActionButton2({})
    }
}
