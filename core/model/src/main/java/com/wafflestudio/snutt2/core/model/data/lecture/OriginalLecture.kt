package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.PlaceTime

class OriginalLecture(
    id: String,
    title: String,
    instructor: String,
    department: String?,
    academicYear: String?,
    credit: Long,
    classification: String?,
    category: String?,
    courseNumber: String?,
    lectureNumber: String?,
    quota: Long?,
    freshmanQuota: Long?,
    remark: String,
    placeTimes: List<PlaceTime>,
    registrationCount: Long?,
    wasFull: Boolean?,
) : Lecture(
    id = id,
    originalLectureId = null,
    title = title,
    instructor = instructor,
    department = department,
    academicYear = academicYear,
    credit = credit,
    classification = classification,
    category = category,
    courseNumber = courseNumber,
    lectureNumber = lectureNumber,
    quota = quota,
    freshmanQuota = freshmanQuota,
    remark = remark,
    placeTimes = placeTimes,
    color = null,
    registrationCount = registrationCount,
    wasFull = wasFull
)
