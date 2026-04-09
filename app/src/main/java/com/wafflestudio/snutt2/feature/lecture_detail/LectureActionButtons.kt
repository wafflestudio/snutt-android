package com.wafflestudio.snutt2.feature.lecture_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
internal fun LectureActionButtons(
    lecture: Lecture,
    editMode: Boolean,
    hideDeleteButton: Boolean = false,
    onSyllabus: () -> Unit,
    onReview: () -> Unit,
    onDelete: () -> Unit,
    onReset: () -> Unit,
) {
    // 강의계획서 + 강의평
    AnimatedVisibility(visible = lecture is LectureSyllabusInfo && !editMode) {
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            LectureDetailActionButton(
                title = stringResource(R.string.lecture_detail_syllabus_button),
                onClick = onSyllabus,
            )
            LectureDetailActionButton(
                title = stringResource(R.string.lecture_detail_review_button),
                onClick = onReview,
            )
        }
    }

    // 삭제 / 초기화
    if (hideDeleteButton.not()) {
        when (lecture) {
            is CustomLecture -> {
                AnimatedVisibility(visible = !editMode) {
                    Box(modifier = Modifier.background(Color.White)) {
                        LectureDetailActionButton(
                            title = stringResource(R.string.lecture_detail_delete_button),
                            textStyle = SNUTTTypography.body1.copy(
                                fontSize = 15.sp,
                                color = SNUTTColors.Red,
                            ),
                            onClick = onDelete,
                        )
                    }
                }
            }

            is SyllabusLecture -> {
                Box(modifier = Modifier.background(Color.White)) {
                    LectureDetailActionButton(
                        title = if (editMode) {
                            stringResource(R.string.lecture_detail_reset_button)
                        } else {
                            stringResource(R.string.lecture_detail_delete_button)
                        },
                        textStyle = SNUTTTypography.body1.copy(
                            fontSize = 15.sp,
                            color = SNUTTColors.Red,
                        ),
                        onClick = if (editMode) onReset else onDelete,
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun LectureDetailActionButton(
    title: String,
    textStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth()
            .height(45.dp)
            .clicks { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title, style = textStyle)
    }
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture - 보기 모드")
@Composable
private fun SyllabusLectureViewModePreview() {
    LectureActionButtons(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture - 편집 모드")
@Composable
private fun SyllabusLectureEditModePreview() {
    LectureActionButtons(
        lecture = PreviewData.syllabusLecture,
        editMode = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "CustomLecture - 보기 모드")
@Composable
private fun CustomLectureViewModePreview() {
    LectureActionButtons(
        lecture = PreviewData.customLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

