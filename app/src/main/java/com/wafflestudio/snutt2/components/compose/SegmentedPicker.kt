package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset

/**
 * 여러 선택지 중 하나를 선택할 수 있는 UI 컴포넌트
 *
 * @param title 컴포넌트의 제목
 * @param options 선택 가능한 항목들의 문자열 리스트
 * @param selectedOption 현재 선택된 항목
 * @param onOptionSelected 사용자가 새로운 항목을 선택했을 때 호출되는 콜백 함수
 * @param description 컴포넌트의 설명
 * @param modifier 이 컴포저블에 적용할 Modifier
 */
@Composable
fun <T> SegmentedPicker(
    modifier: Modifier = Modifier,
    title: String?,
    options: List<T>,
    optionLabel: (T) -> String,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    description: AnnotatedString?,
    enabled: Boolean = true,
) {
    val optionSize = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp)
            .padding(bottom = 16.dp),
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = title,
                style = SNUTTTypography.body1.copy(fontSize = 15.sp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .onGloballyPositioned { coordinates ->
                    val optionWidthPx = coordinates.size.width / options.size
                    optionSize.value = with(density) { optionWidthPx.toDp() }
                }
                .clip(RoundedCornerShape(9.dp))
                .background(SNUTTColors.FillTertiary),

        ) {
            val selectedIndex = options.indexOf(selectedOption)
            val transition = updateTransition(targetState = selectedIndex, label = "buttonTransition")
            val indicatorOffset by transition.animateDp(label = "indicatorOffset") { index ->
                optionSize.value * index
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .offset(x = indicatorOffset)
                    .width(optionSize.value - 3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(SNUTTColors.BackgroundPrimary)
                    .border(
                        0.5.dp,
                        SNUTTColors.Black150,
                        RoundedCornerShape(7.dp),
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Color.Transparent,
                            )
                            .clicks { if (enabled) onOptionSelected(option) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = optionLabel(option),
                            textAlign = TextAlign.Center,
                            style = SNUTTTypography.body2.copy(
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (enabled || isSelected) SNUTTColors.Black900 else SNUTTColors.TextMed,
                            ),
                        )
                    }
                    // 마지막 아이템이 아닐 경우에만 구분선 추가
                    if (index != options.lastIndex) {
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 6.dp)
                                .width(0.5.dp),
                            color = SNUTTColors.SeparatorTransparency,
                        )
                    }
                }
            }
        }
        if (!description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = description,
                style = SNUTTTypography.subtitle1.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal, color = SNUTTColors.TextMed),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SegmentedPickerPreview() {
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
        modifier = Modifier.width(374.dp),
    ) {
        SegmentedPicker(
            title = "강의 리마인더",
            options = LectureReminderOffset.entries,
            optionLabel = { offset -> offset.getString() },
            selectedOption = LectureReminderOffset.entries[0],
            onOptionSelected = { _ -> },
            description = buildAnnotatedString { append("학기가 시작됐을 때 해당 시간에 푸시 알림을 보내드립니다.") },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnabledSegmentPickerPreview() {
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
        modifier = Modifier.width(374.dp),
    ) {
        SegmentedPicker(
            title = "강의 리마인더",
            options = LectureReminderOffset.entries,
            optionLabel = { offset -> offset.getString() },
            selectedOption = LectureReminderOffset.entries[0],
            onOptionSelected = { _ -> },
            description = buildAnnotatedString { append("최신 학기의 대표시간표 속 강의들에 적용 가능해요.") },
            enabled = false,
        )
    }
}
