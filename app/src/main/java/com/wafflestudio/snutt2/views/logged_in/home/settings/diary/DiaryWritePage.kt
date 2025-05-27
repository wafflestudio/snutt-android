package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryWriteRoute(modifier: Modifier = Modifier) {
    val previewData = DiaryPreviewData.diaryWritePreviewData

    DiaryWriteScreen(
        modifier = modifier,
        diaryWriteUiState = DiaryWriteUiState.Success(previewData),
        onTodayComplete = {},
        onComplete = {},
    )
}

@Composable
fun DiaryWriteScreen(
    modifier: Modifier = Modifier,
    diaryWriteUiState: DiaryWriteUiState,
    onTodayComplete: () -> Unit,
    onComplete: () -> Unit,
) {
    when (diaryWriteUiState) {
        DiaryWriteUiState.Error -> {}
        DiaryWriteUiState.Loading -> {}
        DiaryWriteUiState.Empty -> {}
        is DiaryWriteUiState.Success -> {


            Column {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(color = SNUTTColors.White)
                        .padding(top = 44.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "오늘 수강한 '${diaryWriteUiState.diaryList.lectureName}' 에 대한 의견을 남겨보세요.",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .background(color = SNUTTColors.Gray),
                ) {
                    val todayOptions = diaryWriteUiState.diaryList.todayOptions
                    val questions = diaryWriteUiState.diaryList.questions

                    DiaryQuestionBox(
                        onComplete,
                        onTodayComplete,
                        listOf(DiaryWriteQuestion("오늘 무엇을 했나요?", todayOptions)),
                        true,
                    )

                    DiaryQuestionBox(
                        onComplete,
                        {},
                        questions,
                        false,
                    )

                    MoreTextItem(
                        moreTextInit = diaryWriteUiState.diaryList.moreText
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
                            .align(Alignment.End)
                            .background(color = SNUTTColors.MainBlue, shape = RoundedCornerShape(6.dp))
                            .padding(vertical = 12.dp, horizontal = 48.dp),
                        text = "다음", style = SNUTTTypography.button.copy(color = SNUTTColors.White, fontSize = 15.sp),
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryQuestionBox(
    onTodayComplete: () -> Unit,
    onComplete: () -> Unit,
    questions: List<DiaryWriteQuestion>,
    isTodayBox: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
            .padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
    ) {
        questions.forEachIndexed { index, (question, options) ->
            val optionList = options
            DiaryQuestionItem(onComplete, isTodayBox, question, optionList)

            if (index != questions.lastIndex) {
                Divider(modifier = Modifier.padding(vertical = 20.dp)) // TODO: 색깔 연하게 바꾸기
            } else if (isTodayBox) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "완료",
                        style = SNUTTTypography.button.copy(fontSize = 14.sp, color = SNUTTColors.DarkMainBlue, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clicks { }, // TODO: 완료 누르면 밑에 거 나오도록 + 자동 스크롤
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryQuestionItem(
    onComplete: () -> Unit,
    isDuplicate: Boolean,
    question: String,
    options: List<String>,
) {
    var selectables = remember {
        mutableStateListOf(*options.map { it to false }.toTypedArray())
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(question, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(6.dp))
            if (isDuplicate) Text("중복 가능", fontSize = 13.sp, color = SNUTTColors.EditTextLabel) // TODO: allowDuplicate 필드 추가하기
        }

        FlowRow(
            maxItemsInEachRow = 3,
        ) {
            selectables.forEachIndexed { index, (option, isSelected) ->
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
                            if (!isDuplicate) {
                                for (i in selectables.indices) {
                                    val (label, _) = selectables[i]
                                    selectables[i] = label to (i == index)
                                }
                            } else {
                                val (label, _) = selectables[index]
                                selectables[index] = label to !isSelected
                            }
                        }
                        .border(
                            width = 0.6.dp,
                            color = if (isSelected) SNUTTColors.MainBlue else SNUTTColors.TableGrid,
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

@Composable
fun MoreTextItem(
    moreTextInit: String
){
    var isExpanded by remember { mutableStateOf(false) }
    var moreText by remember { mutableStateOf(moreTextInit) }
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clicks { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("더 남기고 싶은 말을 작성해주세요.", style = SNUTTTypography.h4.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                Text("선택", style = SNUTTTypography.subtitle2.copy(fontSize = 13.sp), lineHeight = 15.sp)
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
                    value = moreText,
                    onValueChange = { moreText = it },
                    hint = "오늘 수업에서 배운 내용, 느낀 점 등을 간단하게 적어보세요.",
                    underlineEnabled = false,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = SNUTTColors.MainBlue)) { // TODO: 200자 넘으면 색깔 바꾸고, 더 못 입력하게
                            append("${moreText.length}")
                        }
                        withStyle(style = SpanStyle(color = SNUTTColors.EditTextLabel)) {
                            append("/")
                            append("200")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    style = SNUTTTypography.button.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
@Preview
fun DiaryWritePagePreview() {
    val previewData = DiaryPreviewData.diaryWritePreviewData
    DiaryWriteScreen(
        diaryWriteUiState = DiaryWriteUiState.Success(previewData),
        onTodayComplete = {},
        onComplete = {},
    )
}

@Composable
@Preview
fun DiaryQuestionBoxPreview() {
    val previewData = DiaryPreviewData.diaryWritePreviewData
    DiaryQuestionBox(
        onComplete = {},
        onTodayComplete = {},
        questions = DiaryPreviewData.diaryWritePreviewData.questions,
        isTodayBox = false,
    )
}

@Composable
@Preview
fun MoreTextPreview() {
    MoreTextItem(
        moreTextInit = DiaryPreviewData.diaryWritePreviewData.moreText,
    )
}
