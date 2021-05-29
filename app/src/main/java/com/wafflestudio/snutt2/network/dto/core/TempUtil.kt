package com.wafflestudio.snutt2.network.dto.core

import com.wafflestudio.snutt2.model.*

object TempUtil {
    fun toLegacyModel(dto: LectureDto): Lecture =
        Lecture()

    fun toLegacyModel(dto: TableDto): Table =
        Table()

    fun toLegacyModel(dto: ColorDto): Color = Color()

    fun toLegacyModel(dto: NotificationDto): Notification = Notification(
        id = "",
        message = "",
        type = 1
    )

    fun toLegacyModel(dto: UserDto): User = User()

    fun toDto(lecture: Lecture): LectureDto = LectureDto(
        id = "",
        classification = "",
        department = "",
        academic_year = "",
        course_number = "",
        lecture_number = "",
        course_title = "",
        credit = 0L,
        class_time = "",
        class_time_mask = emptyList(),
        class_time_json = emptyList(),
        location = "",
        quota = 0L,
        enrollment = 0L,
        instructor = "",
        remark = "",
        category = "",
        colorIndex = 0L
    )

    fun toDto(table: Table): TableDto = TableDto(
        year = 0L,
        semester = 0L,
        title = "",
        _id = "",
        lectureList = emptyList(),
        updated_at = ""
    )
}
