package com.wafflestudio.snutt2.feature.diary.diarywrite

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.preview.DiaryPreviewData
import com.wafflestudio.snutt2.feature.diary.DiaryTheme
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun DiaryWriteRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateReview: () -> Unit,
    onNavigateNextDiaryWrite: (lectureId: String, courseTitle: String) -> Unit,
    viewModel: DiaryWriteViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is DiaryWriteUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is DiaryWriteUiEvent.NextDiary -> {
                    onNavigateNextDiaryWrite(
                        uiEvent.lectureId,
                        uiEvent.courseTitle,
                    )
                }

                is DiaryWriteUiEvent.NavigateReview -> {
                    onNavigateReview()
                }

                is DiaryWriteUiEvent.NavigateHome -> {
                    onNavigateHome()
                }

                is DiaryWriteUiEvent.Return -> {
                    onNavigateBack()
                }

                is DiaryWriteUiEvent.ForceLogout -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    DiaryTheme {
        DiaryWriteScreen(
            modifier = modifier.logImpression(AnalyticsScreen.DiaryWrite),
            uiState = uiState,
            onToggleActivitySelection = viewModel::toggleActivitySelection,
            onCompleteSelectActivities = viewModel::completeActivitySelection,
            onRestartSelectActivities = {
                viewModel.setSelectingActivitiesState(
                    ActivitySelectionState.ReSelecting,
                )
            },
            onToggleAnswer = viewModel::toggleAnswer,
            onSubmitDiary = viewModel::saveDiaryWrite,
            onClickBackButton = onNavigateBack,
            onClickWriteNextButton = viewModel::writeNextDiary,
            onClickWriteReviewButton = viewModel::goReview,
            onClickGoHomeButton = viewModel::goHome,
        )
    }
}

@Composable
private fun DiaryWriteScreen(
    modifier: Modifier = Modifier,
    uiState: DiaryWriteUiState,
    onToggleActivitySelection: (activityIndex: Int) -> Unit,
    onCompleteSelectActivities: () -> Unit,
    onRestartSelectActivities: () -> Unit,
    onToggleAnswer: (questionIndex: Int, answerIndex: Int) -> Unit,
    onSubmitDiary: (comment: String) -> Unit,
    onClickBackButton: () -> Unit,
    onClickWriteNextButton: () -> Unit,
    onClickWriteReviewButton: () -> Unit,
    onClickGoHomeButton: () -> Unit,
) {
    when (uiState) {
        DiaryWriteUiState.Error -> {}
        DiaryWriteUiState.Loading -> {}
        is DiaryWriteUiState.Complete -> DiaryComplete(
            modifier,
            uiState,
            onClickWriteNextButton,
            onClickWriteReviewButton,
            onClickGoHomeButton,
        )

        is DiaryWriteUiState.Write -> DiaryWriting(
            modifier,
            uiState,
            onToggleActivitySelection,
            onCompleteSelectActivities,
            onRestartSelectActivities,
            onToggleAnswer,
            onSubmitDiary,
            onClickBackButton,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DiaryWriting(
    modifier: Modifier = Modifier,
    uiState: DiaryWriteUiState.Write,
    onToggleActivitySelection: (activityIndex: Int) -> Unit,
    onCompleteSelectActivities: () -> Unit,
    onRestartSelectActivities: () -> Unit,
    onToggleAnswer: (questionIndex: Int, answerIndex: Int) -> Unit,
    onSubmitDiary: (comment: String) -> Unit,
    onClickBackButton: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            bringIntoViewRequester.bringIntoView()
        }
    }

    val toScrollOffset =
        remember { mutableIntStateOf(0) }
    var commentText by remember {
        mutableStateOf("")
    }
    var isMoreTextExpanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(color = DiaryTheme.colors.headerBackground)
                .border(width = 0.2.dp, color = DiaryTheme.colors.headerBorder)
                .padding(top = 44.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.diary_write_header_title, uiState.lectureName),
                    style = SNUTTTypography.h3.copy(fontSize = 17.sp, lineHeight = 25.sp, color = DiaryTheme.colors.textPrimary),
                )

                Text(
                    text = stringResource(R.string.diary_write_header_subtitle),
                    style = SNUTTTypography.body1.copy(color = DiaryTheme.colors.textSecondary),
                )
            }

            ExitIcon(
                modifier = Modifier
                    .padding(start = 57.dp)
                    .width(24.dp)
                    .clicks {
                        onClickBackButton()
                    },
                colorFilter = ColorFilter.tint(
                    DiaryTheme.colors.exitIcon,
                ),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(color = DiaryTheme.colors.bodyBackground)
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DiaryActivitySelectSection(
                uiState.activitySelectingState,
                onToggleActivitySelection,
                onCompleteSelectActivities = {
                    if (uiState.activitySelectingState.isSelecting()) {
                        val prevOffset = toScrollOffset.intValue
                        scope.launch {
                            snapshotFlow { toScrollOffset.intValue }
                                .first { it > 0 && it != prevOffset }
                            scrollState.animateScrollTo(
                                toScrollOffset.intValue,
                                animationSpec = spring(
                                    Spring.DampingRatioLowBouncy,
                                    Spring.StiffnessLow,
                                ),
                            )
                        }
                    }
                    onCompleteSelectActivities()
                },
                onRestartSelectActivities,
                uiState.dailyClassTypes,
            )

            if (uiState.activitySelectingState != ActivitySelectionState.InitialSelecting) {
                Column(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        toScrollOffset.intValue =
                            coordinates.positionInParent().y.toInt()
                    },
                ) {
                    DiaryQuestionsSection(
                        uiState.questions,
                        onChange = onToggleAnswer,
                    )
                }

                MoreTextItem(
                    moreText = commentText,
                    onChange = { text ->
                        commentText = text
                    },
                    isExpanded = isMoreTextExpanded,
                    onExpandedChange = { isMoreTextExpanded = it },
                )

                Text(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .align(Alignment.End)
                        .background(
                            color = if (uiState.allQuestionAnswered()) {
                                DiaryTheme.colors.submitButtonEnabled
                            } else {
                                DiaryTheme.colors.submitButtonDisabled
                            },
                            shape = RoundedCornerShape(
                                6.dp,
                            ),
                        )
                        .clicks(enabled = uiState.allQuestionAnswered()) {
                            onSubmitDiary(commentText)
                        }
                        .padding(
                            vertical = 12.dp,
                            horizontal = 48.dp,
                        ),
                    text = stringResource(R.string.diary_write_next_button),
                    style = if (uiState.allQuestionAnswered()) {
                        SNUTTTypography.button.copy(
                            color = DiaryTheme.colors.submitButtonTextEnabled,
                            fontSize = 15.sp,
                        )
                    } else {
                        SNUTTTypography.button.copy(
                            color = DiaryTheme.colors.submitButtonTextDisabled,
                            fontSize = 15.sp,
                        )
                    },
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DiaryComplete(
    modifier: Modifier = Modifier,
    uiState: DiaryWriteUiState.Complete,
    onClickWriteNextButton: () -> Unit,
    onClickWriteReviewButton: () -> Unit,
    onClickGoHomeButton: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DiaryTheme.colors.screenBackground)
            .padding(start = 32.dp, end = 32.dp, bottom = 40.dp, top = 248.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 45.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_cat_complete),
                contentDescription = "",
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(R.string.diary_write_complete_title),
                style = SNUTTTypography.h3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.diary_write_complete_subtitle),
                style = SNUTTTypography.body1.copy(color = DiaryTheme.colors.textSubtitle),
                textAlign = TextAlign.Center,
            )
            if (uiState.nextAction != DiaryNextAction.Nothing) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .border(
                            width = 1.dp,
                            color = DiaryTheme.colors.nextActionBorder,
                            shape = RoundedCornerShape(30.dp),
                        )
                        .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
                        .clicks {
                            when (uiState.nextAction) {
                                is DiaryNextAction.WriteNext -> onClickWriteNextButton()
                                is DiaryNextAction.WriteReview -> onClickWriteReviewButton()
                                DiaryNextAction.Nothing -> {}
                            }
                        }
                        .padding(start = 29.dp, end = 21.dp, top = 13.dp, bottom = 13.dp),
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = when (uiState.nextAction) {
                                is DiaryNextAction.WriteNext -> stringResource(R.string.diary_write_more_button)
                                is DiaryNextAction.WriteReview -> stringResource(R.string.diary_write_review_button)
                                DiaryNextAction.Nothing -> ""
                            },
                            style = SNUTTTypography.button.copy(fontSize = 15.sp, color = DiaryTheme.colors.textPrimary),
                        )
                        Icon(
                            modifier = Modifier.size(20.dp, 20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                            contentDescription = "",
                        )
                    }
                }
            }
        }

        Text(
            modifier = Modifier
                .clicks { onClickGoHomeButton() }
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(color = DiaryTheme.colors.theme, shape = RoundedCornerShape(6.dp))
                .padding(12.dp),
            text = stringResource(R.string.diary_write_home_button),
            textAlign = TextAlign.Center,
            style = SNUTTTypography.button.copy(color = DiaryTheme.colors.submitButtonTextEnabled, fontSize = 15.sp),
        )
    }
}

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1100)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1100)
@Composable
private fun DiaryWriting_InProgress() {
    SnuttPreviewSurface {
        DiaryTheme {
            DiaryWriting(
                uiState = DiaryPreviewData.sampleWriteUiState,
                onToggleActivitySelection = {},
                onCompleteSelectActivities = {},
                onRestartSelectActivities = {},
                onToggleAnswer = { _, _ -> },
                onSubmitDiary = {},
                onClickBackButton = {},
            )
        }
    }
}

@SnuttPreview
@Composable
private fun DiaryComplete_Default() {
    SnuttPreviewSurface {
        DiaryTheme {
            DiaryComplete(
                uiState = DiaryWriteUiState.Complete(DiaryNextAction.WriteReview),
                onClickGoHomeButton = {},
                onClickWriteNextButton = {},
                onClickWriteReviewButton = {},
            )
        }
    }
}
