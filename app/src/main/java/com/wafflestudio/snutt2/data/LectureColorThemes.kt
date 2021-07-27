package com.wafflestudio.snutt2.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

enum class TimetableColorTheme(val code: Int) {
    UNKNOWN(-1),
    SNUTT(0),
    MODERN(1),
    AUTUMN(2),
    PINK(3),
    ICE(4),
    JADE(5);

    companion object {
        fun fromInt(type: Int) = values().associateBy(TimetableColorTheme::code)[type] ?: UNKNOWN
    }
}

class TimetableColorThemeAdapter() {
    @ToJson
    fun toJson(type: TimetableColorTheme): Int = type.code

    @FromJson
    fun fromJson(value: String): TimetableColorTheme = TimetableColorTheme.fromInt(value.toInt())
}
