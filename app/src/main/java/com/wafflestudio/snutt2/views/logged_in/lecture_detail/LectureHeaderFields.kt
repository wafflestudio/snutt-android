package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowRight
import com.wafflestudio.snutt2.components.compose.ColorBox
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureUIInfo
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
internal fun LectureHeaderFields(
    lecture: Lecture,
    editMode: Boolean,
    tableTheme: TableTheme,
    onCourseTitleChange: (String) -> Unit,
    onInstructorChange: (String) -> Unit,
    onColorClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        LectureDetailItem(
            title = stringResource(R.string.lecture_detail_lecture_title),
            value = lecture.courseTitle,
            onValueChange = onCourseTitleChange,
            hint = if (editMode) {
                stringResource(R.string.lecture_detail_lecture_title_hint)
            } else {
                stringResource(R.string.lecture_detail_hint_nothing)
            },
            enabled = editMode,
        )
        LectureDetailItem(
            title = stringResource(R.string.lecture_detail_instructor),
            value = lecture.instructor,
            onValueChange = onInstructorChange,
            hint = if (editMode) {
                stringResource(R.string.lecture_detail_instructor_hint)
            } else {
                stringResource(R.string.lecture_detail_hint_nothing)
            },
            enabled = editMode,
        )

        if (lecture is LectureUIInfo) {
            LectureColorField(
                color = lecture.color,
                tableTheme = tableTheme,
                editMode = editMode,
                onColorEditClick = onColorClick,
            )
        }
    }
}

@Composable
private fun LectureColorField(
    color: LectureColor,
    tableTheme: TableTheme,
    editMode: Boolean,
    onColorEditClick: () -> Unit,
) {
    LectureDetailItem(
        title = stringResource(R.string.lecture_detail_color),
    ) {
        Row(
            modifier = Modifier.clicks(enabled = editMode) { onColorEditClick() },
        ) {
            when (color) {
                is LectureColor.Custom -> {
                    ColorBox(
                        foreground = Color(color.foreground),
                        background = Color(color.background),
                    )
                }

                is LectureColor.BuiltIn -> {
                    val paletteColor = tableTheme.getColors(isDarkMode())[color.colorIndex]
                    ColorBox(
                        foreground = Color(paletteColor.foreground),
                        background = Color(paletteColor.background),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(visible = editMode) {
                ArrowRight(
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Custom 색상")
@Composable
private fun CustomColorPreview() {
    LectureHeaderFields(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "BuiltIn 색상")
@Composable
private fun BuiltInColorPreview() {
    LectureHeaderFields(
        lecture = PreviewData.builtInColorLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "편집 모드")
@Composable
private fun EditModePreview() {
    LectureHeaderFields(
        lecture = PreviewData.syllabusLecture,
        editMode = true,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SearchedLecture")
@Composable
private fun SearchedLecturePreview() {
    LectureHeaderFields(
        lecture = PreviewData.searchedLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}
