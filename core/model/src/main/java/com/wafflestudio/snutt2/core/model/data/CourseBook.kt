package com.wafflestudio.snutt2.core.model.data

data class CourseBook(
    val semester: Long,
    val year: Long,
): Comparable<CourseBook> {
    override fun compareTo(other: CourseBook): Int {
        if (year > other.year) {
            return -1
        } else if (year < other.year) {
            return 1
        } else {
            if (semester > other.semester) {
                return -1
            } else if (semester < other.semester) return 1
        }
        return 0
    }
}