package com.wafflestudio.snutt2.domainmodel

data class Table(
    val summary: TableSummary,
    val lectures: List<LocalLecture>,
    val themeRef: ThemeReference,
)

data class TableSummary(
    val id: String,
    val courseBook: CourseBook,
    val title: String,
    val totalCredit: Long,
    val isPrimary: Boolean,
) {
    fun courseBookEquals(other: TableSummary) = courseBook == other.courseBook
    fun courseBookEquals(courseBook: CourseBook) = this.courseBook == courseBook

    companion object {
        val Default = TableSummary(
            id = "",
            courseBook = CourseBook(1, 2026),
            title = "테스트 강의",
            totalCredit = 3,
            isPrimary = false,
        )
    }
}
