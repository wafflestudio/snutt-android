package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.storage.model.TableLectureCustomData

data class TableLectureCustom(
    val title: Boolean,
    val place: Boolean,
    val lectureNumber: Boolean,
    val instructor: Boolean,
) {
    companion object {
        val Default = TableLectureCustom(true, true, false, false)
    }
}

fun TableLectureCustom.toDataModel(): TableLectureCustomData = TableLectureCustomData(
    title = title,
    place = place,
    lectureNumber = lectureNumber,
    instructor = instructor,
)
