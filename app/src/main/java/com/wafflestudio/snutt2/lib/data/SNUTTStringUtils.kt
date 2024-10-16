package com.wafflestudio.snutt2.lib.data

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.SearchTimeDto
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SNUTTStringUtils {
    fun getFullSemester(tableDto: TableDto): String {
        val yearString = tableDto.year.toString()
        val semesterString = when (tableDto.semester) {
            1L -> "1"
            2L -> "S"
            3L -> "2"
            4L -> "W"
            else -> {
                Timber.e("semester is out of range!!")
                ""
            }
        }
        return "$yearString-$semesterString"
    }

    // 간소화된 강의 시간
    fun getSimplifiedClassTime(lectureDto: LectureDto): String {
        val texts = StringBuilder()
        lectureDto.class_time_json.forEachIndexed { index, classTimeDto ->
            texts.append(getClassTimeText(classTimeDto))
            texts.append(")")
            if (index != lectureDto.class_time_json.size - 1) texts.append(", ")
        }
        if (texts.isEmpty()) texts.append("(없음)")
        return texts.toString()
    }

    fun getSimplifiedLocation(lectureDto: LectureDto): String {
        val text = StringBuilder()
        val places = lectureDto.class_time_json.map { it.place }.distinct()
        places.forEachIndexed { index, place ->
            text.append(place)
            if (index != places.size - 1 && place.isNotEmpty()) text.append(" / ")
        }
        if (text.isEmpty()) text.append("(없음)")
        return text.toString()
    }

    fun getNotificationTime(context: Context, info: NotificationDto): String {
        try {
            val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date1 = format.parse(info.createdAt) ?: Date()
            val date2 = Date()

            val diff = date2.time - date1.time
            val hours = diff / (1000 * 60 * 60)
            val minutes = diff / (1000 * 60)
            val days = hours / 24
            return when {
                days > 0 -> {
                    SimpleDateFormat("yyyy/MM/dd").format(date1)
                }
                hours > 0 -> {
                    context.getString(R.string.time_hours_ago, hours)
                }
                minutes > 0 -> {
                    context.getString(R.string.time_minutes_ago, minutes)
                }
                else -> {
                    context.getString(R.string.time_now)
                }
            }
        } catch (e: ParseException) {
            Timber.e("notification created time parse error!")
            return ""
        }
    }

    fun getLectureTagText(lecture: LectureDto): String {
        return listOf(
            lecture.category,
            lecture.department,
            lecture.academic_year,
        )
            .filter { it.isNullOrBlank().not() }
            .let {
                if (it.isEmpty()) "(없음)" else it.joinToString(", ")
            }
    }

    fun getCreditSumFromLectureList(lectureList: List<LectureDto>): Long {
        return lectureList.fold(0L) { acc, lecture -> acc + lecture.credit }
    }

    fun getInstructorAndCreditText(lecture: LectureDto): String {
        return lecture.instructor + " / " + lecture.credit + "학점"
    }

    fun getClassTimeText(classTime: ClassTimeDto): String {
        return StringBuilder()
            .append(SNUTTUtils.numberToWday(classTime.day))
            .append("(")
            .append("%02d:%02d".format(classTime.startTimeHour, classTime.startTimeMinute))
            .append("~")
            .append("%02d:%02d".format(classTime.endTimeHour, classTime.endTimeMinute))
            .toString()
    }

    // 570 -> 오전 09:30
    fun Int.toFormattedTimeString(): String {
        val amPm = if (this < SearchTimeDto.MIDDAY) "오전" else "오후"
        val hour = (this / 60).let {
            if (it != 12) it % 12 else it
        }
        return String.format("%s %d:%02d", amPm, hour, this % 60)
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
        return if (this.isEmpty()) {
            true
        } else {
            regex.matches(this).not()
        }
    }

    fun String.creditStringToLong(): Long {
        return try {
            this.toLong().coerceAtLeast(0L)
        } catch (e: Exception) {
            0
        }
    }

    fun LectureDto.getQuotaTitle(context: Context): String = StringBuilder().apply {
        append(context.getString(R.string.lecture_detail_quota))
        if (freshmanQuota != null && freshmanQuota != 0L) append("(${context.getString(R.string.lecture_detail_senior)})")
    }.toString()

    fun LectureDto.getFullQuota(): String = StringBuilder().apply {
        append(quota)
        if (freshmanQuota != null && freshmanQuota != 0L) {
            append("(${quota - freshmanQuota})")
        }
    }.toString()
}
