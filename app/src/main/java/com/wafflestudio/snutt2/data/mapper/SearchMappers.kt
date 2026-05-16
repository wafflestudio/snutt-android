package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.SearchTime
import com.wafflestudio.snutt2.network.dto.SearchTimeDto

fun SearchTime.toDto(): SearchTimeDto = SearchTimeDto(
    // NOTE: DayOfWeek는 1이 월요일이고, 서버는 0이 월요일이다
    day = day.value - 1,
    startMinute = startTime.hour * 60 + startTime.minute,
    endMinute = endTime.hour * 60 + endTime.minute,
)
