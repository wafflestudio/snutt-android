package com.wafflestudio.snutt2.lib

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto


fun LectureDto.contains(queryDay: Int, queryTime: Float): Boolean {
    for (classTimeDto in this.class_time_json) {
        val day1 = classTimeDto.day
        val start1 = classTimeDto.start
        val len1 = classTimeDto.len
        val end1 = start1 + len1
        val len2 = 0.5f
        val end2 = queryTime + len2
        if (day1 != queryDay) continue
        if (!(end1 <= queryTime || end2 <= start1)) return true
    }
    return false
}
