package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.domain.model.preview.PreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_SyllabusLecture_ViewMode() {
    LectureActionButtons(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_SyllabusLecture_EditMode() {
    LectureActionButtons(
        lecture = PreviewData.syllabusLecture,
        editMode = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_CustomLecture_ViewMode() {
    LectureActionButtons(
        lecture = PreviewData.customLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_CustomLecture_EditMode() {
    LectureActionButtons(
        lecture = PreviewData.customLecture,
        editMode = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_SearchedLecture_ViewMode() {
    LectureActionButtons(
        lecture = PreviewData.searchedLecture,
        editMode = false,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360)
@Composable
fun LectureActionButtons_HideDeleteButton_SyllabusLecture_ViewMode() {
    LectureActionButtons(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        hideDeleteButton = true,
        onSyllabus = {},
        onReview = {},
        onDelete = {},
        onReset = {},
    )
}
