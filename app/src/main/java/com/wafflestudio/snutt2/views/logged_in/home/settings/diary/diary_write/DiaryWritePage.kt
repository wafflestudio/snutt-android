package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DiaryWriteRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateReview: (lectureId: String) -> Unit,
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

                is DiaryWriteUiEvent.Return -> {
                    onNavigateBack()
                }

                is DiaryWriteUiEvent.ForceLogout -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    DiaryWriteScreen(
        modifier = modifier,
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
        onClickWriteReviewButton = {
            // TODO
            // next ID는 어떻게 관리할지 고민..
        },
        onClickGoHomeButton = onNavigateHome,
    )
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

    val toScrollOffset =
        remember { mutableIntStateOf(0) }
    var commentText by remember {
        mutableStateOf("")
    }

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(color = SNUTTColors.White)
                .border(width = 0.2.dp, color = SNUTTColors.EditTextUnderline)
                .padding(top = 44.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "오늘 수강한 '${uiState.lectureName}' 에 대한 의견을 남겨보세요.",
                    style = SNUTTTypography.h3.copy(fontSize = 17.sp, lineHeight = 25.sp),
                )

                Text(
                    text = "더보기 > 강의일기장에서 확인할 수 있어요.",
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
                )
            }

            ExitIcon(
                modifier = Modifier
                    .padding(start = 57.dp)
                    .width(24.dp)
                    .clicks {
                        onClickBackButton()
                    },
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(color = SNUTTColors.Gray),
        ) {
            DiaryActivitySelectSection(
                uiState.activitySelectingState,
                onToggleActivitySelection,
                onCompleteSelectActivities = {
                    if (uiState.activitySelectingState.isSelecting()) {
                        scope.launch {
                            delay(100)
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
                    MoreTextItem(
                        moreText = commentText,
                        onChange = { text ->
                            commentText = text
                        },
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 12.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 40.dp,
                            )
                            .align(Alignment.End)
                            .background(
                                color = if (uiState.allQuestionAnswered()) {
                                    SNUTTColors.MainBlue
                                } else {
                                    SNUTTColors.TableGrid
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
                        text = "다음",
                        style = if (uiState.allQuestionAnswered()) {
                            SNUTTTypography.button.copy(
                                color = SNUTTColors.White,
                                fontSize = 15.sp,
                            )
                        } else {
                            SNUTTTypography.button.copy(
                                color = SNUTTColors.Gray20,
                                fontSize = 15.sp,
                            )
                        },
                    )
                }
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
            .background(SNUTTColors.White)
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
                text = "강의일기가 등록되었습니다.",
                style = SNUTTTypography.h3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "작성한 강의일기는 더보기>강의일기장에서 확인할 수 있어요.",
                style = SNUTTTypography.body1.copy(color = SNUTTColors.Black.copy(alpha = 0.5f)),
                textAlign = TextAlign.Center,
            )
            if (uiState.nextAction != DiaryNextAction.Nothing) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .border(
                            width = 1.dp,
                            color = SNUTTColors.Gray,
                            shape = RoundedCornerShape(30.dp),
                        )
                        .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
                        .clicks {
                            when (uiState.nextAction) {
                                DiaryNextAction.WriteNext -> onClickWriteNextButton()
                                DiaryNextAction.WriteReview -> onClickWriteReviewButton()
                                else -> {}
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
                                DiaryNextAction.WriteNext -> "더 기록하기"
                                DiaryNextAction.WriteReview -> "강의평 남기기"
                                else -> ""
                            },
                            style = SNUTTTypography.button.copy(fontSize = 15.sp),
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
                .background(color = SNUTTColors.SNUTTTheme, shape = RoundedCornerShape(6.dp))
                .padding(12.dp),
            text = "홈으로",
            textAlign = TextAlign.Center,
            style = SNUTTTypography.button.copy(color = SNUTTColors.White, fontSize = 15.sp),
        )
    }
}

@Composable
@Preview(heightDp = 1030)
private fun DiaryWritingPreview() {
    DiaryWriting(
        uiState = DiaryMockData.sampleWriteUiState,
        onToggleActivitySelection = {},
        onCompleteSelectActivities = {},
        onRestartSelectActivities = {},
        onToggleAnswer = { _, _ -> },
        onSubmitDiary = {},
        onClickBackButton = {},
    )
}

@Composable
@Preview
private fun DiaryCompletePreview() {
    DiaryComplete(
        uiState = DiaryWriteUiState.Complete(DiaryNextAction.WriteReview),
        onClickGoHomeButton = {},
        onClickWriteNextButton = {},
        onClickWriteReviewButton = {},
    )
}
