package com.wafflestudio.snutt2.lib.network.dto.core

import android.graphics.Color
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ColorDto(
    @Json(name = "fg") val fgRaw: String? = null,
    @Json(name = "bg") val bgRaw: String? = null
) : Parcelable {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor)
    )

    val fgColor: Int?
        get() = if (fgRaw != null) Color.parseColor(fgRaw) else null

    val bgColor: Int?
        get() = if (bgRaw != null) Color.parseColor(bgRaw) else null
}
