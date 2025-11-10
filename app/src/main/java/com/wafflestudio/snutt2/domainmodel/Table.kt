package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TimetableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TimetableLectureDto

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
                    courseBook = CourseBook(dto.semester, dto.year),
                    title = dto.title,
                    totalCredit = dto.totalCredit ?: 0,
                    isPrimary = dto.isPrimary,
                ),
                lectures = dto.lectureList.map { it.toLocalLecture() },
                themeRef = themeRef,
            )
        }

        fun fromTimetableDto(dto: TimetableDto): Table {
            val themeRef = dto.themeId?.let {
                ThemeReference.Custom(it)
            } ?: ThemeReference.BuiltIn(dto.theme.toIntOrNull() ?: 0)

            return Table(
                summary = TableSummary(
                    id = dto.id ?: "",
                    courseBook = CourseBook(dto.semester.toLongOrNull() ?: 1, dto.year.toLong()),
                    title = dto.title,
                    totalCredit = 0, // TimetableDto에는 totalCredit 정보가 없음
                    isPrimary = dto.isPrimary,
                ),
                lectures = dto.lectures.map { it.toLocalLecture() },
                themeRef = themeRef,
            )
        }
    }

    // FIXME: 리팩토링 전 컴포넌트(TableState) 사용을 위한 임시 변환 함수
    // updatedAt 등 일부 필드는 기본값 사용
    fun toTableDto(): TableDto {
        return TableDto(
            id = summary.id,
            year = summary.courseBook.year,
            semester = summary.courseBook.semester,
            title = summary.title,
            lectureList = lectures.map { LectureDto.fromLocalLecture(it) },
            updatedAt = "", // FIXME: Table에 updatedAt 정보 없음
            totalCredit = summary.totalCredit,
            theme = when (themeRef) {
                is ThemeReference.BuiltIn -> themeRef.code
                is ThemeReference.Custom -> 0 // FIXME: Custom theme의 경우 기본값 사용
            },
            themeId = when (themeRef) {
                is ThemeReference.Custom -> themeRef.themeId
                is ThemeReference.BuiltIn -> null
            },
            isPrimary = summary.isPrimary,
        )
    }

    fun toTimetableDto(userId: String): TimetableDto {
        return TimetableDto(
            id = summary.id,
            userId = userId,
            year = summary.courseBook.year.toInt(),
            semester = summary.courseBook.semester.toString(),
            title = summary.title,
            lectures = lectures.map { TimetableLectureDto.fromLocalLecture(it) },
            updatedAt = "", // FIXME: Table에 updatedAt 정보 없음
            theme = when (themeRef) {
                is ThemeReference.BuiltIn -> themeRef.code.toString()
                is ThemeReference.Custom -> "0" // FIXME: Custom theme의 경우 기본값 사용
            },
            themeId = when (themeRef) {
                is ThemeReference.Custom -> themeRef.themeId
                is ThemeReference.BuiltIn -> null
            },
            isPrimary = summary.isPrimary,
        )
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

        val Default = TableSummary(
            id = "",
            courseBook = CourseBook(1, 2026),
            title = "테스트 강의",
            totalCredit = 3,
            isPrimary = false,
        )
    }
}
