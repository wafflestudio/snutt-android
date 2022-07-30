package com.wafflestudio.snutt2.lib.data

import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import timber.log.Timber
import java.text.DateFormat
import java.text.DecimalFormat
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
        var text = ""
        lectureDto.class_time_json.forEachIndexed { index, classTimeDto ->
            val day = classTimeDto.day
            val start = classTimeDto.start
            val len = classTimeDto.len
            val place = classTimeDto.place
            text += SNUTTUtils.numberToWday(day.toInt()) + DecimalFormat().format(start.toDouble())
            if (index != lectureDto.class_time_json.size - 1) text += "/"
        }
        if (text.isEmpty()) text = "(없음)"
        return text
    }

    fun getSimplifiedLocation(lectureDto: LectureDto): String {
        var text = ""
        lectureDto.class_time_json.forEachIndexed { index, classTimeDto ->
            val day = classTimeDto.day
            val start = classTimeDto.start
            val len = classTimeDto.len
            val place = classTimeDto.place
            text += place
            if (index != lectureDto.class_time_json.size - 1) text += "/"
        }
        if (text.isEmpty()) text = "(없음)"
        return text
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
                    "$hours 시간 전"
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
}
