package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.database.model.SimpleTable
import com.wafflestudio.snutt2.core.network.model.SimpleTableDto as SimpleTableDtoNetwork

@JsonClass(generateAdapter = true)
data class SimpleTableDto(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
) {
    companion object {
        val Default = SimpleTableDto(
            id = "",
            year = 2022,
            semester = 1L,
            title = "",
            updatedAt = "",
            totalCredit = null,
        )
    }
}

fun SimpleTableDtoNetwork.toExternalModel() = SimpleTableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)

fun SimpleTable.toExternalModel() = SimpleTableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)

fun SimpleTableDto.toDatabaseModel() = SimpleTable(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)
