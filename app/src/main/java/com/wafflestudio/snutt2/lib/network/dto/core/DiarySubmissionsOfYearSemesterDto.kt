package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.diary.CourseBookDiarySubmissions

@JsonClass(generateAdapter = true)
data class DiarySubmissionsOfYearSemesterDto(
    @param:Json(name = "year") val year: Int,
    @param:Json(name = "semester") val semester: Int,
    @param:Json(name = "submissions") val submissions: List<DiarySubmissionSummaryDto>,
)

fun DiarySubmissionsOfYearSemesterDto.toDomainModel(): CourseBookDiarySubmissions {
    return CourseBookDiarySubmissions(
        courseBook = CourseBook(
            year = year.toLong(),
            semester = semester.toLong(),
        ),
        submissions = submissions.map { it.toDomainModel() },
    )
}
