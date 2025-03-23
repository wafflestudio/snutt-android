package com.wafflestudio.snutt2.lib.network.dto.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.BuiltInColor
import com.wafflestudio.snutt2.domainmodel.CustomColor
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import java.time.DayOfWeek
import java.time.LocalTime

@JsonClass(generateAdapter = true)
data class LectureDto(
    @Json(name = "_id") val id: String,
    @Json(name = "lecture_id") val lecture_id: String? = null,
    @Json(name = "classification") val classification: String?,
    @Json(name = "department") val department: String?,
    @Json(name = "academic_year") val academic_year: String?,
    @Json(name = "course_number") val course_number: String?,
    @Json(name = "lecture_number") val lecture_number: String?,
    @Json(name = "course_title") val course_title: String,
    @Json(name = "credit") val credit: Long,
    @Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @Json(name = "instructor") val instructor: String,
    @Json(name = "quota") val quota: Long = 0,
    @Json(name = "freshmanQuota") val freshmanQuota: Long?,
    @Json(name = "remark") val remark: String,
    @Json(name = "category") val category: String?,
    @Json(name = "categoryPre2025") val categoryPre2025: String?,
    @Json(name = "colorIndex") val colorIndex: Long = 0, // 색상
    @Json(name = "color") val color: ColorDto = ColorDto(),
    @Json(name = "registrationCount") val registrationCount: Long = 0,
    @Json(name = "wasFull") val wasFull: Boolean = false,
    @Json(name = "snuttEvLecture") val review: LectureReviewDto? = null,
) {

    val isCustom: Boolean
        get() = course_number.isNullOrBlank() && lecture_number.isNullOrEmpty()

    companion object {
        val Default = LectureDto(
            id = "",
            course_title = "",
            instructor = "",
            colorIndex = 1L,
            color = ColorDto(),
            department = null,
            academic_year = null,
            credit = 0,
            category = null,
            categoryPre2025 = null,
            classification = null,
            course_number = null,
            lecture_number = null,
            quota = 0L,
            freshmanQuota = null,
            remark = "",
            class_time_json = emptyList(),
        )

        fun fromLocalLecture(localLecture: LocalLecture): LectureDto = LectureDto(
            id = localLecture.id,
            lecture_id = if (localLecture is SyllabusLecture) localLecture.originalLectureId else null,
            classification = if (localLecture is SyllabusLecture) localLecture.classification else null,
            department = if (localLecture is SyllabusLecture) localLecture.department else null,
            academic_year = if (localLecture is SyllabusLecture) localLecture.academicYear else null,
            course_number = if (localLecture is SyllabusLecture) localLecture.courseNumber else null,
            lecture_number = if (localLecture is SyllabusLecture) localLecture.lectureNumber else null,
            course_title = localLecture.courseTitle,
            credit = localLecture.credit,
            class_time_json = localLecture.lectureSessions.map {
                ClassTimeDto(
                    day = it.day.value,
                    place = it.place,
                    id = it.id,
                    startMinute = it.startTime.hour * 60 + it.startTime.minute,
                    endMinute = it.endTime.hour * 60 + it.endTime.minute,
                )
            },
            instructor = localLecture.instructor,
            quota = if (localLecture is SyllabusLecture) localLecture.quota else 0,
            freshmanQuota = if (localLecture is SyllabusLecture) localLecture.freshmanQuota else null,
            remark = localLecture.remark,
            category = if (localLecture is SyllabusLecture) localLecture.category else null,
            categoryPre2025 = if (localLecture is SyllabusLecture) localLecture.categoryPre2025 else null,
            colorIndex = (localLecture.color as? BuiltInColor)?.colorIndex?.toLong() ?: 0L,
            color = ColorDto(fgColor = localLecture.color.foreground.toArgb(), localLecture.color.background.toArgb()),
            registrationCount = 0L,
            wasFull = false,
            review = null,
        )
    }

    fun toLocalLecture(): LocalLecture {
        if (lecture_id != null) {
            return SyllabusLecture(
                id = id,
                courseTitle = course_title,
                lectureSessions = class_time_json.map { (day, place, id, startMinute, endMinute) ->
                    LectureSession(
                        id = id,
                        day = DayOfWeek.of(day + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                        place = place,
                    )
                },
                instructor = instructor,
                credit = credit,
                remark = remark,
                classification = classification ?: "",
                department = department ?: "",
                academicYear = academic_year ?: "",
                courseNumber = course_number ?: "",
                lectureNumber = lecture_number ?: "",
                category = category ?: "",
                categoryPre2025 = categoryPre2025 ?: "",
                quota = quota,
                freshmanQuota = freshmanQuota ?: 0, // TODO,
                originalLectureId = lecture_id,
                color = if (colorIndex == 0L) {
                    CustomColor(
                        foreground = Color(color.fgRaw?.toColorInt() ?: 0xFFFFFF),
                        background = Color(color.bgRaw?.toColorInt() ?: 0xFFFFFF),
                    )
                } else {
                    BuiltInColor(
                        foreground = Color(color.fgRaw?.toColorInt() ?: 0xFFFFFF),
                        background = Color(color.bgRaw?.toColorInt() ?: 0xFFFFFF),
                        colorIndex = colorIndex.toInt(),
                    )
                },
            )
        } else {
            return CustomLecture(
                id = id,
                courseTitle = course_title,
                lectureSessions = class_time_json.map { (day, place, id, startMinute, endMinute) ->
                    LectureSession(
                        id = id,
                        day = DayOfWeek.of(day + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                        place = place,
                    )
                },
                instructor = instructor,
                credit = credit,
                remark = remark,
                color = if (colorIndex == 0L) {
                    CustomColor(
                        foreground = Color(color.fgRaw?.toColorInt() ?: 0xFFFFFF),
                        background = Color(color.bgRaw?.toColorInt() ?: 0xFFFFFF),
                    )
                } else {
                    BuiltInColor(
                        foreground = Color(color.fgRaw?.toColorInt() ?: 0xFFFFFF),
                        background = Color(color.bgRaw?.toColorInt() ?: 0xFFFFFF),
                        colorIndex = colorIndex.toInt(),
                    )
                },
            )
        }
    }

    fun toDomainModel(): Lecture {
        if (this.review != null) {
            return SearchedLecture(
                id = id,
                courseTitle = course_title,
                lectureSessions = class_time_json.map { (day, place, id, startMinute, endMinute) ->
                    LectureSession(
                        id = id,
                        day = DayOfWeek.of(day + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                        place = place,
                    )
                },
                instructor = instructor,
                credit = credit,
                remark = remark,
                classification = classification ?: "",
                department = department ?: "",
                academicYear = academic_year ?: "",
                courseNumber = course_number ?: "",
                lectureNumber = lecture_number ?: "",
                category = category ?: "",
                categoryPre2025 = categoryPre2025 ?: "",
                quota = quota,
                freshmanQuota = freshmanQuota ?: 0, // TODO
                registrationCount = registrationCount,
                wasFull = wasFull,
                reviewInfo = LectureReviewInfo(
                    id = review.id,
                    rating = review.rating ?: 0.0,
                    reviewCount = review.reviewCount ?: 0,
                ),
            )
        } else {
            return this.toLocalLecture()
        }
    }
}
