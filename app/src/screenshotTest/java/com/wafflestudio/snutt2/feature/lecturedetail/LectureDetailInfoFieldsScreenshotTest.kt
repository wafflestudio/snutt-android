package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.ui.preview.LecturePreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureDetailInfoFields_SyllabusLecture_ViewMode_WithCategoryPre2025() {
    LectureDetailInfoFields(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = false,
        showCategoryPre2025 = true,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onCreditChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureDetailInfoFields_SyllabusLecture_ViewMode_WithoutCategoryPre2025() {
    LectureDetailInfoFields(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = false,
        showCategoryPre2025 = false,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onCreditChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureDetailInfoFields_SyllabusLecture_EditMode() {
    LectureDetailInfoFields(
        lecture = LecturePreviewData.syllabusLecture,
        editMode = true,
        showCategoryPre2025 = true,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onCreditChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureDetailInfoFields_CustomLecture_ViewMode() {
    LectureDetailInfoFields(
        lecture = LecturePreviewData.customLecture,
        editMode = false,
        showCategoryPre2025 = false,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onCreditChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun LectureDetailInfoFields_CustomLecture_EditMode() {
    LectureDetailInfoFields(
        lecture = LecturePreviewData.customLecture,
        editMode = true,
        showCategoryPre2025 = false,
        onDepartmentChange = {},
        onAcademicYearChange = {},
        onCreditChange = {},
        onClassificationChange = {},
        onCategoryChange = {},
        onCategoryPre2025Change = {},
        onRemarkChange = {},
    )
}
