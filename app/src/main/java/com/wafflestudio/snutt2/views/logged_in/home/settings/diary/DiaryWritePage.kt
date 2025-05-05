package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.wafflestudio.snutt2.components.compose.ArrowUpIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryWritePage() {
    val today_options = listOf(
        "개강" to true,
        "수업" to false,
        "실기" to true,
        "시험" to false,
        "발표" to true,
        "휴강" to false,
        "종강" to true,
        "드랍" to false,
    )

    val sugang_options = listOf(
        "널널했어요" to false,
        "1픽했어요" to false,
        "2~3픽했어요" to false,
        "초안지 썼어요" to false,
    )

    val first_impression_options = listOf(
        "두려워요" to false,
        "두려워요" to false,
        "유익했어요" to false,
        "유익했어요" to false,
    )

    val til_end_options = listOf(
        "끝까지 들을 거에요" to false,
        "모르겠어요" to false,
        "드랍할 것 같아요" to false,
    )

    var isExpanded by remember { mutableStateOf(true) }
    var moreText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = SNUTTColors.Gray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = SNUTTColors.White)
                .padding(top = 44.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "오늘 수강한 '시각디자인기초' 에 대한 의견을 남겨보세요.",
                    style = SNUTTTypography.h3.copy(fontSize = 17.sp, lineHeight = 25.sp),
                )
                Text(
                    text = "더보기 > 강의일기장에서 확인할 수 있어요.",
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
                )
            }
            ExitIcon(
                modifier = Modifier
                    .padding(start = 57.dp)
                    .width(24.dp),
            )
        }
        DiaryQuestionBox(listOf(DiaryContent("오늘 무엇을 했나요?", true, today_options)))
        DiaryQuestionBox(
            (
                listOf(
                    DiaryContent("수강신청은 어땠나요?", false, sugang_options),
                    DiaryContent("교수님의 첫인상은 어땠나요?", false, first_impression_options),
                    DiaryContent("수업 끝까지 들을 것 같나요?", false, til_end_options),
                )
                ),
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("더 남기고 싶은 말을 작성해주세요.", style = SNUTTTypography.h4.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                        Text("선택", style = SNUTTTypography.subtitle2.copy(fontSize = 13.sp), lineHeight = 15.sp)
                    }
                    ArrowDownIcon(modifier = Modifier.height(24.dp).rotate(if(isExpanded) 180f else 0f))
                }
                AnimatedVisibility(isExpanded) {
                    Box(
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
                        Column {
                            EditText(
                                value = moreText,
                                onValueChange = { moreText = it },
                                hint = "오늘 수업에서 배운 내용, 느낀 점 등을 간단하게 적어보세요.",
                                underlineEnabled = false,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = SNUTTColors.MainBlue)) {
                                        append("${moreText.length}")
                                    }
                                    withStyle(style = SpanStyle(color = SNUTTColors.EditTextLabel)) {
                                        append("/")
                                        append("200")
                                    }
                                },
                                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                                style = SNUTTTypography.button.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
                .align(Alignment.End)
                .background(color = SNUTTColors.MainBlue, shape = RoundedCornerShape(6.dp))
                .padding(vertical = 12.dp, horizontal = 48.dp),
        ) {
            Text("다음", style = SNUTTTypography.button.copy(color = SNUTTColors.White, fontSize = 15.sp))
        }
    }
}

data class DiaryContent(
    val question: String,
    val allowDuplicates: Boolean,
    val options: List<Pair<String, Boolean>>,
)

@Composable
fun DiaryQuestionBox(diaryContents: List<DiaryContent>) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
            .padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
    ) {
        LazyColumn {
            item {
                diaryContents.forEachIndexed { index, (question, allowDuplicates, options) ->

                    DiaryQuestionItem(question, allowDuplicates, options)

                    if (index != diaryContents.lastIndex) {
                        Divider(modifier = Modifier.padding(vertical = 20.dp))
                    } else {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "완료",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                style = SNUTTTypography.button.copy(fontSize = 14.sp, color = SNUTTColors.DarkMainBlue),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryQuestionItem(question: String, allowDuplicates: Boolean, options: List<Pair<String, Boolean>>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(question, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                if (allowDuplicates) Text("중복 가능", fontSize = 13.sp, color = SNUTTColors.EditTextLabel)
            }
            FlowRow(
                maxItemsInEachRow = 3,
            ) {
                options.forEach { (option, isSelected) ->
                    Text(
                        text = option, style = SNUTTTypography.button.copy(fontSize = 14.sp, color = if (isSelected) SNUTTColors.DarkMainBlue else SNUTTColors.DarkerGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                        modifier = Modifier
                            .padding(8.dp)
                            .border(
                                width = 0.6.dp,
                                color = if (isSelected) SNUTTColors.MainBlue else SNUTTColors.Gray200,
                                shape = RoundedCornerShape(17.dp),
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.background(
                                        color = SNUTTColors.MainBlue.copy(alpha = 0.06f),
                                        shape = RoundedCornerShape(17.dp),
                                    )
                                } else {
                                    Modifier
                                },
                            )
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DiaryWritePagePreview() {
    DiaryWritePage()
}
