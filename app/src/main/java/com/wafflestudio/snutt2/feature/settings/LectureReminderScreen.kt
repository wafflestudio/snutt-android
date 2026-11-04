package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.components.compose.SegmentedPicker
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.SnackBarScaffold
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun LectureReminderRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: LectureReminderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(viewModel, context) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LectureReminderUiEvent.ShowToast -> context.toast(event.message)
                is LectureReminderUiEvent.ReminderUpdated -> {
                    val messageId = when (event.offset) {
                        LectureReminderOffset.NONE -> null
                        LectureReminderOffset.TEN_MINUTES_BEFORE ->
                            R.string.settings_lecture_reminder_update_success_ten_minutes_before
                        LectureReminderOffset.AT_START_TIME ->
                            R.string.settings_lecture_reminder_update_success_at_start_time
                        LectureReminderOffset.TEN_MINUTES_AFTER ->
                            R.string.settings_lecture_reminder_update_success_ten_minutes_after
                    }
                    if (messageId != null) {
                        scope.launch {
                            snackBarHostState.showSnackBar(
                                message = context.getString(messageId),
                                duration = CustomSnackBarDuration(
                                    fadeIn = 500L,
                                    inBetween = 3000L,
                                    fadeOut = 500L,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        SnackBarScaffold(snackBarHostState = snackBarHostState, hazeState = hazeState) { padding ->
            LectureReminderScreen(
                modifier = Modifier.padding(padding).hazeSource(hazeState),
                onClickBack = onNavigateBack,
                lectures = (uiState as? LectureReminderUiState.Loaded)?.lectures.orEmpty(),
                isLoading = uiState is LectureReminderUiState.Loading,
                errorMessage = (uiState as? LectureReminderUiState.Error)?.message,
                onRetry = viewModel::retry,
                onSelectOffset = viewModel::onSelectOffset,
            )
        }
    }
}

@Composable
fun LectureReminderScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    lectures: List<LectureWithReminderOption> = emptyList(),
    onSelectOffset: (String, LectureReminderOffset) -> Unit = { _, _ -> },
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_lecture_reminder_title),
            onClickNavigateBack = onClickBack,
        )
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = errorMessage,
                    style = SNUTTTypography.body1,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onRetry) {
                    Text(stringResource(R.string.reviews_error_retry))
                }
            }
            lectures.isEmpty() -> LectureReminderEmpty()
            else -> LectureReminderList(
                lectures = lectures,
                onSelectOffset = onSelectOffset,
            )
        }
    }
}

@Composable
private fun LectureReminderList(
    lectures: List<LectureWithReminderOption>,
    onSelectOffset: (String, LectureReminderOffset) -> Unit,
) {
    val labels = listOf(
        stringResource(R.string.settings_lecture_reminder_none),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_before),
        stringResource(R.string.settings_lecture_reminder_at_start_time),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_after),
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_lecture_reminder_my_reminders),
                modifier = Modifier.padding(start = 14.dp, bottom = 8.dp),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.TextAlternative),
            )
        }
        itemsIndexed(lectures, key = { _, lecture -> lecture.lectureId }) { index, lecture ->
            val shape = RoundedCornerShape(
                topStart = if (index == 0) 10.dp else 0.dp,
                topEnd = if (index == 0) 10.dp else 0.dp,
                bottomStart = if (index == lectures.lastIndex) 10.dp else 0.dp,
                bottomEnd = if (index == lectures.lastIndex) 10.dp else 0.dp,
            )
            SegmentedPicker(
                modifier = Modifier.fillMaxWidth().background(SNUTTColors.White900, shape),
                title = lecture.lectureTitle,
                options = LectureReminderOffset.entries,
                optionLabel = { labels[it.ordinal] },
                selectedOption = lecture.lectureReminderOffset,
                onOptionSelected = { onSelectOffset(lecture.lectureId, it) },
                description = null,
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.settings_lecture_reminder_guide_normal1))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.settings_lecture_reminder_guide_bold1))
                    }
                    append(stringResource(R.string.settings_lecture_reminder_guide_normal2))
                },
                modifier = Modifier.padding(top = 16.dp),
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.TextMed,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                ),
            )
        }
    }
}

@Composable
private fun LectureReminderEmpty() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 60.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.ic_cat_retry),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
            )
        }
        item {
            Text(
                text = stringResource(R.string.settings_lecture_reminder_empty_title),
                style = SNUTTTypography.body1.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.settings_lecture_reminder_empty_normal1))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.settings_lecture_reminder_empty_bold1))
                    }
                    append(stringResource(R.string.settings_lecture_reminder_empty_normal2))
                    append(stringResource(R.string.settings_lecture_reminder_empty_normal3))
                    append(stringResource(R.string.settings_lecture_reminder_empty_normal4))
                },
                style = SNUTTTypography.body2.copy(color = SNUTTColors.TextMed, lineHeight = 18.sp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@SnuttPreview
@Composable
private fun LectureReminderScreenPreview() {
    SnuttPreviewSurface {
        LectureReminderScreen(onClickBack = {})
    }
}

@SnuttPreview
@Composable
private fun LectureReminderScreenLoadedPreview() {
    SnuttPreviewSurface {
        LectureReminderScreen(
            onClickBack = {},
            lectures = LectureReminderOffset.entries.mapIndexed { index, offset ->
                LectureWithReminderOption(
                    lectureId = "$index",
                    lectureTitle = "과목 ${index + 1}",
                    lectureReminderOffset = offset,
                )
            },
        )
    }
}
