package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.SearchTimeDto

@JsonClass(generateAdapter = true)
data class PostSearchQueryParams(
    @param:Json(name = "year") val year: Long,
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "classification") val classification: List<String>? = null,
    @param:Json(name = "credit") val credit: List<Long>? = null,
    @param:Json(name = "course_number") val courseNumber: List<Long>? = null,
    @param:Json(name = "academic_year") val academic_year: List<String>? = null,
    @param:Json(name = "department") val department: List<String>? = null,
    @param:Json(name = "category") val category: List<String>? = null,
    @param:Json(name = "categoryPre2025") val categoryPre2025: List<String>? = null,
    @param:Json(name = "etc") val etc: List<String>? = null,
    @param:Json(name = "times") val times: List<SearchTimeDto>?,
    @param:Json(name = "timesToExclude") val timesToExclude: List<SearchTimeDto>?,
    @param:Json(name = "offset") val offset: Long? = null,
    @param:Json(name = "limit") val limit: Long? = null,
    @param:Json(name = "sortCriteria") val sortCriteria: String? = null,
)
