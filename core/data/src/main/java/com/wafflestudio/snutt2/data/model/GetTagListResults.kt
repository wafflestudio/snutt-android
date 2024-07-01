package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTagListResultsT(
    @Json(name = "classification") val classification: List<String>,
    @Json(name = "department") val department: List<String>,
    @Json(name = "academic_year") val academicYear: List<String>,
    @Json(name = "credit") val credit: List<String>,
    @Json(name = "instructor") val instructor: List<String>,
    @Json(name = "category") val category: List<String>,
)
