package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.components.compose.SegmentedPicker
import com.wafflestudio.snutt2.ui.preview.LecturePreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
internal fun LectureReminderSection(
    lectureWithReminderOption: LectureWithReminderOption,
    enableLectureReminderPicker: Boolean,
    onReminderOptionChange: (LectureWithReminderOption) -> Unit,
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
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        SegmentedPicker(
            title = stringResource(R.string.lecture_detail_lecture_reminder_title),
            options = LectureReminderOffset.entries,
            optionLabel = { it.getString() },
            selectedOption = lectureWithReminderOption.lectureReminderOffset,
            onOptionSelected = { offset ->
                onReminderOptionChange(
                    LectureWithReminderOption(
                        lectureId = lectureWithReminderOption.lectureId,
                        lectureTitle = lectureWithReminderOption.lectureTitle,
                        lectureReminderOffset = offset,
                    ),
                )
            },
            description = buildAnnotatedString {
                if (enableLectureReminderPicker) {
                    append(stringResource(R.string.lecture_detail_lecture_reminder_description))
                } else {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(R.string.lecture_detail_lecture_reminder_guide_bold1))
                    }
                    append(stringResource(R.string.lecture_detail_lecture_reminder_guide_normal1))
                }
            },
            enabled = enableLectureReminderPicker,
        )
    }
}

@SnuttPreview
@Composable
private fun LectureReminderSection_Enabled() {
    SnuttPreviewSurface {
        LectureReminderSection(
            lectureWithReminderOption = LecturePreviewData.sampleReminderOption,
            enableLectureReminderPicker = true,
            onReminderOptionChange = {},
        )
    }
}

@SnuttPreview
@Composable
private fun LectureReminderSection_Disabled() {
    SnuttPreviewSurface {
        LectureReminderSection(
            lectureWithReminderOption = LecturePreviewData.sampleReminderOptionDefault,
            enableLectureReminderPicker = false,
            onReminderOptionChange = {},
        )
    }
}
