package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.TipCloseIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.getLectureSessionString

@Composable
internal fun LectureSessionListSection(
    sessions: List<LectureSession>,
    editMode: Boolean,
    onEditTime: (index: Int, session: LectureSession) -> Unit,
    onLocationChange: (index: Int, location: String) -> Unit,
    onDeleteSession: (index: Int) -> Unit,
    onAddSession: () -> Unit,
) {
    // 시간 및 장소 item 추가했을 때 애니메이션 적용하기 (LazyColumn 의 기능 모방)
    var lastItemAnimState by remember {
        mutableStateOf(MutableTransitionState(true))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        Text(
            text = stringResource(R.string.lecture_detail_class_time),
            modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 14.dp),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
        )

        sessions.forEachIndexed { idx, session ->
            TimeAndLocationItem(
                timeText = getLectureSessionString(session),
                locationText = session.place,
                editTime = { onEditTime(idx, session) },
                onLocationTextChange = { onLocationChange(idx, it) },
                onClickDeleteIcon = { onDeleteSession(idx) },
                editMode = editMode,
                visibleState = if (idx == sessions.lastIndex) {
                    lastItemAnimState
                } else {
                    MutableTransitionState(true)
                },
            )
        }

        AnimatedVisibility(visible = editMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clicks {
                        lastItemAnimState = MutableTransitionState(false).apply { targetState = true }
                        onAddSession()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.lecture_detail_add_class_time),
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                )
            }
        }
    }
}

@Composable
private fun TimeAndLocationItem(
    timeText: String,
    locationText: String,
    editMode: Boolean,
    editTime: () -> Unit,
    onLocationTextChange: (String) -> Unit,
    onClickDeleteIcon: () -> Unit,
    visibleState: MutableTransitionState<Boolean>,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.lecture_detail_time),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                        modifier = Modifier.width(76.dp),
                    )
                    Text(
                        text = timeText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clicks { if (editMode) editTime() },
                        style = SNUTTTypography.body1.copy(
                            fontSize = 15.sp,
                            color = SNUTTColors.Black900,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.lecture_detail_place),
                        modifier = Modifier.width(76.dp),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = locationText,
                        onValueChange = onLocationTextChange,
                        enabled = editMode,
                        modifier = Modifier.fillMaxWidth(),
                        underlineEnabled = false,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                        hint = stringResource(R.string.lecture_detail_hint_nothing),
                    )
                }
            }
            AnimatedVisibility(visible = editMode) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .clicks { onClickDeleteIcon() },
                    contentAlignment = Alignment.Center,
                ) {
                    TipCloseIcon(
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "보기 모드")
@Composable
private fun ViewModePreview() {
    LectureSessionListSection(
        sessions = PreviewData.syllabusLecture.lectureSessions,
        editMode = false,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "편집 모드")
@Composable
private fun EditModePreview() {
    LectureSessionListSection(
        sessions = PreviewData.syllabusLecture.lectureSessions,
        editMode = true,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}
