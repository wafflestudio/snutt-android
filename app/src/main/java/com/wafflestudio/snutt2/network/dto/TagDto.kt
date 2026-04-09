package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.TagType

@JsonClass(generateAdapter = true)
data class TagDto(
    val type: TagType,
    val name: String,
) {
    companion object {
        val TIME_EMPTY: TagDto = TagDto(TagType.TIME, "빈 시간대로 검색")
        val TIME_SELECT: TagDto = TagDto(TagType.TIME, "시간대 직접 선택")
        val ETC_ENG: TagDto = TagDto(TagType.ETC, "영어진행 강의")
        val ETC_MILITARY: TagDto = TagDto(TagType.ETC, "군휴학 원격수업")
    }

    fun toItemKey(): String = type.toString() + name
}
