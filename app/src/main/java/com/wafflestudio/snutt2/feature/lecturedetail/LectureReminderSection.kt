package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.ui.components.compose.SegmentedPicker
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
internal fun LectureReminderSection(
    reminder: LectureWithReminderOption,
    enabled: Boolean,
    showPrimaryTableGuide: Boolean,
    onOptionChange: (LectureWithReminderOption) -> Unit,
) {
    val labels = listOf(
        stringResource(R.string.settings_lecture_reminder_none),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_before),
        stringResource(R.string.settings_lecture_reminder_at_start_time),
        stringResource(R.string.settings_lecture_reminder_ten_minutes_after),
    )
    Column {
        SegmentedPicker(
            modifier = Modifier.fillMaxWidth().background(SNUTTColors.White900),
            title = stringResource(R.string.lecture_detail_lecture_reminder_title),
            options = LectureReminderOffset.entries,
            optionLabel = { labels[it.ordinal] },
            selectedOption = reminder.lectureReminderOffset,
            onOptionSelected = { onOptionChange(reminder.copy(lectureReminderOffset = it)) },
            description = buildAnnotatedString {
                append(stringResource(R.string.lecture_detail_lecture_reminder_description))
                if (showPrimaryTableGuide) {
                    append("\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.lecture_detail_lecture_reminder_guide_bold1))
                    }
                    append(stringResource(R.string.lecture_detail_lecture_reminder_guide_normal1))
                }
            },
            enabled = enabled,
        )
        Spacer(Modifier.height(10.dp))
    }
}
