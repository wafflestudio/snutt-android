package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSearchQueryParams(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String? = null,
    @Json(name = "classification") val classification: List<String>? = null,
    @Json(name = "credit") val credit: List<Long>? = null,
    @Json(name = "course_number") val courseNumber: List<Long>? = null,
    @Json(name = "academic_year") val academic_year: List<String>? = null,
    @Json(name = "instructor") val instructor: List<String>? = null,
    @Json(name = "department") val department: List<String>? = null,
    @Json(name = "category") val category: List<String>? = null,
    @Json(name = "etc") val etc: List<String>? = null,
    @Json(name = "time_mask") val time_mask: List<Long>? = null,
    @Json(name = "offset") val offset: Long? = null,
    @Json(name = "limit") val limit: Long? = null
)
