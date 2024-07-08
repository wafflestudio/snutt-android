package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Day
import com.wafflestudio.snutt2.core.model.data.Place
import com.wafflestudio.snutt2.core.model.data.PlaceTime
import com.wafflestudio.snutt2.core.model.data.Time
import com.wafflestudio.snutt2.core.model.data.TimetableBlock
import com.wafflestudio.snutt2.core.model.data.lecture.Lecture
import com.wafflestudio.snutt2.core.model.data.lecture.OriginalLecture
import com.wafflestudio.snutt2.core.model.data.lecture.TimetableLecture
import com.wafflestudio.snutt2.core.network.model.ClassTimeDto
import com.wafflestudio.snutt2.core.network.model.ColorDto
import com.wafflestudio.snutt2.core.network.model.LectureDto

fun LectureDto.toExternalModel(): Lecture {
    return when (this.color == ColorDto()) { // TODO : revisit (true이면 OriginalLecture, false이면 TimeTableLecture 반환)
        true -> this.toOriginalLecture()
        false -> this.toTimetableLecture()
    }
}

fun LectureDto.toOriginalLecture() = OriginalLecture(
    id = this.id, // TODO : 모지 이거 id랑 lecture_id 중에 뭐가 필요한거지..?
    classification = this.classification,
    department = this.department,
    academicYear = this.academic_year,
    courseNumber = this.course_number,
    lectureNumber = this.lecture_number,
    title = this.course_title,
    credit = this.credit,
    placeTimes = this.class_time_json.map { it.toExternalModel() },
    instructor = this.instructor,
    quota = this.quota,
    freshmanQuota = this.freshmanQuota,
    remark = this.remark,
    category = this.category,
    registrationCount = this.registrationCount,
    wasFull = this.wasFull
)

fun LectureDto.toTimetableLecture() = TimetableLecture(
    id = this.id, // TODO : 모지 이거 id랑 lecture_id 중에 뭐가 필요한거지..?
    classification = this.classification,
    department = this.department,
    academicYear = this.academic_year,
    lectureNumber = this.lecture_number,
    courseNumber = this.course_number,
    title = this.course_title,
    credit = this.credit,
    placeTimes = this.class_time_json.map { it.toExternalModel() },
    instructor = this.instructor,
    quota = this.quota,
    freshmanQuota = this.freshmanQuota,
    remark = this.remark,
    category = this.category,
    originalLectureId = this.lecture_id, // TODO : 모지 이거 id랑 lecture_id 중에 뭐가 필요한거지..?
    colorIndex = this.colorIndex,
    color = this.color.toExternalModel(),
)

fun ClassTimeDto.toExternalModel() = PlaceTime(
    timetableBlock = TimetableBlock(
        day = when (this.day){
            0 -> Day.MONDAY
            1 -> Day.TUESDAY
            2 -> Day.WEDNESDAY
            3 -> Day.THURSDAY
            4 -> Day.FRIDAY
            5 -> Day.SATURDAY
            else -> Day.SUNDAY
        },
        startTime = Time(
            this.startMinute,
        ),
        endTime = Time(
            this.endMinute
        )

    ),
    place = Place(
        name = this.place,
        building = null
    )
)

// TODO : 제대로 되는지 테스트 해보면 좋을 듯