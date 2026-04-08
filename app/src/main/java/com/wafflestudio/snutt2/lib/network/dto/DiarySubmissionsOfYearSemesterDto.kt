package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiarySubmissionsOfYearSemesterDto(
    @param:Json(name = "year") val year: Int,
    @param:Json(name = "semester") val semester: Int,
    @param:Json(name = "submissions") val submissions: List<DiarySubmissionSummaryDto>,
)
