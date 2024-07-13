package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.LectureColor
import com.wafflestudio.snutt2.core.model.data.PlaceTime

abstract class TimetableLecture(
    override val id: String,
    override val lectureId: String?,
    override val classification: String?,
    override val department: String?,
    override val academicYear: String?,
    override val courseNumber: String?,
    override val lectureNumber: String?,
    override val title: String,
    override val credit: Long,
    override val placeTimes: List<PlaceTime>,
    override val instructor: String,
    override val quota: Long?,
    override val freshmanQuota: Long?,
    override val remark: String,
    override val category: String?,
    override val colorIndex: Long?,
    override val color: LectureColor?,
    override val registrationCount: Long?,
    override val wasFull: Boolean?,
): Lecture(
    id = id,
    lectureId = lectureId,
    classification = classification,
    department = department,
    academicYear = academicYear,
    courseNumber = courseNumber,
    lectureNumber = lectureNumber,
    title = title,
    credit = credit,
    placeTimes = placeTimes,
    instructor = instructor,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    category = category,
    colorIndex = colorIndex,
    color = color,
    registrationCount = null,
    wasFull = null,
) {
    constructor(
        id: String,
        lectureId: String?,
        classification: String?,
        department: String?,
        academicYear: String?,
        courseNumber: String?,
        lectureNumber: String?,
        title: String,
        credit: Long,
        placeTimes: List<PlaceTime>,
        instructor: String,
        quota: Long?,
        freshmanQuota: Long?,
        remark: String,
        category: String?,
        colorIndex: Long?,
        color: LectureColor?,
    ): this(
        id = id,
        lectureId = lectureId,
        classification = classification,
        department = department,
        academicYear = academicYear,
        courseNumber = courseNumber,
        lectureNumber = lectureNumber,
        title = title,
        credit = credit,
        placeTimes = placeTimes,
        instructor = instructor,
        quota = quota,
        freshmanQuota = freshmanQuota,
        remark = remark,
        category = category,
        colorIndex = colorIndex,
        color = color,
        registrationCount = null,
        wasFull = null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        else if (other !is OriginalLecture) return false
        else {
            return if (id != other.id) { false }
            else if (lectureId != other.lectureId) { false }
            else if (classification != other.classification) { false }
            else if (department != other.department) { false }
            else if (academicYear != other.academicYear) { false }
            else if (courseNumber != other.courseNumber) { false }
            else if (lectureNumber != other.lectureNumber) { false }
            else if (title != other.title) { false }
            else if (credit != other.credit) { false }
            else if (placeTimes.toSet() != other.placeTimes.toSet()) { false } // TODO : 추후 테스트
            else if (instructor != other.instructor) { false }
            else if (quota != other.quota) { false }
            else if (freshmanQuota != other.freshmanQuota) { false }
            else if (remark != other.remark) { false }
            else if (category != other.category) { false }
            else if (colorIndex != other.colorIndex) { false }
            else if (color != other.color) { false }
            else if (registrationCount != other.registrationCount) { false }
            else { wasFull != other.wasFull }
        }
    }

    override fun hashCode(): Int {
        var result = 0
        result = updateHashCode(result, id)
        result = updateHashCode(result, lectureId)
        result = updateHashCode(result, classification)
        result = updateHashCode(result, department)
        result = updateHashCode(result, academicYear)
        result = updateHashCode(result, courseNumber)
        result = updateHashCode(result, lectureNumber)
        result = updateHashCode(result, title)
        result = updateHashCode(result, credit)
        result = updateHashCode(result, placeTimes)
        result = updateHashCode(result, instructor)
        result = updateHashCode(result, quota)
        result = updateHashCode(result, freshmanQuota)
        result = updateHashCode(result, remark)
        result = updateHashCode(result, category)
        result = updateHashCode(result, colorIndex)
        result = updateHashCode(result, color)
        result = updateHashCode(result, registrationCount)
        result = updateHashCode(result, wasFull)
        return result
    }

    private fun updateHashCode(currentHashCode: Int, property: Any?): Int
            = currentHashCode * 31 + (property?.hashCode() ?: 0)
}
