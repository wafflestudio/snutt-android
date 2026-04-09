package com.wafflestudio.snutt2.storage.model

import com.wafflestudio.snutt2.domain.model.TableLectureCustom

data class TableLectureCustomData(
    val title: Boolean,
    val place: Boolean,
    val lectureNumber: Boolean,
    val instructor: Boolean,
) {
    companion object {
        val Default = TableLectureCustomData(true, true, false, false)
    }
}

fun TableLectureCustomData.toDomainModel(): TableLectureCustom = TableLectureCustom(
    title = title,
    place = place,
    lectureNumber = lectureNumber,
    instructor = instructor,
)
