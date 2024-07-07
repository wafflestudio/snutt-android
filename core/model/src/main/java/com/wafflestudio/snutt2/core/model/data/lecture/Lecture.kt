package com.wafflestudio.snutt2.core.model.data.lecture

import com.wafflestudio.snutt2.core.model.data.PlaceTime

abstract class Lecture(
    open val id: String,
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
)