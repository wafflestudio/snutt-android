package com.wafflestudio.snutt2.ui.util.formatter

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getSimplifiedClassTimeForLecture(context: Context, lecture: SearchedLecture): String {
    if (lecture.lectureSessions.isEmpty()) {
        return context.getString(R.string.lecture_detail_hint_nothing)
    }
    return lecture.lectureSessions.joinToString(", ") { session ->
        buildString {
            append(session.day.getDayOfWeekString())
            append("(")
            append(session.startTime.getHourMinuteString())
            append("~")
            append(session.endTime.getHourMinuteString())
            append(")")
        }
    }
}

fun getSimplifiedClassTimeForLecture(context: Context, lecture: Lecture): String {
    if (lecture.lectureSessions.isEmpty()) {
        return context.getString(R.string.lecture_detail_hint_nothing)
    }
    return lecture.lectureSessions.joinToString(", ", transform = ::getLectureSessionString)
}

fun getLectureSessionString(lectureSession: LectureSession): String = buildString {
    append(lectureSession.day.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
    append("(")
    append(lectureSession.startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
    append("~")
    append(lectureSession.endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
    append(")")
}

fun getSimplifiedLocation(context: Context, lecture: SearchedLecture): String {
    val places = lecture.lectureSessions.map { it.place }
        .filter { it.isNotBlank() }
        .distinct()
    if (places.isEmpty()) return context.getString(R.string.lecture_detail_hint_nothing)
    return places.joinToString(" / ")
}

fun getSimplifiedLocation(context: Context, lecture: Lecture): String {
    val places = lecture.lectureSessions.map { it.place }
        .filter { it.isNotBlank() }
        .distinct()
    if (places.isEmpty()) return context.getString(R.string.lecture_detail_hint_nothing)
    return places.joinToString(" / ")
}

fun getLectureTagText(context: Context, lecture: SearchedLecture): String = listOf(lecture.category, lecture.department, lecture.academicYear)
    .filter { it.isNotBlank() }
    .let {
        if (it.isEmpty()) context.getString(R.string.lecture_detail_hint_nothing) else it.joinToString(", ")
    }

fun getLectureTagText(context: Context, lecture: LocalLecture): String = when (lecture) {
    is SyllabusLecture -> {
        listOf(lecture.category, lecture.department, lecture.academicYear)
            .filter { it.isNotBlank() }
            .joinToString(", ")
    }
    is CustomLecture -> {
        context.getString(R.string.lecture_detail_hint_nothing)
    }
}

fun getInstructorAndCreditText(context: Context, lecture: Lecture): String {
    val creditText = "${lecture.credit}${context.getString(R.string.lecture_detail_credit)}"
    if (lecture.instructor.isBlank()) return creditText
    return "${lecture.instructor} / $creditText"
}

fun getQuotaTitle(info: LectureSyllabusInfo, context: Context): String = buildString {
    append(context.getString(R.string.lecture_detail_quota))
    if (info.freshmanQuota != 0L) {
        append("(${context.getString(R.string.lecture_detail_senior)})")
    }
}

fun getFullQuota(info: LectureSyllabusInfo): String = buildString {
    append(info.quota)
    if (info.freshmanQuota != 0L) {
        append("(${info.quota - info.freshmanQuota})")
    }
}

private fun LocalTime.getHourMinuteString(): String = "%02d:%02d".format(this.hour, this.minute)

private fun DayOfWeek.getDayOfWeekString(): String = this.getDisplayName(TextStyle.SHORT, Locale.getDefault())
