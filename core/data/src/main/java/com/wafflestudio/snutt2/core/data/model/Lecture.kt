package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Day
import com.wafflestudio.snutt2.core.model.data.PlaceTime
import com.wafflestudio.snutt2.core.model.data.Time
import com.wafflestudio.snutt2.core.model.data.TimetableBlock
import com.wafflestudio.snutt2.core.model.data.lecture.Lecture
import com.wafflestudio.snutt2.core.model.data.lecture.OriginalLecture
import com.wafflestudio.snutt2.core.model.data.lecture.TimetableLecture
import com.wafflestudio.snutt2.core.network.model.ClassTimeDto
import com.wafflestudio.snutt2.core.network.model.LectureDto

fun LectureDto.toExternalModel(): Lecture {
    return when (lecture_id == null) {
        true -> toOriginalLecture()
        false -> toTimetableLecture()
    }
}

fun LectureDto.toOriginalLecture() = OriginalLecture(
    id = id,
    classification = classification,
    department = department,
    academicYear = academic_year,
    courseNumber = course_number,
    lectureNumber = lecture_number,
    title = course_title,
    credit = credit,
    placeTimes = class_time_json.map { it.toExternalModel() },
    instructor = instructor,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    category = category,
    registrationCount = registrationCount,
    wasFull = wasFull
)

fun LectureDto.toTimetableLecture() = TimetableLecture(
    id = id,
    classification = classification,
    department = department,
    academicYear = academic_year,
    lectureNumber = lecture_number,
    courseNumber = course_number,
    title = course_title,
    credit = credit,
    placeTimes = class_time_json.map { it.toExternalModel() },
    instructor = instructor,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    category = category,
    originalLectureId = lecture_id,
    colorIndex = colorIndex,
    color = color.toExternalModel(),
)

fun ClassTimeDto.toExternalModel() = PlaceTime(
    timetableBlock = TimetableBlock(
        day = when (day){
            0 -> Day.MONDAY
            1 -> Day.TUESDAY
            2 -> Day.WEDNESDAY
            3 -> Day.THURSDAY
            4 -> Day.FRIDAY
            5 -> Day.SATURDAY
            else -> Day.SUNDAY
        },
        startTime = Time(
            startMinute,
        ),
        endTime = Time(
            endMinute
        )

    ),
    placeName = place
)

// TODO : 제대로 되는지 테스트 해보면 좋을 듯