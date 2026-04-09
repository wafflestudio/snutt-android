package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class ColorSetDto(
    @param:Json(name = "fg") val fg: String? = null,
    @param:Json(name = "bg") val bg: String? = null,
) {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor),
    )

    val fgColor: Int?
        get() = fg?.let { parseHexColor(it) }

    val bgColor: Int?
        get() = bg?.let { parseHexColor(it) }
}
