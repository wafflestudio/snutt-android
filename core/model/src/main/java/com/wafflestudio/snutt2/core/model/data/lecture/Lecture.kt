package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.Day
import com.wafflestudio.snutt2.core.model.data.LectureColor
import com.wafflestudio.snutt2.core.model.data.PlaceTime
import com.wafflestudio.snutt2.core.model.data.Time

abstract class Lecture(
    val id: String,
    val originalLectureId: String?,
    val title: String,
    val instructor: String,
    val department: String?,
    val academicYear: String?,
    val credit: Long,
    val classification: String?,
    val category: String?,
    val courseNumber: String?,
    val lectureNumber: String?,
    val quota: Long?,
    val freshmanQuota: Long?,
    val remark: String,
    val placeTimes: List<PlaceTime>,
    val color: LectureColor?,
    val registrationCount: Long?,
    val wasFull: Boolean?,
) {
    fun contains(day: Day, time: Time): Boolean = placeTimes.map {
        it.timetableBlock
    }.any { timetableBlock ->
        day == timetableBlock.day && time in timetableBlock.startTime..timetableBlock.endTime
    }

    fun isCourseNumberEquals(lecture: Lecture): Boolean {
        if (courseNumber == null) return false
        return courseNumber == lecture.courseNumber
    }

    fun isLectureNumberEquals(lecture: Lecture): Boolean {
        if (lectureNumber == null) return false
        return isCourseNumberEquals(lecture) && lectureNumber == lecture.lectureNumber
    }
}
