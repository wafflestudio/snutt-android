package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto

data class Table(
    val summary: TableSummary,
    val lectures: List<LocalLecture>,
    val themeRef: ThemeReference,
) {
    companion object {
        fun fromTableDto(dto: TableDto): Table {
            val themeRef = dto.themeId?.let {
                ThemeReference.Custom(it)
            } ?: ThemeReference.BuiltIn(dto.theme)

            return Table(
                summary = TableSummary(
                    id = dto.id,
                    courseBook = CourseBook(dto.year, dto.semester),
                    title = dto.title,
                    totalCredit = dto.totalCredit ?: 0,
                    isPrimary = dto.isPrimary,
                ),
                lectures = dto.lectureList.map { it.toLocalLecture() },
                themeRef = themeRef,
            )
        }
    }
}

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
        fun fromSimpleTableDto(dto: SimpleTableDto): TableSummary = TableSummary(
            id = dto.id,
            courseBook = CourseBook(dto.semester, dto.year),
            title = dto.title,
            totalCredit = dto.totalCredit ?: 0,
            isPrimary = dto.isPrimary,
        )
    }
}