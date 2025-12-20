package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType

@JsonClass(generateAdapter = true)
data class DiaryDailyClassTypeDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "name") val name: String,
)

fun DiaryDailyClassTypeDto.toDomainModel(): DiaryDailyClassType {
    return DiaryDailyClassType(
        id = id,
        name = name,
    )
}
