package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.Building
import com.wafflestudio.snutt2.domain.model.Campus
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.GeoCoordinate
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.network.dto.ClassPlaceAndTimeDto
import com.wafflestudio.snutt2.network.dto.ClassTimeDto
import com.wafflestudio.snutt2.network.dto.ColorDto
import com.wafflestudio.snutt2.network.dto.ColorSetDto
import com.wafflestudio.snutt2.network.dto.LectureBuildingDto
import com.wafflestudio.snutt2.network.dto.LectureDto
import com.wafflestudio.snutt2.network.dto.LectureReviewDto
import com.wafflestudio.snutt2.network.dto.SnuttEvLectureIdDto
import com.wafflestudio.snutt2.network.dto.TimetableLectureDto
import com.wafflestudio.snutt2.network.dto.parseHexColor
import java.time.DayOfWeek
import java.time.LocalTime

// region LectureDto ↔ Lecture

fun LectureDto.toDomain(): Lecture = if (review != null) toSearchedLecture() else toLocalLecture()

fun LectureDto.toLocalLecture(): LocalLecture {
    return if (lecture_id != null) {
        SyllabusLecture(
            id = id,
            courseTitle = course_title,
            lectureSessions = class_time_json.map { it.toLectureSession() },
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
            originalLectureId = lecture_id,
            color = colorDtoToLectureColor(colorIndex.toInt(), color),
        )
    } else {
        CustomLecture(
            id = id,
            courseTitle = course_title,
            lectureSessions = class_time_json.map { it.toLectureSession() },
            instructor = instructor,
            credit = credit,
            remark = remark,
            color = colorDtoToLectureColor(colorIndex.toInt(), color),
        )
    }
}

fun LectureDto.toSearchedLecture(): SearchedLecture = SearchedLecture(
    id = id,
    courseTitle = course_title,
    lectureSessions = class_time_json.map { it.toLectureSession() },
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

fun Lecture.toLectureDto(): LectureDto = when (this) {
    is LocalLecture -> toLectureDto()
    is SearchedLecture -> toLectureDto()
}

fun LocalLecture.toLectureDto(): LectureDto = LectureDto(
    id = id,
    lecture_id = if (this is SyllabusLecture) originalLectureId else null,
    classification = if (this is SyllabusLecture) classification else null,
    department = if (this is SyllabusLecture) department else null,
    academic_year = if (this is SyllabusLecture) academicYear else null,
    course_number = if (this is SyllabusLecture) courseNumber else null,
    lecture_number = if (this is SyllabusLecture) lectureNumber else null,
    course_title = courseTitle,
    credit = credit,
    class_time_json = lectureSessions.map { it.toClassTimeDto() },
    instructor = instructor,
    quota = if (this is SyllabusLecture) quota else 0,
    freshmanQuota = if (this is SyllabusLecture) freshmanQuota else null,
    remark = remark,
    category = if (this is SyllabusLecture) category else null,
    categoryPre2025 = if (this is SyllabusLecture) categoryPre2025 else null,
    colorIndex = lectureColorToColorIndex(color).toLong(),
    color = lectureColorToColorDto(color),
    registrationCount = 0L,
    wasFull = false,
    review = null,
)

fun SearchedLecture.toLectureDto(): LectureDto = LectureDto(
    id = id,
    lecture_id = null,
    classification = classification,
    department = department,
    academic_year = academicYear,
    course_number = courseNumber,
    lecture_number = lectureNumber,
    course_title = courseTitle,
    credit = credit,
    class_time_json = lectureSessions.map { it.toClassTimeDto() },
    instructor = instructor,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    category = category,
    categoryPre2025 = categoryPre2025,
    colorIndex = 0L,
    color = ColorDto(),
    registrationCount = registrationCount,
    wasFull = wasFull,
    review = LectureReviewDto(
        id = reviewInfo.id,
        rating = reviewInfo.rating,
        reviewCount = reviewInfo.reviewCount,
    ),
)

// endregion

// region TimetableLectureDto ↔ Lecture

fun TimetableLectureDto.toDomain(): Lecture =
    if (snuttEvLecture != null) toSearchedLecture() else toLocalLecture()

fun TimetableLectureDto.toLocalLecture(): LocalLecture {
    return if (lectureId != null) {
        SyllabusLecture(
            id = id ?: "",
            courseTitle = courseTitle,
            lectureSessions = classPlaceAndTimes.map { it.toLectureSession(id ?: "") },
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
            color = colorSetDtoToLectureColor(colorIndex, color),
        )
    } else {
        CustomLecture(
            id = id ?: "",
            courseTitle = courseTitle,
            lectureSessions = classPlaceAndTimes.map { it.toLectureSession(id ?: "") },
            instructor = instructor ?: "",
            credit = credit ?: 0,
            remark = remark ?: "",
            color = colorSetDtoToLectureColor(colorIndex, color),
        )
    }
}

fun TimetableLectureDto.toSearchedLecture(): SearchedLecture = SearchedLecture(
    id = id ?: "",
    courseTitle = courseTitle,
    lectureSessions = classPlaceAndTimes.map { it.toLectureSession(id ?: "") },
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

fun Lecture.toTimetableLectureDto(): TimetableLectureDto = when (this) {
    is LocalLecture -> toTimetableLectureDto()
    is SearchedLecture -> toTimetableLectureDto()
}

fun LocalLecture.toTimetableLectureDto(): TimetableLectureDto = TimetableLectureDto(
    id = id,
    lectureId = if (this is SyllabusLecture) originalLectureId else null,
    classification = if (this is SyllabusLecture) classification else null,
    department = if (this is SyllabusLecture) department else null,
    academicYear = if (this is SyllabusLecture) academicYear else null,
    courseNumber = if (this is SyllabusLecture) courseNumber else null,
    lectureNumber = if (this is SyllabusLecture) lectureNumber else null,
    courseTitle = courseTitle,
    credit = credit,
    classPlaceAndTimes = lectureSessions.map { it.toClassPlaceAndTimeDto() },
    instructor = instructor,
    quota = if (this is SyllabusLecture) quota.toInt() else 0,
    freshmanQuota = if (this is SyllabusLecture) freshmanQuota.toInt() else null,
    remark = remark,
    category = if (this is SyllabusLecture) category else null,
    categoryPre2025 = if (this is SyllabusLecture) categoryPre2025 else null,
    colorIndex = lectureColorToColorIndex(color),
    color = lectureColorToColorSetDto(color),
    snuttEvLecture = null,
)

fun SearchedLecture.toTimetableLectureDto(): TimetableLectureDto = TimetableLectureDto(
    id = id,
    lectureId = null,
    classification = classification,
    department = department,
    academicYear = academicYear,
    courseNumber = courseNumber,
    lectureNumber = lectureNumber,
    courseTitle = courseTitle,
    credit = credit,
    classPlaceAndTimes = lectureSessions.map { it.toClassPlaceAndTimeDto() },
    instructor = instructor,
    quota = quota.toInt(),
    freshmanQuota = freshmanQuota.toInt(),
    remark = remark,
    category = category,
    categoryPre2025 = categoryPre2025,
    colorIndex = 0,
    color = null,
    snuttEvLecture = if (reviewInfo.id.isNotEmpty()) {
        SnuttEvLectureIdDto(evLectureId = reviewInfo.id.toLongOrNull() ?: 0L)
    } else {
        null
    },
)

// endregion

// region LectureBuildingDto → Building

fun LectureBuildingDto.toDomain(): Building = Building(
    campus = campus,
    buildingNumber = buildingNumber,
    buildingNameKor = buildingNameKor ?: "",
    buildingNameEng = buildingNameEng ?: "",
    coordinate = GeoCoordinate(
        latitude = locationInDMS.latitude,
        longitude = locationInDMS.longitude,
    ),
)

// endregion

// region 내부 헬퍼

private fun ClassTimeDto.toLectureSession(): LectureSession = LectureSession(
    id = id,
    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
    day = DayOfWeek.of(day + 1),
    startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
    endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
    place = place,
)

private fun ClassPlaceAndTimeDto.toLectureSession(lectureId: String): LectureSession = LectureSession(
    id = lectureId,
    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
    day = DayOfWeek.of(day.toInt() + 1),
    startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
    endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
    place = place ?: "",
)

private fun LectureSession.toClassTimeDto(): ClassTimeDto = ClassTimeDto(
    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
    day = day.value - 1,
    place = place,
    id = id,
    startMinute = startTime.hour * 60 + startTime.minute,
    endMinute = endTime.hour * 60 + endTime.minute,
)

private fun LectureSession.toClassPlaceAndTimeDto(): ClassPlaceAndTimeDto = ClassPlaceAndTimeDto(
    // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
    day = (day.value - 1).toString(),
    place = place,
    startMinute = startTime.hour * 60 + startTime.minute,
    endMinute = endTime.hour * 60 + endTime.minute,
)

private fun colorDtoToLectureColor(colorIndex: Int, color: ColorDto): LectureColor =
    if (colorIndex == 0) {
        LectureColor.Custom(
            foreground = color.fgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
            background = color.bgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
        )
    } else {
        LectureColor.BuiltIn(colorIndex = colorIndex - 1)
    }

private fun colorSetDtoToLectureColor(colorIndex: Int, color: ColorSetDto?): LectureColor =
    if (colorIndex == 0) {
        LectureColor.Custom(
            foreground = color?.fg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
            background = color?.bg?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
        )
    } else {
        LectureColor.BuiltIn(colorIndex = colorIndex - 1)
    }

private fun lectureColorToColorIndex(color: LectureColor): Int =
    ((color as? LectureColor.BuiltIn)?.colorIndex ?: -1) + 1

private fun lectureColorToColorDto(color: LectureColor): ColorDto = when (color) {
    is LectureColor.Custom -> ColorDto(fgColor = color.foreground, bgColor = color.background)
    is LectureColor.BuiltIn -> ColorDto()
}

private fun lectureColorToColorSetDto(color: LectureColor): ColorSetDto? = when (color) {
    is LectureColor.Custom -> ColorSetDto(fgColor = color.foreground, bgColor = color.background)
    is LectureColor.BuiltIn -> null
}

// endregion
