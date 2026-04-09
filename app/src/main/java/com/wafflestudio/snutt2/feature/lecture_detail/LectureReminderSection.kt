package com.wafflestudio.snutt2.feature.lecture_detail

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
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.SegmentedPicker
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
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

@Preview(showBackground = true, widthDp = 360, name = "리마인더 활성")
@Composable
private fun EnabledPreview() {
    LectureReminderSection(
        lectureWithReminderOption = PreviewData.sampleReminderOption,
        enableLectureReminderPicker = true,
        onReminderOptionChange = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "리마인더 비활성")
@Composable
private fun DisabledPreview() {
    LectureReminderSection(
        lectureWithReminderOption = PreviewData.sampleReminderOptionDefault,
        enableLectureReminderPicker = false,
        onReminderOptionChange = {},
    )
}
