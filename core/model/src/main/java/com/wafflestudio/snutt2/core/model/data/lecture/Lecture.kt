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

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Lecture) {
            return false
        } else {
            return if (id != other.id) {
                false
            } else if (originalLectureId != other.originalLectureId) {
                false
            } else if (title != other.title) {
                false
            } else if (instructor != other.instructor) {
                false
            } else if (department != other.department) {
                false
            } else if (academicYear != other.academicYear) {
                false
            } else if (credit != other.credit) {
                false
            } else if (classification != other.classification) {
                false
            } else if (category != other.category) {
                false
            } else if (courseNumber != other.courseNumber) {
                false
            } else if (lectureNumber != other.lectureNumber) {
                false
            } else if (quota != other.quota) {
                false
            } else if (freshmanQuota != other.freshmanQuota) {
                false
            } else if (remark != other.remark) {
                false
            } else if (placeTimes.toSet() != other.placeTimes.toSet()) {
                false
            } else if (color != other.color) {
                false
            } else if (registrationCount != other.registrationCount) {
                false
            } else {
                wasFull != other.wasFull
            }
        }
    }

    override fun hashCode(): Int {
        return listOf(
            id, originalLectureId, title, instructor, department,
            academicYear, credit, classification, category, courseNumber, lectureNumber,
            quota, freshmanQuota, remark, placeTimes, color, registrationCount, wasFull,
        ).fold(0) { current, value ->
            current * 31 + (value?.hashCode() ?: 0)
        }
    }
}
