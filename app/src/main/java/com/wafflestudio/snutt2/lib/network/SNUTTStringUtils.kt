package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import timber.log.Timber
import java.text.DecimalFormat

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
}
