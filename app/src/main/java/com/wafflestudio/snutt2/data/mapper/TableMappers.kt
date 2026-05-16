package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.network.dto.ColorDto
import com.wafflestudio.snutt2.network.dto.SimpleTableDto
import com.wafflestudio.snutt2.network.dto.TableDto
import com.wafflestudio.snutt2.network.dto.ThemeDto
import com.wafflestudio.snutt2.network.dto.TimetableDto
import com.wafflestudio.snutt2.network.dto.parseHexColor
import com.wafflestudio.snutt2.storage.model.SimpleTableLocalEntity

// region TableDto / TimetableDto / SimpleTableDto → Table / TableSummary

fun TableDto.toDomain(): Table {
    val themeRef = themeId?.let { ThemeReference.Custom(it) }
        ?: ThemeReference.BuiltIn(theme)
    return Table(
        summary = TableSummary(
            id = id,
            courseBook = CourseBook(semester, year),
            title = title,
            totalCredit = totalCredit ?: 0,
            isPrimary = isPrimary,
        ),
        lectures = lectureList.map { it.toLocalLecture() },
        themeRef = themeRef,
    )
}

fun TimetableDto.toDomain(): Table {
    val themeRef = themeId?.let { ThemeReference.Custom(it) }
        ?: ThemeReference.BuiltIn(theme.toIntOrNull() ?: 0)
    return Table(
        summary = TableSummary(
            id = id ?: "",
            courseBook = CourseBook(semester.toLongOrNull() ?: 1, year.toLong()),
            title = title,
            totalCredit = 0, // TimetableDto에는 totalCredit 정보가 없음
            isPrimary = isPrimary,
        ),
        lectures = lectures.map { it.toLocalLecture() },
        themeRef = themeRef,
    )
}

fun SimpleTableDto.toDomain(): TableSummary = TableSummary(
    id = id,
    courseBook = CourseBook(semester, year),
    title = title,
    totalCredit = totalCredit ?: 0,
    isPrimary = isPrimary,
)

fun SimpleTableDto.toLocalEntity(): SimpleTableLocalEntity = SimpleTableLocalEntity(
    id = id,
    year = year,
    semester = semester,
    title = title,
    updatedAt = updatedAt,
    totalCredit = totalCredit,
    isPrimary = isPrimary,
)

// endregion

// region Table → TableDto / TimetableDto

// FIXME: 리팩토링 전 컴포넌트(TableState) 사용을 위한 임시 변환 함수
// updatedAt 등 일부 필드는 기본값 사용
fun Table.toTableDto(): TableDto = TableDto(
    id = summary.id,
    year = summary.courseBook.year,
    semester = summary.courseBook.semester,
    title = summary.title,
    lectureList = lectures.map { it.toLectureDto() },
    updatedAt = "", // FIXME: Table에 updatedAt 정보 없음
    totalCredit = summary.totalCredit,
    theme = when (val ref = themeRef) {
        is ThemeReference.BuiltIn -> ref.code
        is ThemeReference.Custom -> 0 // FIXME: Custom theme의 경우 기본값 사용
    },
    themeId = when (val ref = themeRef) {
        is ThemeReference.Custom -> ref.themeId
        is ThemeReference.BuiltIn -> null
    },
    isPrimary = summary.isPrimary,
)

fun Table.toTimetableDto(userId: String): TimetableDto = TimetableDto(
    id = summary.id,
    userId = userId,
    year = summary.courseBook.year.toInt(),
    semester = summary.courseBook.semester.toString(),
    title = summary.title,
    lectures = lectures.map { it.toTimetableLectureDto() },
    updatedAt = "", // FIXME: Table에 updatedAt 정보 없음
    theme = when (val ref = themeRef) {
        is ThemeReference.BuiltIn -> ref.code.toString()
        is ThemeReference.Custom -> "0" // FIXME: Custom theme의 경우 기본값 사용
    },
    themeId = when (val ref = themeRef) {
        is ThemeReference.Custom -> ref.themeId
        is ThemeReference.BuiltIn -> null
    },
    isPrimary = summary.isPrimary,
)

// endregion

// region ThemeDto / ColorDto → TableTheme / ThemeColor

fun ThemeDto.toTableTheme(): TableTheme = if (isCustom != false) {
    CustomTheme(
        id = id!!,
        name = name ?: "",
        colors = colors?.map { it.toThemeColor() } ?: emptyList(),
        isFromMarket = status == "DOWNLOADED",
    )
} else {
    BuiltInTheme.fromCode(theme ?: 0)
}

fun ColorDto.toThemeColor(): ThemeColor = ThemeColor(
    foreground = fgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
    background = bgRaw?.let { parseHexColor(it) } ?: 0xFFFFFFFF.toInt(),
)

// endregion
