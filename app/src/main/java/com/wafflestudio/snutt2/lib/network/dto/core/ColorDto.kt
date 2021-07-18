package com.wafflestudio.snutt2.lib.network.dto.core

import android.graphics.Color
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.ColorConst

@JsonClass(generateAdapter = true)
data class ColorDto(
    @Json(name = "fg") val fgRaw: String? = null,
    @Json(name = "bg") val bgRaw: String? = null
) {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor)
    )

    val fgColor: Int?
        get() = if (fgRaw != null) Color.parseColor(fgRaw) else null

    val bgColor: Int?
        get() = if (bgRaw != null) Color.parseColor(bgRaw) else null
}
