package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.ColorDto

@JsonClass(generateAdapter = true)
data class PostCustomLectureParams(
    @param:Json(name = "id") var id: String? = null,
    @param:Json(name = "classification") var classification: String? = null,
    @param:Json(name = "department") var department: String? = null,
    @param:Json(name = "academic_year") var academic_year: String? = null,
    @param:Json(name = "course_number") var course_number: String? = null,
    @param:Json(name = "lecture_number") var lecture_number: String? = null,
    @param:Json(name = "course_title") var course_title: String? = null,
    @param:Json(name = "credit") var credit: Long? = null,
    @param:Json(name = "class_time") var class_time: String? = null, // lecture 검색시 띄어주는 class time
    @param:Json(name = "class_time_json") var class_time_json: List<ClassTimeDto>? = null,
    @param:Json(name = "location") var location: String? = null,
    @param:Json(name = "instructor") var instructor: String? = null,
    @param:Json(name = "quota") var quota: Long? = null,
    @param:Json(name = "enrollment") var enrollment: Long? = null,
    @param:Json(name = "remark") var remark: String? = null,
    @param:Json(name = "category") var category: String? = null,
    @param:Json(name = "categoryPre2025") var categoryPre2025: String? = null,
    @param:Json(name = "colorIndex") var colorIndex: Long? = null, // 색상
    @param:Json(name = "color") var color: ColorDto? = null,
    @param:Json(name = "is_forced") var isForced: Boolean? = false,
)
