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

fun Lecture.contains(queryDay: Int, queryTime: Float): Boolean {
    for (placeTime in this.placeTimes) {
        val start = placeTime.timetableBlock.startTime.timeInMinutes / 60f
        val end = placeTime.timetableBlock.endTime.timeInMinutes / 60f

        if (queryDay != placeTime.timetableBlock.day.ordinal) continue // TODO : ordinal?
        if (queryTime in start..end) return true
    }
    return false
}

fun Lecture.isCourseNumberEquals(lecture: Lecture): Boolean {
    return courseNumber != null && courseNumber == lecture.courseNumber
}

fun Lecture.isLectureNumberEquals(lecture: Lecture): Boolean {
    return isCourseNumberEquals(lecture) && lectureNumber != null && lectureNumber == lecture.lectureNumber
}

// TODO : context가 필요한 확장 함수는 어떻게 하지?
//fun Lecture.getQuotaTitle(context: Context): String = StringBuilder().apply {
//    append(context.getString(R.string.lecture_detail_quota))
//    if (freshmanQuota != null && freshmanQuota != 0L) append("(${context.getString(R.string.lecture_detail_senior)})")
//}.toString()


// TODO : quota랑 freshmanQuota가 open val이면 null 관련 오류 발생
fun OriginalLecture.getFullQuota(): String = StringBuilder().apply {
    append(quota)
    if (quota != null && freshmanQuota != null && freshmanQuota != 0L) {
        append("(${quota - freshmanQuota})")
    }
}.toString()

fun TimetableLecture.getFullQuota(): String = StringBuilder().apply {
    append(quota)
    if (quota != null && freshmanQuota != null && freshmanQuota != 0L) {
        append("(${quota - freshmanQuota})")
    }
}.toString()