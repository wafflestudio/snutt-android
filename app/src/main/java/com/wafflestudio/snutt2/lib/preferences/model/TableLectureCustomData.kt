package com.wafflestudio.snutt2.lib.preferences.model

import com.wafflestudio.snutt2.domainmodel.TableLectureCustom

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
