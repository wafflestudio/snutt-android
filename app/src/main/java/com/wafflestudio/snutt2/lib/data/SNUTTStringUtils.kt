package com.wafflestudio.snutt2.lib.data

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SearchTime
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object SNUTTStringUtils {
    /**
     * 강의의 모든 lectureSession을 text로 변환
     * ex) 월, 수 09:30 ~ 10:45 이면 -> "월(09:30~10:45), 수(09:30~10:45)"
     */
    fun getSimplifiedClassTimeForLecture(context: Context, lecture: SearchedLecture): String {
        if (lecture.lectureSessions.isEmpty()) {
            return context.getString(R.string.lecture_detail_hint_nothing)
        }

        return lecture.lectureSessions.joinToString(", ", transform = ::getClassTimeTextForLecture)
    }

    /**
     * 하나의 classTime을 텍스트로 변환, 강의 내의 모든 classTime에 대해 필요할 때
     * ex) 월 09:30 ~ 10:45 이면 -> "월(09:30~10:45)"
     */
    private fun getClassTimeTextForLecture(session: LectureSession): String = buildString {
        append(session.day.getString())
        append("(")
        append(session.startTime.getHourMinuteString())
        append("~")
        append(session.endTime.getHourMinuteString())
        append(")")
    }

    fun getSimplifiedLocation(context: Context, lecture: SearchedLecture): String {
        val text = StringBuilder()
        val places = lecture.lectureSessions.map { it.place }.distinct()
        places.forEachIndexed { index, place ->
            text.append(place)
            if (index != places.size - 1 && place.isNotEmpty()) text.append(" / ")
        }
        if (text.isEmpty()) text.append(context.getString(R.string.lecture_detail_hint_nothing))
        return text.toString()
    }

    fun getLocalDateTimeFromString(data: String): LocalDateTime {
        val instant = Instant.parse(data)

        // 서버에서 내려오는 createdAt이 GMT+0 기준이기 때문에 KST로 변환해준다.
        return instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
    }

    fun getNotificationTime(context: Context, dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val now = LocalDateTime.now()
        val duration = Duration.between(dateTime, now)

        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()

        return when {
            days > 0 -> dateTime.format(formatter)
            hours > 0 -> context.getString(R.string.time_hours_ago, hours)
            minutes > 0 -> context.getString(R.string.time_minutes_ago, minutes)
            else -> context.getString(R.string.time_now)
        }
    }

    fun getLectureTagText(context: Context, lecture: SearchedLecture): String {
        return listOf(
            lecture.category,
            lecture.department,
            lecture.academicYear,
        )
            .filter { it.isBlank().not() }
            .let {
                if (it.isEmpty()) context.getString(R.string.lecture_detail_hint_nothing) else it.joinToString(", ")
            }
    }

    fun getCreditSumFromLectureList(lectureList: List<Lecture>): Long {
        return lectureList.fold(0L) { acc, lecture -> acc + lecture.credit }
    }

    // 570 -> 오전 09:30 / 9:30 AM
    fun Int.toFormattedTimeString(context: Context): String {
        val amPm = if (this < SearchTime.MIDDAY_MINUTE) context.getString(R.string.morning) else context.getString(R.string.afternoon)
        val hour = (this / 60).let {
            if (it != 12) it % 12 else it
        }
        return context.getString(R.string.time_format_am_pm, amPm, hour, this % 60)
    }

    fun String.isEmailInvalid(): Boolean {
        val regex = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+",
        )
        return this.isEmpty() || regex.matches(this).not()
    }

    fun String.isPasswordInvalid(): Boolean =
        Regex("^(?=.*\\d)(?=.*[a-zA-Z])\\S{6,20}\$").matches(this).not()

    fun String.isIdInvalid(): Boolean = Regex("^[A-Za-z\\d]{4,32}\$").matches(this).not()

    fun LocalTime.getHourMinuteString(): String = buildString {
        append("%02d:%02d".format(this@getHourMinuteString.hour, this@getHourMinuteString.minute))
    }

    fun DayOfWeek.getString(): String =
        this.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
}
