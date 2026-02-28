package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.logged_in.home.settings.diary.DiaryTheme

@Composable
fun DiaryActivitySelectSection(
    activitySelectionState: ActivitySelectionState,
    onToggleActivitySelection: (activityIndex: Int) -> Unit,
    onCompleteSelectActivities: () -> Unit,
    onRestartSelectActivities: () -> Unit,
    dailyClassTypes: List<Selectable<DiaryDailyClassType>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DiaryTheme.colors.cardBackground,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(
                top = 24.dp,
                bottom = 20.dp,
                start = 20.dp,
                end = 20.dp,
            ),
    ) {
        DiaryQuestionItem(
            true,
            "오늘 무엇을 했나요?",
            options = dailyClassTypes.map { (dailyClassType, selected) ->
                Selectable(
                    dailyClassType.name,
                    selected,
                )
            },
            onChange = { index ->
                if (activitySelectionState == ActivitySelectionState.Complete) {
                    onRestartSelectActivities()
                }
                onToggleActivitySelection(index)
            },
        )
        if (activitySelectionState.isSelecting() && dailyClassTypes.anySelected()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "완료",
                    style = SNUTTTypography.button.copy(
                        fontSize = 14.sp,
                        color = DiaryTheme.colors.accentStrong,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(
                            horizontal = 12.dp,
                            vertical = 4.dp,
                        )
                        .clicks {
                            onCompleteSelectActivities()
                        },
                )
            }
        }
    }
}

@Composable
fun DiaryQuestionsSection(
    questions: List<DiaryQuestion>,
    onChange: (questionIndex: Int, answerIndex: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DiaryTheme.colors.cardBackground,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(
                top = 24.dp,
                bottom = 20.dp,
                start = 20.dp,
                end = 20.dp,
            ),
    ) {
        questions.forEachIndexed { questionIndex, (_, question, answers) ->
            DiaryQuestionItem(
                false,
                question,
                answers,
                { index ->
                    onChange(questionIndex, index)
                },
            )

            if (questionIndex != questions.lastIndex) {
                Divider(
                    color = DiaryTheme.colors.divider,
                    modifier = Modifier.padding(vertical = 20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryQuestionItem(
    isDuplicate: Boolean,
    question: String,
    options: List<Selectable<String>>,
    onChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                question,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DiaryTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.padding(6.dp))
            if (isDuplicate) {
                Text(
                    "중복 가능",
                    fontSize = 13.sp,
                    color = DiaryTheme.colors.textSecondary,
                )
            }
        }

        FlowRow(
            maxItemsInEachRow = 3,
        ) {
            options.forEachIndexed { index, (option, isSelected) ->
                Text(
                    text = option,
                    style = SNUTTTypography.button.copy(
                        fontSize = 14.sp,
                        color = if (isSelected) DiaryTheme.colors.optionSelectedText else DiaryTheme.colors.optionUnselectedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clicks {
                            onChange(index)
                        }
                        .border(
                            width = 0.6.dp,
                            color = if (isSelected) DiaryTheme.colors.optionSelectedBorder else DiaryTheme.colors.optionUnselectedBorder,
                            shape = RoundedCornerShape(17.dp),
                        )
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    color = DiaryTheme.colors.optionSelectedBackground,
                                    shape = RoundedCornerShape(
                                        17.dp,
                                    ),
                                )
                            } else {
                                Modifier
                            },
                        )
                        .padding(
                            horizontal = 24.dp,
                            vertical = 8.dp,
                        ),

                    )
            }
        }
    }
}

@Composable
fun MoreTextItem(
    moreText: String?,
    onChange: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(
                color = DiaryTheme.colors.cardBackground,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clicks { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    6.dp,
                ),
            ) {
                Text(
                    "더 남기고 싶은 말을 작성해주세요.",
                    style = SNUTTTypography.h4.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiaryTheme.colors.textPrimary,
                    ),
                )
                Text(
                    "선택",
                    style = SNUTTTypography.subtitle2.copy(
                        fontSize = 13.sp,
                        color = DiaryTheme.colors.textSecondary,
                    ),
                    lineHeight = 15.sp,
                )
            }
            ArrowDownIcon(
                modifier = Modifier
                    .height(24.dp)
                    .rotate(if (isExpanded) 180f else 0f),
                colorFilter = ColorFilter.tint(
                    DiaryTheme.colors.exitIcon,
                ),
            )
        }

        AnimatedVisibility(isExpanded) {
            val dividerColor = DiaryTheme.colors.divider
            Column(
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            color = dividerColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 0.8.dp.toPx(),
                        )
                    },
            ) {
                EditText(
                    value = moreText ?: "",
                    onValueChange = { moretext ->
                        if (moretext.length <= 200) {
                            onChange(moretext)
                        } else {
                            onChange(moretext.take(200))
                        }
                    },
                    hint = "오늘 수업에서 배운 내용, 느낀 점 등을 간단하게 적어보세요.",
                    underlineEnabled = false,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(120.dp),
                    textStyle = SNUTTTypography.body1.copy(
                        color = DiaryTheme.colors.textBody,
                    ),
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = DiaryTheme.colors.accent)) {
                            append("${moreText?.length ?: 0}")
                        }
                        withStyle(style = SpanStyle(color = DiaryTheme.colors.textSecondary)) {
                            append("/")
                            append("200")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    style = SNUTTTypography.button.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

@Composable
@Preview
fun DiaryActivitySelectingPreview() {
    DiaryTheme {
        DiaryActivitySelectSection(
            activitySelectionState = ActivitySelectionState.Complete,
            onToggleActivitySelection = {},
            onCompleteSelectActivities = {},
            onRestartSelectActivities = {},
            dailyClassTypes = DiaryPreviewData.sampleWriteUiStateSelecting.dailyClassTypes,
        )
    }
}

@Composable
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF1a1a1a)
fun DiaryActivitySelectingDarkPreview() {
    DiaryTheme(darkTheme = true) {
        DiaryActivitySelectSection(
            activitySelectionState = ActivitySelectionState.Complete,
            onToggleActivitySelection = {},
            onCompleteSelectActivities = {},
            onRestartSelectActivities = {},
            dailyClassTypes = DiaryPreviewData.sampleWriteUiStateSelecting.dailyClassTypes,
        )
    }
}

@Composable
@Preview
fun DiaryQuestionBoxPreview() {
    DiaryTheme {
        DiaryQuestionsSection(
            questions = DiaryPreviewData.getQuestionsForActivities(
                listOf("수업"),
                "컴퓨터프로그래밍",
            ),
            onChange = { _, _ -> },
        )
    }
}

@Composable
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF1a1a1a)
fun DiaryQuestionBoxDarkPreview() {
    DiaryTheme(darkTheme = true) {
        DiaryQuestionsSection(
            questions = DiaryPreviewData.getQuestionsForActivities(
                listOf("수업"),
                "컴퓨터프로그래밍",
            ),
            onChange = { _, _ -> },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DiaryQuestionPreview() {
    DiaryTheme {
        val sampleQuestion =
            DiaryPreviewData.getQuestionsForActivities(listOf("수업"))
                .first()
        DiaryQuestionItem(
            isDuplicate = false,
            question = sampleQuestion.question,
            options = sampleQuestion.selectableAnswers,
            onChange = {},
        )
    }
}

@Composable
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF1a1a1a)
fun DiaryQuestionDarkPreview() {
    DiaryTheme(darkTheme = true) {
        val sampleQuestion =
            DiaryPreviewData.getQuestionsForActivities(listOf("수업"))
                .first()
        DiaryQuestionItem(
            isDuplicate = false,
            question = sampleQuestion.question,
            options = sampleQuestion.selectableAnswers,
            onChange = {},
        )
    }
}

@Composable
@Preview
fun MoreTextPreview() {
    DiaryTheme {
        MoreTextItem(
            moreText = "시험을 예고 없이 보니 주의하시기 바랍니다.",
            onChange = {},
        )
    }
}

@Composable
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF1a1a1a)
fun MoreTextDarkPreview() {
    DiaryTheme(darkTheme = true) {
        MoreTextItem(
            moreText = "시험을 예고 없이 보니 주의하시기 바랍니다.",
            onChange = {},
        )
    }
}
