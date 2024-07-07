package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.toNetworkModel
import com.wafflestudio.snutt2.core.network.model.PutLectureParams as PutLectureParamsNetwork

@JsonClass(generateAdapter = true)
data class PutLectureParams(
    @Json(name = "id") var id: String? = null,
    @Json(name = "classification") var classification: String? = null,
    @Json(name = "department") var department: String? = null,
    @Json(name = "academic_year") var academic_year: String? = null,
    @Json(name = "course_number") var course_number: String? = null,
    @Json(name = "lecture_number") var lecture_number: String? = null,
    @Json(name = "course_title") var course_title: String? = null,
    @Json(name = "credit") var credit: Long? = null,
    @Json(name = "class_time") var class_time: String? = null, // lecture 검색시 띄어주는 class time
    @Json(name = "class_time_json") var class_time_json: List<ClassTimeDto>? = null,
    @Json(name = "location") var location: String? = null,
    @Json(name = "instructor") var instructor: String? = null,
    @Json(name = "quota") var quota: Long? = null,
    @Json(name = "enrollment") var enrollment: Long? = null,
    @Json(name = "remark") var remark: String? = null,
    @Json(name = "category") var category: String? = null,
    @Json(name = "colorIndex") var colorIndex: Long? = null, // 색상
    @Json(name = "color") var color: ColorDto? = null,
    @Json(name = "is_forced") var isForced: Boolean? = false,
)

fun PutLectureParams.toNetworkModel() =
    PutLectureParamsNetwork(
        id = this.id,
        classification = this.classification,
        department = this.department,
        academic_year = this.academic_year,
        course_number = this.course_number,
        lecture_number = this.lecture_number,
        course_title = this.course_title,
        credit = credit,
        class_time = this.class_time,
        class_time_json = this.class_time_json?.map { it.toNetworkModel() },
        location = this.location,
        instructor = this.instructor,
        quota = this.quota,
        enrollment = this.enrollment,
        remark = this.remark,
        category = this.category,
        colorIndex = this.colorIndex,
        color = this.color?.toNetworkModel(),
        isForced = this.isForced,
    )
