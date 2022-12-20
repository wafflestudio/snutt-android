package com.wafflestudio.snutt2.lib.data

import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
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
            texts.append(SNUTTUtils.numberToWday(classTimeDto.day))
            texts.append(" ")
            texts.append(classTimeDto.start_time)
            texts.append("~")
            texts.append(classTimeDto.end_time)
            if (index != lectureDto.class_time_json.size - 1) texts.append("/")
        }
        if (texts.isEmpty()) texts.append("(없음)")
        return texts.toString()
    }

    fun getSimplifiedLocation(lectureDto: LectureDto): String {
        val text = StringBuilder()
        lectureDto.class_time_json.forEachIndexed { index, classTimeDto ->
            text.append(classTimeDto.place)
            if (index != lectureDto.class_time_json.size - 1 && classTimeDto.place.isNotEmpty()) text.append("/")
        }
        if (text.isEmpty()) text.append("(없음)")
        return text.toString()
    }

    fun getNotificationTime(info: NotificationDto): String {
        try {
            val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date1 = format.parse(info.createdAt) ?: Date()
            val date2 = Date()

            val diff = date2.time - date1.time
            val hours = diff / (1000 * 60 * 60)
            val days = hours / 24
            return when {
                days > 0 -> {
                    DateFormat.getDateInstance().format(date1)
                }
                hours > 0 -> {
                    "$hours 시간 전" // TODO: resource로 빼기
                }
                else -> {
                    "방금"
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
            lecture.academic_year
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
            .append(" ")
            .append(classTime.start_time)
            .append("~")
            .append(classTime.end_time)
            .toString()
    }

    fun String.isEmailInvalid(): Boolean {
        val regex = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        )
        return if (this.isEmpty()) true
        else regex.matches(this).not()
    }
}
