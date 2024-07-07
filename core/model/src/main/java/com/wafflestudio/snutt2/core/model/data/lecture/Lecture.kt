package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.PlaceTime

abstract class Lecture(
    open val id: String,
    open val classification: String?,
    open val department: String?,
    open val academicYear: String?,
    open val courseNumber: String?,
    open val lectureNumber: String?,
    open val courseTitle: String,
    open val credit: Long,
    open val placeTimes: List<PlaceTime>,
    open val instructor: String,
    open val quota: Long?,
    open val freshmanQuota: Long?,
    open val remark: String,
    open val category: String?,
)