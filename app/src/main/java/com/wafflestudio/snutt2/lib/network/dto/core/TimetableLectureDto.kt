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

// 신규
@JsonClass(generateAdapter = true)
data class TimetableLectureDto(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "academicYear") val academicYear: String?,
    @param:Json(name = "category") val category: String?,
    @param:Json(name = "classPlaceAndTimes") val classPlaceAndTimes: List<ClassPlaceAndTimeDto>,
    @param:Json(name = "classification") val classification: String?,
    @param:Json(name = "credit") val credit: Long?,
    @param:Json(name = "department") val department: String?,
    @param:Json(name = "instructor") val instructor: String?,
    @param:Json(name = "lectureNumber") val lectureNumber: String?,
    @param:Json(name = "quota") val quota: Int?,
    @param:Json(name = "freshmanQuota") val freshmanQuota: Int?,
    @param:Json(name = "remark") val remark: String?,
    @param:Json(name = "courseNumber") val courseNumber: String?,
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "color") val color: ColorSetDto?,
    @param:Json(name = "colorIndex") val colorIndex: Int,
    @param:Json(name = "lectureId") val lectureId: String?,
    @param:Json(name = "snuttEvLecture") val snuttEvLecture: SnuttEvLectureIdDto?,
    @param:Json(name = "categoryPre2025") val categoryPre2025: String?,
) {
    val isCustom: Boolean
        get() = courseNumber.isNullOrBlank() && lectureNumber.isNullOrEmpty()

    companion object {
        val Default = TimetableLectureDto(
            id = "",
            courseTitle = "",
            instructor = "",
            colorIndex = 1,
            color = null,
            department = null,
            academicYear = null,
            credit = 0,
            category = null,
            categoryPre2025 = null,
            classification = null,
            courseNumber = null,
            lectureNumber = null,
            quota = 0,
            freshmanQuota = null,
            remark = null,
            classPlaceAndTimes = emptyList(),
            lectureId = null,
            snuttEvLecture = null,
        )

        fun fromLecture(lecture: Lecture): TimetableLectureDto {
            return when (lecture) {
                is LocalLecture -> fromLocalLecture(lecture)
                is SearchedLecture -> fromSearchedLecture(lecture)
            }
        }

        fun fromLocalLecture(localLecture: LocalLecture): TimetableLectureDto = TimetableLectureDto(
            id = localLecture.id,
            lectureId = if (localLecture is SyllabusLecture) localLecture.originalLectureId else null,
            classification = if (localLecture is SyllabusLecture) localLecture.classification else null,
            department = if (localLecture is SyllabusLecture) localLecture.department else null,
            academicYear = if (localLecture is SyllabusLecture) localLecture.academicYear else null,
            courseNumber = if (localLecture is SyllabusLecture) localLecture.courseNumber else null,
            lectureNumber = if (localLecture is SyllabusLecture) localLecture.lectureNumber else null,
            courseTitle = localLecture.courseTitle,
            credit = localLecture.credit,
            classPlaceAndTimes = localLecture.lectureSessions.map {
                ClassPlaceAndTimeDto(
                    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                    day = (it.day.value - 1).toString(),
                    place = it.place,
                    startMinute = it.startTime.hour * 60 + it.startTime.minute,
                    endMinute = it.endTime.hour * 60 + it.endTime.minute,
                )
            },
            instructor = localLecture.instructor,
            quota = if (localLecture is SyllabusLecture) localLecture.quota.toInt() else 0,
            freshmanQuota = if (localLecture is SyllabusLecture) localLecture.freshmanQuota.toInt() else null,
            remark = localLecture.remark,
            category = if (localLecture is SyllabusLecture) localLecture.category else null,
            categoryPre2025 = if (localLecture is SyllabusLecture) localLecture.categoryPre2025 else null,
            colorIndex = ((localLecture.color as? LectureColor.BuiltIn)?.colorIndex ?: -1) + 1,
            color = when (val c = localLecture.color) {
                is LectureColor.Custom -> ColorSetDto(fgColor = c.foreground, bgColor = c.background)
                is LectureColor.BuiltIn -> null
            },
            snuttEvLecture = null,
        )

        fun fromSearchedLecture(searchedLecture: SearchedLecture): TimetableLectureDto =
            TimetableLectureDto(
                id = searchedLecture.id,
                lectureId = null,
                classification = searchedLecture.classification,
                department = searchedLecture.department,
                academicYear = searchedLecture.academicYear,
                courseNumber = searchedLecture.courseNumber,
                lectureNumber = searchedLecture.lectureNumber,
                courseTitle = searchedLecture.courseTitle,
                credit = searchedLecture.credit,
                classPlaceAndTimes = searchedLecture.lectureSessions.map {
                    ClassPlaceAndTimeDto(
                        // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                        day = (it.day.value - 1).toString(),
                        place = it.place,
                        startMinute = it.startTime.hour * 60 + it.startTime.minute,
                        endMinute = it.endTime.hour * 60 + it.endTime.minute,
                    )
                },
                instructor = searchedLecture.instructor,
                quota = searchedLecture.quota.toInt(),
                freshmanQuota = searchedLecture.freshmanQuota.toInt(),
                remark = searchedLecture.remark,
                category = searchedLecture.category,
                categoryPre2025 = searchedLecture.categoryPre2025,
                colorIndex = 0,
                color = null,
                snuttEvLecture = if (searchedLecture.reviewInfo.id.isNotEmpty()) {
                    SnuttEvLectureIdDto(
                        evLectureId = searchedLecture.reviewInfo.id.toLongOrNull() ?: 0L,
                    )
                } else {
                    null
                },
            )
    }

    fun toLocalLecture(): LocalLecture {
        if (lectureId != null) {
            return SyllabusLecture(
                id = id ?: "",
                courseTitle = courseTitle,
                lectureSessions = classPlaceAndTimes.map { (day, place, startMinute, endMinute) ->
                    LectureSession(
                        id = id ?: "",
                        // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                        day = DayOfWeek.of(day.toInt() + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                        place = place ?: "",
                    )
                },
                instructor = instructor ?: "",
                credit = credit ?: 0,
                remark = remark ?: "",
                classification = classification ?: "",
                department = department ?: "",
                academicYear = academicYear ?: "",
                courseNumber = courseNumber ?: "",
                lectureNumber = lectureNumber ?: "",
                category = category ?: "",
                categoryPre2025 = categoryPre2025 ?: "",
                quota = quota?.toLong() ?: 0,
                freshmanQuota = freshmanQuota?.toLong() ?: 0,
                originalLectureId = lectureId,
                color = if (colorIndex == 0) {
                    LectureColor.Custom(
                        foreground = color?.fg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                        background = color?.bg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                    )
                } else {
                    LectureColor.BuiltIn(colorIndex = colorIndex - 1)
                },
            )
        } else {
            return CustomLecture(
                id = id ?: "",
                courseTitle = courseTitle,
                lectureSessions = classPlaceAndTimes.map { (day, place, startMinute, endMinute) ->
                    LectureSession(
                        id = id ?: "",
                        // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                        day = DayOfWeek.of(day.toInt() + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                        place = place ?: "",
                    )
                },
                instructor = instructor ?: "",
                credit = credit ?: 0,
                remark = remark ?: "",
                color = if (colorIndex == 0) {
                    LectureColor.Custom(
                        foreground = color?.fg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                        background = color?.bg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
                    )
                } else {
                    LectureColor.BuiltIn(colorIndex = colorIndex - 1)
                },
            )
        }
    }

    fun toSearchedLecture(): SearchedLecture = SearchedLecture(
        id = id ?: "",
        courseTitle = courseTitle,
        lectureSessions = classPlaceAndTimes.map { (day, place, startMinute, endMinute) ->
            LectureSession(
                id = id ?: "",
                // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                day = DayOfWeek.of(day.toInt() + 1),
                startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                place = place ?: "",
            )
        },
        instructor = instructor ?: "",
        credit = credit ?: 0,
        remark = remark ?: "",
        classification = classification ?: "",
        department = department ?: "",
        academicYear = academicYear ?: "",
        courseNumber = courseNumber ?: "",
        lectureNumber = lectureNumber ?: "",
        category = category ?: "",
        categoryPre2025 = categoryPre2025 ?: "",
        quota = quota?.toLong() ?: 0,
        freshmanQuota = freshmanQuota?.toLong() ?: 0,
        registrationCount = 0L,
        wasFull = false,
        reviewInfo = LectureReviewInfo(
            id = snuttEvLecture?.evLectureId?.toString() ?: "",
            rating = 0.0,
            reviewCount = 0,
        ),
    )

    fun toDomainModel(): Lecture {
        return if (this.snuttEvLecture != null) {
            this.toSearchedLecture()
        } else {
            this.toLocalLecture()
        }
    }
}
