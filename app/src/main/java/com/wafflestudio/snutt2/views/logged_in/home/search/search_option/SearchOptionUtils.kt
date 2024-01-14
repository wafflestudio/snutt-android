package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import com.wafflestudio.snutt2.model.SearchTimeDto
import kotlin.math.roundToInt

fun List<List<Boolean>>.clusterToTimeBlocks(): List<SearchTimeDto> {
    val timeSlots = (8 * 2..22 * 2).map { it / 2f }
    val list = mutableListOf<SearchTimeDto>()

    for (dayIndex in indices) {
        var clusterStart: Int? = null

        for (timeIndex in this[dayIndex].indices) {
            if (this[dayIndex][timeIndex] && clusterStart == null) {
                // 클러스터가 이어지지 않고 있고, 현재 시간이 true일 경우 클러스터 시작
                clusterStart = timeIndex
            } else {
                // 현재 시간이 false일 경우 클러스터 끝
                if (this[dayIndex][timeIndex].not() && clusterStart != null) {
                    val clusterEnd = timeIndex
                    list.add(SearchTimeDto(dayIndex, (timeSlots[clusterStart] * 60).roundToInt(), (timeSlots[clusterEnd] * 60).roundToInt()))
                    clusterStart = null
                }
            }
        }

        // 마지막 시간이 true로 끝나는 경우 처리
        if (clusterStart != null) {
            val clusterEnd = this[dayIndex].lastIndexOf(true) + 1
            list.add(SearchTimeDto(dayIndex, (timeSlots[clusterStart] * 60).roundToInt(), (timeSlots[clusterEnd] * 60).roundToInt()))
        }
    }
    return list
}

fun timeSlotsToFormattedString(booleanArray: List<List<Boolean>>): String {
    val daysOfWeek = listOf("월", "화", "수", "목", "금")
    val timeSlots = (8 * 2..22 * 2).map { it / 2f }
    val builder = StringBuilder()

    for (dayIndex in booleanArray.indices) {
        var first = true
        val dayOfWeek = daysOfWeek[dayIndex]
        var clusterStart: Int? = null

        for (timeIndex in booleanArray[dayIndex].indices) {
            if (booleanArray[dayIndex][timeIndex] && clusterStart == null) {
                // 클러스터가 이어지지 않고 있고, 현재 시간이 true일 경우 클러스터 시작
                clusterStart = timeIndex
            } else {
                // 현재 시간이 false일 경우 클러스터 끝
                if (booleanArray[dayIndex][timeIndex].not() && clusterStart != null) {
                    val clusterEnd = timeIndex - 1
                    val timeRange = getTimeRange(timeSlots[clusterStart], timeSlots[clusterEnd])
                    if (first.not()) {
                        builder.append(", ")
                    } else {
                        first = false
                    }
                    builder.append("$dayOfWeek : $timeRange")
                    clusterStart = null
                }
            }
        }

        // 마지막 시간이 true로 끝나는 경우 처리
        if (clusterStart != null) {
            val clusterEnd = booleanArray[dayIndex].lastIndexOf(true)
            val timeRange = getTimeRange(timeSlots[clusterStart], timeSlots[clusterEnd])
            builder.append("$dayOfWeek: $timeRange")
        }
        if (booleanArray[dayIndex].any { it }) builder.append("\n")
    }
    return builder.toString()
}

fun getTimeRange(startTime: Float, endTime: Float): String {
    val startHour = startTime.toInt()
    val startMinute = ((startTime - startHour) * 60).toInt()
    val endHour = (endTime + 0.5f).toInt()
    val endMinute = (((endTime + 0.5f) - endHour) * 60).toInt()

    return "$startHour:${String.format("%02d", startMinute)}-$endHour:${
    String.format(
        "%02d",
        endMinute,
    )
    }"
}
