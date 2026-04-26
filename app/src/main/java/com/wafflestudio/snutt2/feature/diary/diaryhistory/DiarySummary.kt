package com.wafflestudio.snutt2.feature.diary.diaryhistory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.domain.model.preview.DiaryPreviewData
import com.wafflestudio.snutt2.feature.diary.DiaryTheme
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DiarySummariesOfDay(
    date: LocalDate,
    listOfDiarySummary: List<DiarySummary>,
    expanded: Boolean,
    toggleExpended: () -> Unit,
    onDeleteDiary: (DiarySummary) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        date.format(DateTimeFormatter.ofPattern("yyyy.M.d")),
                        style = SNUTTTypography.h3.copy(fontSize = 15.sp, color = DiaryTheme.colors.textPrimary),
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("E", java.util.Locale.getDefault())),
                        style = SNUTTTypography.h3.copy(fontSize = 15.sp, color = DiaryTheme.colors.textPrimary),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .clicks { toggleExpended() }
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = listOfDiarySummary.joinToString(separator = ", ") { it.courseName },
                    style = SNUTTTypography.body1,
                    color = DiaryTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                SnuttIcon(
                    R.drawable.ic_arrow_down,
                    modifier = Modifier
                        .height(20.dp)
                        .rotate(if (expanded) 180f else 0f),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOfDiarySummary.forEach { diaryListLectureItem ->
                    DiarySummary(
                        diaryListLectureItem,
                        onClickDeleteButton = {
                            onDeleteDiary(diaryListLectureItem)
                        },
                    )
                }
            }
        }
    }

    Divider(
        modifier = Modifier.height(0.5.dp),
        color = DiaryTheme.colors.sectionDivider,
    )
}

@Composable
private fun DiarySummary(
    diarySummary: DiarySummary,
    onClickDeleteButton: () -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(stringResource(R.string.diary_summary_more_text_label)),
        style = SNUTTTypography.subtitle2,
    )
    val widthInDp = with(LocalDensity.current) { textLayoutResult.size.width.toDp() }
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = DiaryTheme.colors.summaryCardBackground,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    diarySummary.courseName,
                    style = SNUTTTypography.body1.copy(color = DiaryTheme.colors.textLabel),
                )
                SnuttIcon(
                    R.drawable.ic_trash,
                    modifier = Modifier
                        .size(28.dp, 28.dp)
                        .clicks { onClickDeleteButton() },
                    colorFilter = ColorFilter.tint(DiaryTheme.colors.iconSecondary),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                diarySummary.questionAnswers.forEach { diaryQuestionAnswer ->
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .width(widthInDp),
                            text = diaryQuestionAnswer.question,
                            style = SNUTTTypography.subtitle2,
                            fontWeight = FontWeight.Bold,
                            color = DiaryTheme.colors.textLabel,
                        )
                        Text(diaryQuestionAnswer.answer, style = SNUTTTypography.body1, color = DiaryTheme.colors.textBody)
                    }
                }
                if (diarySummary.comment != null) {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .width(widthInDp),
                            text = stringResource(R.string.diary_summary_more_text_label),
                            style = SNUTTTypography.subtitle2,
                            fontWeight = FontWeight.Bold,
                            color = DiaryTheme.colors.textLabel,
                        )
                        Text(diarySummary.comment, style = SNUTTTypography.body1, color = DiaryTheme.colors.textBody)
                    }
                }
            }
        }
    }
}

@SnuttPreview
@Composable
private fun DiarySummariesOfDay_Collapsed() {
    SnuttPreviewSurface {
        DiaryTheme {
            DiarySummariesOfDay(
                LocalDate.of(2025, 3, 20),
                listOf(DiaryPreviewData.sampleDiarySummaryLongComment),
                false,
                {},
                { _ -> },
            )
        }
    }
}

@SnuttPreview
@Composable
private fun DiarySummariesOfDay_Expanded() {
    SnuttPreviewSurface {
        DiaryTheme {
            DiarySummariesOfDay(
                LocalDate.of(2025, 3, 20),
                listOf(
                    DiaryPreviewData.sampleDiarySummaryLongComment,
                    DiaryPreviewData.sampleDiarySummaryShortComment,
                ),
                true,
                {},
                { _ -> },
            )
        }
    }
}
