package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.LectureColor
import com.wafflestudio.snutt2.core.model.data.PlaceTime

class TimetableLecture(
    id: String,
    originalLectureId: String?,
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
    color: LectureColor?,
) : Lecture(
    id = id,
    originalLectureId = originalLectureId,
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
    color = color,
    registrationCount = null,
    wasFull = null,
)
