package com.wafflestudio.snutt2.views.logged_in.lecture_detail.refactor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtilsNew
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailItem

@Composable
internal fun LectureDetailInfoFields(
    lecture: Lecture,
    editMode: Boolean,
    showCategoryPre2025: Boolean,
    onDepartmentChange: (String) -> Unit,
    onAcademicYearChange: (String) -> Unit,
    onCreditChange: (Long) -> Unit,
    onClassificationChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCategoryPre2025Change: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        if (lecture is LectureSyllabusInfo) {
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_department),
                value = lecture.department,
                onValueChange = onDepartmentChange,
                enabled = editMode,
            )
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_academic_year),
                value = lecture.academicYear,
                onValueChange = onAcademicYearChange,
                enabled = editMode,
            )
        }

        // FIXME: Long 타입 상태인 credit 에 대해 빈칸을 입력할 수 있게 하기 위한 UI 버퍼. EditText에서 기능 지원해주도록 수정 필요
        var creditText by remember(editMode) {
            mutableStateOf(lecture.credit.toString())
        }
        LectureDetailItem(
            title = stringResource(R.string.lecture_detail_credit),
            value = creditText,
            onValueChange = { text ->
                val filtered = text.filter { it.isDigit() }
                creditText = filtered
                onCreditChange(filtered.toLongOrNull() ?: 0L)
            },
            enabled = editMode,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            hint = "0",
        )

        if (lecture is LectureSyllabusInfo) {
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_classification),
                value = lecture.classification,
                onValueChange = onClassificationChange,
                enabled = editMode,
            )
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_category),
                value = lecture.category,
                onValueChange = onCategoryChange,
                enabled = editMode,
            )
            if (showCategoryPre2025) {
                LectureDetailItem(
                    title = stringResource(R.string.lecture_detail_categoryPre2025),
                    value = lecture.categoryPre2025,
                    onValueChange = onCategoryPre2025Change,
                    enabled = editMode,
                )
            }
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_course_number),
                value = lecture.courseNumber,
                editable = false,
            )
            LectureDetailItem(
                title = stringResource(R.string.lecture_detail_lecture_number),
                value = lecture.lectureNumber,
                editable = false,
            )
            LectureDetailItem(
                title = SNUTTStringUtilsNew.getQuotaTitle(lecture, LocalContext.current),
                value = SNUTTStringUtilsNew.getFullQuota(lecture),
                editable = false,
            )
        }

        LectureDetailItem(
            title = stringResource(R.string.lecture_detail_remark),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(vertical = 10.dp),
            value = lecture.remark,
            onValueChange = onRemarkChange,
            enabled = editMode,
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            hint = if (editMode) {
                stringResource(R.string.lecture_detail_remark_hint)
            } else {
                stringResource(R.string.lecture_detail_hint_nothing)
            },
            labelVerticalAlignment = Alignment.Top,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture")
@Composable
private fun SyllabusLectureWithCategoryPre2025Preview() {
    LectureDetailInfoFields(
        lecture = PreviewData.syllabusLecture,
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

@Preview(showBackground = true, widthDp = 360, name = "CustomLecture")
@Composable
private fun CustomLecturePreview() {
    LectureDetailInfoFields(
        lecture = PreviewData.customLecture,
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
