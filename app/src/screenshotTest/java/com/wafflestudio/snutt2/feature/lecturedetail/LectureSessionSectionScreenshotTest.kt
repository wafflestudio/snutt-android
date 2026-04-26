package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.ui.preview.PreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureSessionListSection_NonEmpty_ViewMode() {
    LectureSessionListSection(
        sessions = PreviewData.syllabusLecture.lectureSessions,
        editMode = false,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureSessionListSection_NonEmpty_EditMode() {
    LectureSessionListSection(
        sessions = PreviewData.syllabusLecture.lectureSessions,
        editMode = true,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureSessionListSection_Empty_ViewMode() {
    LectureSessionListSection(
        sessions = emptyList(),
        editMode = false,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureSessionListSection_Empty_EditMode() {
    LectureSessionListSection(
        sessions = emptyList(),
        editMode = true,
        onEditTime = { _, _ -> },
        onLocationChange = { _, _ -> },
        onDeleteSession = {},
        onAddSession = {},
    )
}
