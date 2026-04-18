package com.wafflestudio.snutt2.lib

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun getLocalDateTimeFromString(data: String): LocalDateTime {
    val instant = Instant.parse(data)
    return instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
}
