package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTagListResults(
    @param:Json(name = "classification") val classification: List<String>,
    @param:Json(name = "department") val department: List<String>,
    @param:Json(name = "academic_year") val academicYear: List<String>,
    @param:Json(name = "credit") val credit: List<String>,
    @param:Json(name = "instructor") val instructor: List<String>,
    @param:Json(name = "category") val category: List<String>,
    @param:Json(name = "categoryPre2025") val categoryPre2025: List<String>,
    @param:Json(name = "sortCriteria") val sortCriteria: List<String>,
)
