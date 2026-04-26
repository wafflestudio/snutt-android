package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.ui.preview.LecturePreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_SyllabusLecture_ViewMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_SyllabusLecture_EditMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_CustomLecture_ViewMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.customLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_CustomLecture_EditMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.customLecture,
        editMode = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_SearchedLecture_ViewMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.searchedLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureActionButtons_HideDeleteButton_SyllabusLecture_ViewMode() {
    LectureActionButtons(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = false,
        hideDeleteButton = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}
