package com.wafflestudio.snutt2.lib.data

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object SNUTTStringUtilsNew {
    fun getInstructorAndCreditText(context: Context, lecture: Lecture): String {
        val creditText = "${lecture.credit}${context.getString(R.string.lecture_detail_credit)}"

        if (lecture.instructor.isBlank()) {
            return creditText
        }

        return "${lecture.instructor} / $creditText"
    }

    fun getLectureTagText(context: Context, lecture: LocalLecture): String {
        return when (lecture) {
            is SyllabusLecture -> {
                listOf(
                    lecture.category,
                    lecture.department,
                    lecture.academicYear,
                )
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
            }

            is CustomLecture -> {
                context.getString(R.string.lecture_detail_hint_nothing)
            }
        }
    }

    /**
     * 강의의 모든 classTime을 text로 변환
     * ex) 월, 수 09:30 ~ 10:45 이면 -> "월(09:30~10:45), 수(09:30~10:45)"
     */
    fun getSimplifiedClassTimeForLecture(context: Context, lecture: Lecture): String {
        if (lecture.lectureSessions.isEmpty()) {
            return context.getString(R.string.lecture_detail_hint_nothing)
        }

        return lecture.lectureSessions.joinToString(", ", transform = ::getLectureSessionString)
    }

    /**
     * 하나의 session을 텍스트로 변환
     * ex) 월 09:30 ~ 10:45 이면 -> "월(09:30~10:45)"
     */
    fun getLectureSessionString(lectureSession: LectureSession): String = buildString {
        append(
            lectureSession.day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        )
        append("(")
        append(lectureSession.startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        append("~")
        append(lectureSession.endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        append(")")
    }

    fun getSimplifiedLocation(context: Context, lecture: Lecture): String {
        val places = lecture.lectureSessions.map { it.place }
            .filter { it.isNotBlank() }
            .distinct()

        if (places.isEmpty()) {
            return context.getString(R.string.lecture_detail_hint_nothing)
        }

        return places.joinToString(" / ")
    }

    /**
     * 정원 타이틀 (신입생 정원이 있으면 "(재학생)" 표시)
     * ex) "정원" 또는 "정원(재학생)"
     */
    fun getQuotaTitle(info: LectureSyllabusInfo, context: Context): String = buildString {
        append(context.getString(R.string.lecture_detail_quota))
        if (info.freshmanQuota != 0L) {
            append("(${context.getString(R.string.lecture_detail_senior)})")
        }
    }

    /**
     * 정원 표시 (신입생 정원이 있으면 재학생 정원도 표시)
     * ex) "150" 또는 "150(120)"
     */
    fun getFullQuota(info: LectureSyllabusInfo): String = buildString {
        append(info.quota)
        if (info.freshmanQuota != 0L) {
            append("(${info.quota - info.freshmanQuota})")
        }
    }
}
