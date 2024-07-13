package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.Day
import com.wafflestudio.snutt2.core.model.data.LectureColor
import com.wafflestudio.snutt2.core.model.data.PlaceTime
import com.wafflestudio.snutt2.core.model.data.Time
import com.wafflestudio.snutt2.core.model.data.TimeTableTrimConfig
import kotlin.math.ceil
import kotlin.math.floor

abstract class Lecture(
    open val id: String,
    open val lectureId: String?,
    open val title: String,
    open val instructor: String,
    open val department: String?,
    open val academicYear: String?,
    open val credit: Long,
    open val classification: String?,
    open val category: String?,
    open val courseNumber: String?,     // 강좌번호
    open val lectureNumber: String?,    // 분반번호
    open val quota: Long?,
    open val freshmanQuota: Long?,
    open val remark: String,
    open val placeTimes: List<PlaceTime>,
    open val colorIndex: Long?,
    open val color: LectureColor?,
    open val registrationCount: Long?,
    open val wasFull: Boolean?,
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

fun List<Lecture>.getFittingTrimParam(tableTrimParam: TimeTableTrimConfig): TimeTableTrimConfig =
    TimeTableTrimConfig(
        startDay = (flatMap { lecture -> lecture.placeTimes.map { it.timetableBlock.day } } + tableTrimParam.startDay).minOf { it },
        endDay = (flatMap { lecture -> lecture.placeTimes.map { it.timetableBlock.day } } + tableTrimParam.endDay).maxOf { it },
        startTime = (flatMap { lecture -> lecture.placeTimes.map { it.timetableBlock.startTime } } + tableTrimParam.startTime).minOf { it },
        endTime = (flatMap { lecture -> lecture.placeTimes.map { it.timetableBlock.endTime } } + tableTrimParam.endTime).maxOf { it },
        forceFitLectures = true,
    )
