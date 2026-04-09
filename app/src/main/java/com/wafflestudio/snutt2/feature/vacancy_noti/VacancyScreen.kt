package com.wafflestudio.snutt2.feature.vacancy_noti

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.QuestionCircleIcon
import com.wafflestudio.snutt2.ui.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.TipCloseIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.util.toast
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import kotlinx.coroutines.launch

@Composable
fun VacancyRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: VacancyViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.vacancyUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.vacancyUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is VacancyUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is VacancyUiEvent.LoggedOut -> {
                    onNavigateOnboard()
                }

                is VacancyUiEvent.OpenWebPage -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uiEvent.url.toUri()))
                }
            }
        }
    }

    BackHandler(enabled = uiState.isEditMode) {
        viewModel.toggleEditMode()
    }

    VacancyScreen(
        modifier = modifier,
        uiState = uiState,
        onClickBack = onNavigateBack,
        onShowIntroDialog = viewModel::showIntroDialog,
        onDismissDialog = viewModel::dismissDialog,
        onToggleEditMode = viewModel::toggleEditMode,
        onReloadVacancyLectures = viewModel::reloadVacancyLectures,
        onToggleLectureSelected = viewModel::toggleLectureSelected,
        onShowDeleteDialog = viewModel::showDeleteDialog,
        onDeleteSelectedLectures = viewModel::deleteSelectedLectures,
        onOpenSugangSnu = viewModel::openSugangSnu,
    )
}

@Composable
fun VacancyScreen(
    modifier: Modifier = Modifier,
    uiState: VacancyUiState,
    onClickBack: () -> Unit,
    onShowIntroDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onToggleEditMode: () -> Unit,
    onReloadVacancyLectures: () -> Unit,
    onToggleLectureSelected: (String) -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDeleteSelectedLectures: () -> Unit,
    onOpenSugangSnu: () -> Unit,
) {
    when (uiState.contentState) {
        VacancyUiState.ContentState.Loading -> VacancyLoading(
            modifier = modifier,
            onClickBack = onClickBack,
        )

        VacancyUiState.ContentState.Error -> VacancyError(
            modifier = modifier,
            onClickBack = onClickBack,
        )

        VacancyUiState.ContentState.Empty -> VacancyEmpty(
            modifier = modifier,
            uiState = uiState,
            onClickBack = onClickBack,
            onShowIntroDialog = onShowIntroDialog,
            onDismissDialog = onDismissDialog,
            onReloadVacancyLectures = onReloadVacancyLectures,
            onOpenSugangSnu = onOpenSugangSnu,
        )

        is VacancyUiState.ContentState.Loaded -> VacancySuccess(
            modifier = modifier,
            uiState = uiState,
            onClickBack = onClickBack,
            onShowIntroDialog = onShowIntroDialog,
            onDismissDialog = onDismissDialog,
            onToggleEditMode = onToggleEditMode,
            onReloadVacancyLectures = onReloadVacancyLectures,
            onToggleLectureSelected = onToggleLectureSelected,
            onShowDeleteDialog = onShowDeleteDialog,
            onDeleteSelectedLectures = onDeleteSelectedLectures,
            onOpenSugangSnu = onOpenSugangSnu,
        )
    }
}

@Composable
fun VacancyLoading(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.vacancy_app_bar_title),
            onClickNavigateBack = onClickBack,
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun VacancyError(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.vacancy_app_bar_title),
            onClickNavigateBack = onClickBack,
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.error_unknown),
                color = MaterialTheme.colors.onBackground,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun VacancyEmpty(
    modifier: Modifier = Modifier,
    uiState: VacancyUiState,
    onClickBack: () -> Unit,
    onShowIntroDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onReloadVacancyLectures: () -> Unit,
    onOpenSugangSnu: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, onReloadVacancyLectures)
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
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(modifier = Modifier.matchParentSize()) {}
                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }

        VacancyPlaceholder(
            onClickDetail = onShowIntroDialog,
            modifier = Modifier.align(Alignment.Center),
        )

        SugangSnuFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset((-27).dp, (-22).dp),
            onClick = onOpenSugangSnu,
        )

        if (uiState.dialogState is VacancyUiState.DialogState.Intro) {
            VacancyIntroDialog(
                onDismiss = onDismissDialog,
            )
        }
    }
}

@Composable
fun VacancyPlaceholder(
    onClickDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(if (isDarkMode()) R.drawable.img_vacancy_empty_dark else R.drawable.img_vacancy_empty),
            contentDescription = null,
            modifier = Modifier
                .height(180.dp)
                .fillMaxSize(),
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .clicks { onClickDetail() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QuestionCircleIcon(
                modifier = Modifier.size(12.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.DARKER_GRAY),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.vacancy_empty_detail),
                textDecoration = TextDecoration.Underline,
                style = SNUTTTypography.subtitle2.copy(
                    fontSize = 12.sp,
                    color = SNUTTColors.DARKER_GRAY,
                ),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun VacancySuccess(
    modifier: Modifier = Modifier,
    uiState: VacancyUiState,
    onClickBack: () -> Unit,
    onShowIntroDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onToggleEditMode: () -> Unit,
    onReloadVacancyLectures: () -> Unit,
    onToggleLectureSelected: (String) -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDeleteSelectedLectures: () -> Unit,
    onOpenSugangSnu: () -> Unit,
) {
    val content = uiState.contentState as VacancyUiState.ContentState.Loaded
    val density = LocalDensity.current
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, onReloadVacancyLectures)
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
                        items = content.vacancyLecturesWithSelection,
                        key = { it.item.id },
                    ) {
                        val lectureId = it.item.id
                        VacancyListItem(
                            lecture = it,
                            editing = uiState.isEditMode,
                            onClick = {
                                // FIXME: 이런 로직도 다 ViewModel 로 넘기기
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

            AnimatedContent(
                targetState = uiState.isEditMode,
            ) { isEditMode ->
                if (isEditMode) {
                    WebViewStyleButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = onShowDeleteDialog,
                        enabled = content.deleteButtonEnabled,
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
            SugangSnuFloatingActionButton(
                onClick = onOpenSugangSnu,
            )
        }
        when (uiState.dialogState) {
            VacancyUiState.DialogState.None -> {}
            VacancyUiState.DialogState.Intro -> {
                VacancyIntroDialog(
                    onDismiss = onDismissDialog,
                )
            }

            VacancyUiState.DialogState.ConfirmDeleteSelected -> {
                CustomDialog(
                    onDismiss = onDismissDialog,
                    onConfirm = onDeleteSelectedLectures,
                    title = stringResource(R.string.vacancy_delete_selected_title),
                ) {
                    Text(
                        text = stringResource(R.string.vacancy_delete_selected_message),
                        style = SNUTTTypography.body1,
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VacancyIntroDialog(
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

    BoxWithConstraints {
        val width =
            maxWidth.times(0.8f) // NOTE: Dialog의 width는 기본적으로 maxWidth * 0.82 정도라서, 그 이상의 값은 의미가 없다.
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
    activeColor: Color,
    inactiveColor: Color,
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
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
fun SugangSnuFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(110.dp, 32.dp)
            .clicks { onClick() },
        shape = RoundedCornerShape(50),
        color = SNUTTColors.SNUTTDarkMintBlue,
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
fun VacancyScreenErrorPreview() {
    VacancyScreen(
        uiState = VacancyUiState(contentState = VacancyUiState.ContentState.Error),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenLoadingPreview() {
    VacancyScreen(
        uiState = VacancyUiState(),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenIntroPreview() {
    VacancyScreen(
        uiState = VacancyUiState(
            contentState = VacancyUiState.ContentState.Loaded(
                vacancyLecturesWithSelection = PreviewData.sampleLectures.map { it.toDataWithState(false) },
            ),
            dialogState = VacancyUiState.DialogState.Intro,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenNormalModePreview() {
    VacancyScreen(
        uiState = VacancyUiState(
            contentState = VacancyUiState.ContentState.Loaded(
                vacancyLecturesWithSelection = PreviewData.sampleLectures.map { it.toDataWithState(false) },
            ),
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenEmptyPreview() {
    VacancyScreen(
        uiState = VacancyUiState(contentState = VacancyUiState.ContentState.Empty),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenEditModePreview() {
    VacancyScreen(
        uiState = VacancyUiState(
            contentState = VacancyUiState.ContentState.Loaded(
                vacancyLecturesWithSelection = PreviewData.sampleLectures.map { it.toDataWithState(false) },
            ),
            isEditMode = true,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Composable
@Preview(showBackground = true)
fun VacancyScreenDeleteEnabledPreview() {
    VacancyScreen(
        uiState = VacancyUiState(
            contentState = VacancyUiState.ContentState.Loaded(
                vacancyLecturesWithSelection = PreviewData.sampleLectures.mapIndexed { index, it ->
                    it.toDataWithState(index < 3)
                },
                deleteButtonEnabled = true,
            ),
            isEditMode = true,
        ),
        onClickBack = {},
        onShowIntroDialog = {},
        onDismissDialog = {},
        onToggleEditMode = {},
        onReloadVacancyLectures = {},
        onToggleLectureSelected = { _ -> },
        onShowDeleteDialog = {},
        onDeleteSelectedLectures = {},
        onOpenSugangSnu = {},
    )
}

@Preview
@Composable
private fun SugangSnuFloatingActionButtonPreview() {
    SNUTTTheme {
        SugangSnuFloatingActionButton({})
    }
}
