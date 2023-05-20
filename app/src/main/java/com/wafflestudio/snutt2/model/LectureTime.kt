package com.wafflestudio.snutt2.model

data class LectureTime(
    val day: Int,
    val startMinute: Int,
    val endMinute: Int,
) {
    companion object {
        const val FIRST = 0
        const val MIDDAY = 720
        const val LAST = 1435
    }
}
