package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.LectureColor
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
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "lecture_id") val lecture_id: String? = null,
    @param:Json(name = "classification") val classification: String?,
    @param:Json(name = "department") val department: String?,
    @param:Json(name = "academic_year") val academic_year: String?,
    @param:Json(name = "course_number") val course_number: String?,
    @param:Json(name = "lecture_number") val lecture_number: String?,
    @param:Json(name = "course_title") val course_title: String,
    @param:Json(name = "credit") val credit: Long,
    @param:Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @param:Json(name = "instructor") val instructor: String,
    @param:Json(name = "quota") val quota: Long = 0,
    @param:Json(name = "freshmanQuota") val freshmanQuota: Long?,
    @param:Json(name = "remark") val remark: String,
    @param:Json(name = "category") val category: String?,
    @param:Json(name = "categoryPre2025") val categoryPre2025: String?,
    @param:Json(name = "colorIndex") val colorIndex: Long = 0, // 색상
    @param:Json(name = "color") val color: ColorDto = ColorDto(),
    @param:Json(name = "registrationCount") val registrationCount: Long = 0,
    @param:Json(name = "wasFull") val wasFull: Boolean = false,
    @param:Json(name = "snuttEvLecture") val review: LectureReviewDto? = null,
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

        fun fromLecture(lecture: Lecture): LectureDto {
            return when (lecture) {
                is LocalLecture -> fromLocalLecture(lecture)
                is SearchedLecture -> fromSearchedLecture(lecture)
            }
        }

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
                    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                    day = it.day.value - 1,
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
            colorIndex = ((localLecture.color as? LectureColor.BuiltIn)?.colorIndex ?: -1).toLong() + 1,
            color = when (val c = localLecture.color) {
                is LectureColor.Custom -> ColorDto(fgColor = c.foreground, bgColor = c.background)
                is LectureColor.BuiltIn -> ColorDto()
            },
            registrationCount = 0L,
            wasFull = false,
            review = null,
        )

        fun fromSearchedLecture(searchedLecture: SearchedLecture): LectureDto = LectureDto(
            id = searchedLecture.id,
            lecture_id = null,
            classification = searchedLecture.classification,
            department = searchedLecture.department,
            academic_year = searchedLecture.academicYear,
            course_number = searchedLecture.courseNumber,
            lecture_number = searchedLecture.lectureNumber,
            course_title = searchedLecture.courseTitle,
            credit = searchedLecture.credit,
            class_time_json = searchedLecture.lectureSessions.map {
                ClassTimeDto(
                    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                    day = it.day.value - 1,
                    place = it.place,
                    id = it.id,
                    startMinute = it.startTime.hour * 60 + it.startTime.minute,
                    endMinute = it.endTime.hour * 60 + it.endTime.minute,
                )
            },
            instructor = searchedLecture.instructor,
            quota = searchedLecture.quota,
            freshmanQuota = searchedLecture.freshmanQuota,
            remark = searchedLecture.remark,
            category = searchedLecture.category,
            categoryPre2025 = searchedLecture.categoryPre2025,
            colorIndex = 0L,
            color = ColorDto(),
            registrationCount = searchedLecture.registrationCount,
            wasFull = searchedLecture.wasFull,
            review = LectureReviewDto(
                id = searchedLecture.reviewInfo.id,
                rating = searchedLecture.reviewInfo.rating,
                reviewCount = searchedLecture.reviewInfo.reviewCount,
            ),
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
                        // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
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
                    LectureColor.Custom(
                        foreground = color.fgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                        background = color.bgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                    )
                } else {
                    LectureColor.BuiltIn(colorIndex = (colorIndex - 1).toInt())
                },
            )
        } else {
            return CustomLecture(
                id = id,
                courseTitle = course_title,
                lectureSessions = class_time_json.map { (day, place, id, startMinute, endMinute) ->
                    LectureSession(
                        id = id,
                        // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
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
                    LectureColor.Custom(
                        foreground = color.fgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                        background = color.bgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                    )
                } else {
                    LectureColor.BuiltIn(colorIndex = (colorIndex - 1).toInt())
                },
            )
        }
    }

    fun toSearchedLecture(): SearchedLecture = SearchedLecture(
        id = id,
        courseTitle = course_title,
        lectureSessions = class_time_json.map { (day, place, id, startMinute, endMinute) ->
            LectureSession(
                id = id,
                // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
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
            id = review?.id ?: "",
            rating = review?.rating,
            reviewCount = review?.reviewCount ?: 0,
        ),
    )

    fun toDomainModel(): Lecture {
        return if (this.review != null) {
            this.toSearchedLecture()
        } else {
            this.toLocalLecture()
        }
    }
}
