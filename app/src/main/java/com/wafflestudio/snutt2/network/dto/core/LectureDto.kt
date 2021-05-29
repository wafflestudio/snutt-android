package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureDto(
    @Json(name = "id") val id: String,
    @Json(name = "classification") val classification: String,
    @Json(name = "department") val department: String,
    @Json(name = "academic_year") val academic_year: String,
    @Json(name = "course_number") val course_number: String,
    @Json(name = "lecture_number") val lecture_number: String,
    @Json(name = "course_title") val course_title: String,
    @Json(name = "credit") val credit: Long,
    @Json(name = "class_time") val class_time: String, // lecture 검색시 띄어주는 class time
    @Json(name = "class_time_mask") val class_time_mask: List<Long>,
    @Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @Json(name = "location") val location: String,
    @Json(name = "instructor") val instructor: String,
    @Json(name = "quota") val quota: Long,
    @Json(name = "enrollment") val enrollment: Long,
    @Json(name = "remark") val remark: String,
    @Json(name = "category") val category: String,
    @Json(name = "colorIndex") val colorIndex: Long, // 색상
) {

    @JsonClass(generateAdapter = true)
    data class ClassTimeDto(
        @Json(name = "day") val day: Long,
        @Json(name = "start") val start: Long,
        @Json(name = "len") val len: Long,
        @Json(name = "place") val place: String,
        @Json(name = "_id") val _id: String
    )
}
