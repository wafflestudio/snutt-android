package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutLectureParamsT(
    @Json(name = "id") var id: String? = null,
    @Json(name = "classification") var classification: String? = null,
    @Json(name = "department") var department: String? = null,
    @Json(name = "academic_year") var academic_year: String? = null,
    @Json(name = "course_number") var course_number: String? = null,
    @Json(name = "lecture_number") var lecture_number: String? = null,
    @Json(name = "course_title") var course_title: String? = null,
    @Json(name = "credit") var credit: Long? = null,
    @Json(name = "class_time") var class_time: String? = null, // lecture 검색시 띄어주는 class time
    @Json(name = "class_time_json") var class_time_json: List<ClassTimeDtoT>? = null,
    @Json(name = "location") var location: String? = null,
    @Json(name = "instructor") var instructor: String? = null,
    @Json(name = "quota") var quota: Long? = null,
    @Json(name = "enrollment") var enrollment: Long? = null,
    @Json(name = "remark") var remark: String? = null,
    @Json(name = "category") var category: String? = null,
    @Json(name = "colorIndex") var colorIndex: Long? = null, // 색상
    @Json(name = "color") var color: ColorDtoT? = null,
    @Json(name = "is_forced") var isForced: Boolean? = false,
)
