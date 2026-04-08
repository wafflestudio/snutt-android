package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.lib.network.dto.SearchTimeDto
import com.wafflestudio.snutt2.lib.network.dto.TagDto

fun SearchTag.toDto(): TagDto = when (this) {
    is SearchTag.Regular -> TagDto(type, name)
    SearchTag.TimeEmpty -> TagDto.TIME_EMPTY
    SearchTag.TimeSelect -> TagDto.TIME_SELECT
    SearchTag.EtcEng -> TagDto.ETC_ENG
    SearchTag.EtcMilitary -> TagDto.ETC_MILITARY
}

fun SearchTime.toDto(): SearchTimeDto = SearchTimeDto(
    // NOTE: DayOfWeek는 1이 월요일이고, 서버는 0이 월요일이다
    day = day.value - 1,
    startMinute = startTime.hour * 60 + startTime.minute,
    endMinute = endTime.hour * 60 + endTime.minute,
)
