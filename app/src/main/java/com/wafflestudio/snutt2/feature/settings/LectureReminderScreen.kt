package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.components.compose.SegmentedPicker
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBar
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarDuration
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHost
import com.wafflestudio.snutt2.ui.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.components.compose.snackbar.dismiss
import com.wafflestudio.snutt2.ui.preview.LecturePreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun LectureReminderRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: LectureReminderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.lectureReminderUiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { CustomSnackBarHostState() }
    val hazeState = rememberHazeState()

    val tenMinutesBeforeMessage = stringResource(R.string.settings_lecture_reminder_update_success_ten_minutes_before)
    val atStartTimeMessage = stringResource(R.string.settings_lecture_reminder_update_success_at_start_time)
    val tenMinutesAfterMessage = stringResource(R.string.settings_lecture_reminder_update_success_ten_minutes_after)

    LaunchedEffect(Unit) {
        viewModel.lectureReminderUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is LectureReminderUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is LectureReminderUiEvent.ShowUpdateSuccessSnackBar -> {
                    val message = when (uiEvent.offset) {
                        LectureReminderOffset.NONE -> ""
                        LectureReminderOffset.TEN_MINUTES_BEFORE -> tenMinutesBeforeMessage
                        LectureReminderOffset.AT_START_TIME -> atStartTimeMessage
                        LectureReminderOffset.TEN_MINUTES_AFTER -> tenMinutesAfterMessage
                    }
                    if (message.isNotEmpty()) {
                        launch {
                            snackBarHostState.currentSnackBarData.dismiss()
                            snackBarHostState.showSnackBar(
                                message = message,
                                duration = CustomSnackBarDuration(
                                    fadeIn = 500L,
                                    inBetween = 3000L,
                                    fadeOut = 500L,
                                ),
                            )
                        }
                    }
                }

                is LectureReminderUiEvent.LoggedOut -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState,
                snackBar = { data ->
                    val currentSnackBarData = snackBarHostState.currentSnackBarData
                    CustomSnackBar(
                        snackBarData = currentSnackBarData,
                        passedData = data,
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = SNUTTColors.SnackbarBackground,
                        contentStyle = SNUTTTypography.body1.copy(color = SNUTTColors.White, fontWeight = FontWeight.Medium),
                        actionLabelStyle = SNUTTTypography.body1.copy(color = SNUTTColors.MilkMint, fontWeight = FontWeight.SemiBold),
                        hazeState = hazeState,
                    )
                },
            )
        },
    ) { padding ->
        LectureReminderScreen(
            modifier = modifier.hazeSource(hazeState),
            padding = padding,
            uiState = uiState,
            onClickBack = onNavigateBack,
            onChangeReminderOption = viewModel::changeLectureReminderOption,
        )
    }
}

@Composable
fun LectureReminderScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues.Absolute(),
    uiState: LectureReminderUiState,
    onClickBack: () -> Unit,
    onChangeReminderOption: (String, LectureWithReminderOption) -> Unit,
) {
    val lectureReminderOptions = listOf(
        stringResource(R.string.settings_lecture_reminder_none),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_before),
        stringResource(R.string.settings_lecture_reminder_at_start_time),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_after),
    )

    fun LectureReminderOffset.getString(): String = when (this) {
        LectureReminderOffset.NONE -> lectureReminderOptions[0]
        LectureReminderOffset.TEN_MINUTES_BEFORE -> lectureReminderOptions[1]
        LectureReminderOffset.AT_START_TIME -> lectureReminderOptions[2]
        LectureReminderOffset.TEN_MINUTES_AFTER -> lectureReminderOptions[3]
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_lecture_reminder_title),
            onClickNavigateBack = onClickBack,
        )

        when (uiState) {
            is LectureReminderUiState.Loading -> LectureReminderLoading()
            is LectureReminderUiState.Error -> LectureReminderError()
            is LectureReminderUiState.NoPrimaryTimetable -> LectureReminderEmpty()
            is LectureReminderUiState.Success -> {
                Column {
                    SettingColumn(
                        modifier = Modifier.weight(1f, fill = false),
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = stringResource(R.string.settings_lecture_reminder_my_reminders),
                                    style = SNUTTTypography.body2.copy(
                                        color = SNUTTColors.TextAlternative,
                                    ),
                                    modifier = Modifier
                                        .padding(top = 24.dp, start = 30.dp, bottom = 8.dp),
                                )
                            }

                            items(
                                items = uiState.data.values.toList(),
                                key = { it.lectureId },
                            ) { lectureWithReminderOption ->
                                SegmentedPicker(
                                    title = lectureWithReminderOption.lectureTitle,
                                    options = LectureReminderOffset.entries,
                                    optionLabel = { offset -> offset.getString() },
                                    selectedOption = lectureWithReminderOption.lectureReminderOffset,
                                    onOptionSelected = { offset ->
                                        onChangeReminderOption(
                                            lectureWithReminderOption.lectureId,
                                            LectureWithReminderOption(
                                                lectureId = lectureWithReminderOption.lectureId,
                                                lectureTitle = lectureWithReminderOption.lectureTitle,
                                                lectureReminderOffset = offset,
                                            ),
                                        )
                                    },
                                    description = null,
                                    modifier = Modifier.background(SNUTTColors.White900),
                                )
                            }

                            item {
                                val annotatedString = buildAnnotatedString {
                                    append(stringResource(R.string.settings_lecture_reminder_guide_normal1))
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                        append(stringResource(R.string.settings_lecture_reminder_guide_bold1))
                                    }
                                    append(stringResource(R.string.settings_lecture_reminder_guide_normal2))
                                }

                                Text(
                                    text = annotatedString,
                                    style = SNUTTTypography.body2.copy(
                                        color = SNUTTColors.TextMed,
                                        fontSize = 13.sp,
                                    ),
                                    modifier = Modifier
                                        .padding(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LectureReminderLoading() {
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

@Composable
fun LectureReminderError() {
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

@Composable
fun LectureReminderEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(vertical = 80.dp, horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cat_retry),
                contentDescription = "Cat_Retry",
                modifier = Modifier.width(60.dp),
            )
            Text(
                text = stringResource(R.string.settings_lecture_reminder_empty_title),
                style = SNUTTTypography.h3.copy(
                    color = SNUTTColors.Black900,
                    fontSize = 15.sp,
                ),
            )
            val annotatedString = buildAnnotatedString {
                append(stringResource(R.string.settings_lecture_reminder_empty_normal1))
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.settings_lecture_reminder_empty_bold1))
                }
                append(stringResource(R.string.settings_lecture_reminder_empty_normal2))
                append(stringResource(R.string.settings_lecture_reminder_empty_normal3))
                append(stringResource(R.string.settings_lecture_reminder_empty_normal4))
            }
            Text(
                textAlign = TextAlign.Center,
                text = annotatedString,
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.TextMed,
                    fontSize = 13.sp,
                ),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun LectureReminderScreen_Success() {
    SnuttPreviewSurface {
        LectureReminderScreen(
            uiState = LectureReminderUiState.Success(
                data = LecturePreviewData.sampleLectureReminderOptions,
                timetableId = "",
            ),
            onClickBack = {},
            onChangeReminderOption = { _, _ -> },
        )
    }
}

@SnuttPreview
@Composable
private fun LectureReminderScreen_NoPrimaryTimetable() {
    SnuttPreviewSurface {
        LectureReminderScreen(
            uiState = LectureReminderUiState.NoPrimaryTimetable,
            onClickBack = {},
            onChangeReminderOption = { _, _ -> },
        )
    }
}

@SnuttPreview
@Composable
private fun LectureReminderEmpty_Default() {
    SnuttPreviewSurface {
        LectureReminderEmpty()
    }
}

@SnuttPreview
@Composable
private fun LectureReminderLoading_Default() {
    SnuttPreviewSurface {
        LectureReminderLoading()
    }
}

@SnuttPreview
@Composable
private fun LectureReminderError_Default() {
    SnuttPreviewSurface {
        LectureReminderError()
    }
}
