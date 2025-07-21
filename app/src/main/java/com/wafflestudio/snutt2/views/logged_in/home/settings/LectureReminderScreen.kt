package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.test.SegmentedPicker
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant

@Composable
fun LectureReminderRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: LectureReminderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.lectureReminderUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.lectureReminderUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is LectureReminderUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }
                is LectureReminderUiEvent.NavigateToOnboard -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    LectureReminderScreen(
        modifier = modifier,
        uiState = uiState,
        onClickBack = onNavigateBack,
        onChangeReminderOption = viewModel::changeLectureReminderOption,
    )
}

@Composable
fun LectureReminderScreen(
    modifier: Modifier = Modifier,
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

    fun String.getLectureReminderOffset(): LectureReminderOffset = when (this) {
        lectureReminderOptions[0] -> LectureReminderOffset.NONE
        lectureReminderOptions[1] -> LectureReminderOffset.TEN_MINUTES_BEFORE
        lectureReminderOptions[2] -> LectureReminderOffset.AT_START_TIME
        lectureReminderOptions[3] -> LectureReminderOffset.TEN_MINUTES_AFTER
        else -> LectureReminderOffset.NONE
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
            is LectureReminderUiState.Success -> {
                SettingColumn(
                    title = stringResource(R.string.settings_lecture_reminder_my_reminders),
                ) {
                    LazyColumn {
                        items(
                            items = uiState.data.values.toList(),
                            key = { it.lectureId },
                        ) { lectureWithReminderOption ->
                            SegmentedPicker(
                                title = lectureWithReminderOption.lectureTitle,
                                options = lectureReminderOptions,
                                selectedOption = lectureWithReminderOption.lectureReminderOffset.getString(),
                                onOptionSelected = { offset ->
                                    onChangeReminderOption(
                                        lectureWithReminderOption.lectureId,
                                        LectureWithReminderOption(
                                            lectureId = lectureWithReminderOption.lectureId,
                                            lectureTitle = lectureWithReminderOption.lectureTitle,
                                            lectureReminderOffset = offset.getLectureReminderOffset(),
                                        ),
                                    )
                                },
                                modifier = Modifier.background(SNUTTColors.White900),
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.settings_lecture_reminder_guide),
                    style = SNUTTTypography.body2.copy(
                        color = MaterialTheme.colors.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                )
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

@Preview
@Composable
fun LectureReminderPagePreview() {
    LectureReminderScreen(
        modifier = Modifier
            .height(959.dp)
            .width(375.dp),
        uiState = LectureReminderUiState.Success(PreviewData.sampleLectureReminderOptions),
        onClickBack = {},
        onChangeReminderOption = { _, _ -> },
    )
}
