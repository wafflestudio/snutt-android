package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.TableSummary

@JsonClass(generateAdapter = true)
data class SimpleTableLocalEntity(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "year") val year: Long,
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "updated_at") val updatedAt: String,
    @param:Json(name = "total_credit") val totalCredit: Long?,
    @param:Json(name = "isPrimary") val isPrimary: Boolean = false,
)

fun SimpleTableLocalEntity.toDomainModel(): TableSummary = TableSummary(
    id = id,
    courseBook = CourseBook(semester, year),
    title = title,
    totalCredit = totalCredit ?: 0,
    isPrimary = isPrimary,
)
