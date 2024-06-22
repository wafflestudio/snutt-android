package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetLecturesIdParams(
    @Json(name = "course_number")
    val courseNumber: String,
    @Json(name = "instructor")
    val instructor: String,
)

// TODO : 얘는 안쓰고 있음
