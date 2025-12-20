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
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryActivitySelectSection(
    activitySelectionState: ActivitySelectionState,
    onToggleActivitySelection: (activityIndex: Int) -> Unit,
    onCompleteSelectActivities: () -> Unit,
    onRestartSelectActivities: () -> Unit,
    dailyClassTypes: List<Selectable<DiaryDailyClassType>>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(
                color = SNUTTColors.White,
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
        if (activitySelectionState.isSelecting()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "완료",
                    style = SNUTTTypography.button.copy(
                        fontSize = 14.sp,
                        color = SNUTTColors.DarkMainBlue,
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
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(
                color = SNUTTColors.White,
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
                    color = SNUTTColors.Gray,
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
            )
            Spacer(modifier = Modifier.padding(6.dp))
            if (isDuplicate) {
                Text(
                    "중복 가능",
                    fontSize = 13.sp,
                    color = SNUTTColors.EditTextLabel,
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
                        color = if (isSelected) SNUTTColors.DarkMainBlue else SNUTTColors.DarkerGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clicks {
                            onChange(index)
                        }
                        .border(
                            width = 0.6.dp,
                            color = if (isSelected) SNUTTColors.MainBlue else SNUTTColors.TableGrid,
                            shape = RoundedCornerShape(17.dp),
                        )
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    color = SNUTTColors.MainBlue.copy(
                                        alpha = 0.06f,
                                    ),
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
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(
                color = SNUTTColors.White,
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
                    ),
                )
                Text(
                    "선택",
                    style = SNUTTTypography.subtitle2.copy(
                        fontSize = 13.sp,
                    ),
                    lineHeight = 15.sp,
                )
            }
            ArrowDownIcon(
                modifier = Modifier
                    .height(24.dp)
                    .rotate(if (isExpanded) 180f else 0f),
            )
        }

        AnimatedVisibility(isExpanded) {
            Column(
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            color = SNUTTColors.Gray,
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
                        color = SNUTTColors.DarkerGray,
                    ),
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = SNUTTColors.MainBlue)) {
                            append("${moreText?.length ?: 0}")
                        }
                        withStyle(style = SpanStyle(color = SNUTTColors.EditTextLabel)) {
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
    DiaryActivitySelectSection(
        activitySelectionState = ActivitySelectionState.Complete,
        onToggleActivitySelection = {},
        onCompleteSelectActivities = {},
        onRestartSelectActivities = {},
        dailyClassTypes = DiaryPreviewData.sampleWriteUiStateSelecting.dailyClassTypes,
    )
}

@Composable
@Preview
fun DiaryQuestionBoxPreview() {
    DiaryQuestionsSection(
        questions = DiaryPreviewData.getQuestionsForActivities(
            listOf("수업"),
            "컴퓨터프로그래밍",
        ),
        onChange = { _, _ -> },
    )
}

@Composable
@Preview(showBackground = true)
fun DiaryQuestionPreview() {
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

@Composable
@Preview
fun MoreTextPreview() {
    MoreTextItem(
        moreText = "시험을 예고 없이 보니 주의하시기 바랍니다.",
        onChange = {},
    )
}
