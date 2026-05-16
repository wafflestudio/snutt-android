package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.CourseBook

@JsonClass(generateAdapter = true)
data class CourseBookLocalEntity(
    val semester: Long,
    val year: Long,
)

fun CourseBookLocalEntity.toDomainModel(): CourseBook = CourseBook(
    semester = semester,
    year = year,
)

fun CourseBook.toLocalEntity(): CourseBookLocalEntity = CourseBookLocalEntity(
    semester = semester,
    year = year,
)
