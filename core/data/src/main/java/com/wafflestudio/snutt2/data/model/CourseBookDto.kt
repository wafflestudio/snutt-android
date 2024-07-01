package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseBookDtoT(
    @Json(name = "semester") val semester: Long,
    @Json(name = "year") val year: Long,
)
