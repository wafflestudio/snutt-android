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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DiaryWriteRoute(
    modifier: Modifier = Modifier,
    diaryWriteViewModel: DiaryWriteViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val diaryWrite by diaryWriteViewModel.diaryWriteInit.collectAsState()

    LaunchedEffect(Unit) {
        diaryWriteViewModel.diaryWriteUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is DiaryWriteUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }
                is DiaryWriteUiEvent.NavigateToWriteMore -> {
                    // TODO: 다음 화면으로 네비게이션
                }
                is DiaryWriteUiEvent.NavigateToOnboard -> {
                    // TODO: onNavigateOnboard 호출
                }
            }
        }
    }

    DiaryWriteScreen(
        modifier = modifier,
        diaryWriteUiState = diaryWrite,
        onComplete = { diaryWriteData ->
            diaryWriteViewModel.saveDiaryWrite(diaryWriteData)
        },
    )
}

@Composable
fun DiaryWriteScreen(
    modifier: Modifier = Modifier,
    diaryWriteUiState: DiaryWriteUiState,
    onComplete: (DiaryWrite) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    when (diaryWriteUiState) {
        DiaryWriteUiState.Error -> {}
        DiaryWriteUiState.Loading -> {}
        DiaryWriteUiState.Empty -> {}
        is DiaryWriteUiState.Success -> {
            var isTodayCompleted by remember {
                mutableStateOf(
                    when (diaryWriteUiState.diaryWrite.todayState) {
                        null -> false
                        else -> true
                    },
                )
            }
            val toScrollOffset = remember { mutableIntStateOf(0) }

            val todaySelected = remember {
                mutableStateOf(
                    when (diaryWriteUiState.diaryWrite.todayState) {
                        null -> List(diaryWriteTodayOptions.size) { false }
                        else -> diaryWriteUiState.diaryWrite.todayState
                    },
                )
            }
            val questionSelected = remember {
                mutableStateOf(
                    when (diaryWriteUiState.diaryWrite.questionsState) {
                        null -> listOf()
                        else -> diaryWriteUiState.diaryWrite.questionsState
                    },
                )
            }
            val moreTextState = remember {
                mutableStateOf(
                    diaryWriteUiState.diaryWrite.moreText,
                )
            }

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
                            text = "오늘 수강한 '${diaryWriteUiState.diaryWrite.lectureName}' 에 대한 의견을 남겨보세요.",
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
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .background(color = SNUTTColors.Gray),
                ) {
                    TodayQuestionBox(
                        isTodayCompleted,
                        { todaySelection ->
                            if (!isTodayCompleted) {
                                scope.launch {
                                    delay(100)
                                    scrollState.animateScrollTo(toScrollOffset.value)
                                }
                            }
                            isTodayCompleted = true
                            todaySelected.value = todaySelection
                            val questions = diaryWriteQuestionList(diaryWriteUiState.diaryWrite.lectureName, diaryWriteTodayOptions.zip(todaySelected.value).filter { it.second }.map { it.first })
                            questionSelected.value = List(questions.size) { i -> List(questions[i].options.size) { false } }
                        },
                        listOf(DiaryWriteQuestion("오늘 무엇을 했나요?", diaryWriteTodayOptions)),
                        todaySelected.value,
                    )

                    if (isTodayCompleted) {
                        Column(
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                toScrollOffset.value = coordinates.positionInParent().y.toInt()
                            },
                        ) {
                            DiaryQuestionBox(
                                diaryWriteQuestionList(diaryWriteUiState.diaryWrite.lectureName, diaryWriteTodayOptions.zip(todaySelected.value).filter { it.second }.map { it.first }),
                                questionSelected.value,
                                onChange = { questionId, index ->
                                    questionSelected.value = List(questionSelected.value.size) { i ->
                                        if (i == questionId) {
                                            List(questionSelected.value[i].size) { id ->
                                                id == index
                                            }
                                        } else {
                                            questionSelected.value[i]
                                        }
                                    }
                                },
                            )

                            MoreTextItem(
                                moreText = moreTextState.value,
                                onChange = { text -> moreTextState.value = text },
                            )

                            Text(
                                modifier = Modifier
                                    .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
                                    .align(Alignment.End)
                                    .background(color = SNUTTColors.MainBlue, shape = RoundedCornerShape(6.dp))
                                    .padding(vertical = 12.dp, horizontal = 48.dp)
                                    .clicks {
                                        onComplete(
                                            DiaryWrite(
                                                diaryWriteUiState.diaryWrite.lectureName,
                                                todaySelected.value,
                                                questionSelected.value,
                                                moreTextState.value,
                                            ),
                                        )
                                    },
                                text = "다음", style = SNUTTTypography.button.copy(color = SNUTTColors.White, fontSize = 15.sp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayQuestionBox(
    isTodayCompleted: Boolean,
    onTodayChange: (List<Boolean>) -> Unit,
    questions: List<DiaryWriteQuestion>,
    selectedState: List<Boolean>,
) {
    var localTodaySelectedState by remember { mutableStateOf(selectedState) }

    LaunchedEffect(localTodaySelectedState) {
        if (isTodayCompleted) onTodayChange(localTodaySelectedState)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
            .padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
    ) {
        questions.forEach { (question, options) ->
            DiaryQuestionItem(true, question, options, localTodaySelectedState, onChange = { index ->
                localTodaySelectedState = localTodaySelectedState.mapIndexed { i, value ->
                    if (i == index) !value else value
                }
            },)
            if (!isTodayCompleted) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "완료",
                        style = SNUTTTypography.button.copy(fontSize = 14.sp, color = SNUTTColors.DarkMainBlue, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clicks {
                                if (localTodaySelectedState != selectedState) {
                                    onTodayChange(localTodaySelectedState)
                                }
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryQuestionBox(
    questions: List<DiaryWriteQuestion>,
    selectedState: List<List<Boolean>>,
    onChange: (Int, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .background(color = SNUTTColors.White, shape = RoundedCornerShape(12.dp))
            .padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
    ) {
        questions.forEachIndexed { questionIndex, (question, options) ->

            DiaryQuestionItem(false, question, options, selectedState[questionIndex]) { index ->
                onChange(questionIndex, index)
            }

            if (questionIndex != questions.lastIndex) {
                Divider(color = SNUTTColors.Gray, modifier = Modifier.padding(vertical = 20.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryQuestionItem(
    isDuplicate: Boolean,
    question: String,
    options: List<String>,
    selectedState: List<Boolean>,
    onChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(question, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(6.dp))
            if (isDuplicate) Text("중복 가능", fontSize = 13.sp, color = SNUTTColors.EditTextLabel)
        }

        FlowRow(
            maxItemsInEachRow = 3,
        ) {
            selectedState.forEachIndexed { index, isSelected ->
                val option = options[index]
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
    moreText: String?,
    onChange: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
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
                    value = moreText ?: "",
                    onValueChange = { moretext -> onChange(moretext) },
                    hint = "오늘 수업에서 배운 내용, 느낀 점 등을 간단하게 적어보세요.",
                    underlineEnabled = false,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = SNUTTColors.MainBlue)) { // TODO: 200자 넘으면 색깔 바꾸고, 더 못 입력하게
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
                    style = SNUTTTypography.button.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
@Preview
fun DiaryWritePagePreview() {
    val previewData = DiaryPreviewData.diaryWriteNewInit
    DiaryWriteScreen(
        diaryWriteUiState = DiaryWriteUiState.Success(previewData),
        onComplete = {},
    )
}

@Composable
@Preview(showBackground = true)
fun DiaryQuestionPreview() {
    val previewData = DiaryPreviewData.diaryWriteQuestion
    DiaryQuestionItem(
        isDuplicate = false,
        question = previewData.question,
        options = previewData.options,
        selectedState = listOf(true, false, true),
        onChange = {},

    )
}

@Composable
@Preview
fun DiaryQuestionBoxPreview() {
    val previewData = DiaryPreviewData.diaryWriteInit
    val todaySelected = previewData.todayState
    DiaryQuestionBox(
        questions = diaryWriteQuestionList(previewData.lectureName, diaryWriteTodayOptions.zip(todaySelected ?: List(8) { true }).filter { it.second }.map { it.first }),
        selectedState = listOf(listOf(true, false, false), listOf(false, false, true), listOf(true, false, false)),
        onChange = { a, b -> },
    )
}

@Composable
@Preview
fun MoreTextPreview() {
    MoreTextItem(
        moreText = DiaryPreviewData.diaryWriteInit.moreText,
        onChange = {},
    )
}
