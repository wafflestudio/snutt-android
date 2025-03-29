package com.wafflestudio.snutt2.lib.data

import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object SNUTTStringUtilsNew {
    fun getInstructorAndCreditText(lecture: Lecture): String {
        val creditText = "${lecture.credit}학점"

        if (lecture.instructor.isBlank()) {
            return creditText
        }

        return "${lecture.instructor} / $creditText"
    }

    fun getLectureTagText(lecture: LocalLecture): String {
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
                "(없음)"
            }
        }
    }

    /**
     * 강의의 모든 classTime을 text로 변환
     * ex) 월, 수 09:30 ~ 10:45 이면 -> "월(09:30~10:45), 수(09:30~10:45)"
     */
    fun getSimplifiedClassTimeForLecture(lecture: Lecture): String {
        if (lecture.lectureSessions.isEmpty()) {
            return "(없음)"
        }

        return lecture.lectureSessions.joinToString(", ", transform = ::getLectureSessionString)
    }

    /**
     * 하나의 session을 텍스트로 변환
     * ex) 월 09:30 ~ 10:45 이면 -> "월(09:30~10:45)"
     */
    private fun getLectureSessionString(lectureSession: LectureSession): String = buildString {
        append(
            lectureSession.day.getDisplayName(TextStyle.SHORT, Locale.KOREA),
        )
        append("(")
        append(lectureSession.startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        append("~")
        append(lectureSession.endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        append(")")
    }

    fun getSimplifiedLocation(lecture: Lecture): String {
        val places = lecture.lectureSessions.map { it.place }
            .filter { it.isNotBlank() }
            .distinct()

        if (places.isEmpty()) {
            return "(없음)"
        }

        return places.joinToString(" / ")
    }
}
