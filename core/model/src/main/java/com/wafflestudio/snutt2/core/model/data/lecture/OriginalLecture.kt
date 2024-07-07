package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.PlaceTime

abstract class OriginalLecture(
    override val id: String,
    override val classification: String?,
    override val department: String?,
    override val academicYear: String?,
    override val courseNumber: String?,
    override val lectureNumber: String?,
    override val courseTitle: String,
    override val credit: Long,
    override val placeTimes: List<PlaceTime>,
    override val instructor: String,
    override val quota: Long?,
    override val freshmanQuota: Long?,
    override val remark: String,
    override val category: String?,
    val registrationCount: Long,
    val wasFull: Boolean,
): Lecture(
    id = id,
    classification = classification,
    department = department,
    academicYear = academicYear,
    courseNumber = courseNumber,
    lectureNumber = lectureNumber,
    courseTitle = courseTitle,
    credit = credit,
    placeTimes = placeTimes,
    instructor = instructor,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    category = category,
)