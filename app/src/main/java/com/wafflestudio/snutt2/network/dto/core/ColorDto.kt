package com.wafflestudio.snutt2.network.dto.core

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

    val fgColor: Int
        get() {
            return if (fgRaw == null) {
                ColorConst.defaultFgColor
            } else {
                Color.parseColor(fgRaw)
            }
        }

    val bgColor: Int
        get() {
            return if (bgRaw == null) {
                ColorConst.defaultBgColor
            } else {
                Color.parseColor(bgRaw)
            }
        }
}
