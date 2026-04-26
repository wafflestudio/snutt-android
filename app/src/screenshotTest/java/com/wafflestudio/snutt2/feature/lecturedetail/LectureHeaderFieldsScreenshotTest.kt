package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.ui.preview.LecturePreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureHeaderFields_LectureUIInfo_CustomColor_ViewMode() {
    LectureHeaderFields(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureHeaderFields_LectureUIInfo_CustomColor_EditMode() {
    LectureHeaderFields(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = true,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureHeaderFields_LectureUIInfo_BuiltInColor_ViewMode() {
    LectureHeaderFields(
        lecture = LecturePreviewData.builtInColorLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureHeaderFields_LectureUIInfo_BuiltInColor_EditMode() {
    LectureHeaderFields(
        lecture = LecturePreviewData.builtInColorLecture,
        editMode = true,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureHeaderFields_NonUIInfo_ViewMode() {
    LectureHeaderFields(
        lecture = LecturePreviewData.searchedLecture,
        editMode = false,
        tableTheme = BuiltInTheme.SNUTT,
        onCourseTitleChange = {},
        onInstructorChange = {},
        onColorClick = {},
    )
}
