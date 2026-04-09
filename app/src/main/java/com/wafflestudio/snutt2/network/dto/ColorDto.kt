package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorDto(
    @param:Json(name = "fg") val fgRaw: String? = null,
    @param:Json(name = "bg") val bgRaw: String? = null,
) {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor),
    )

    val fgColor: Int?
        get() = fgRaw?.let { parseHexColor(it) }

    val bgColor: Int?
        get() = bgRaw?.let { parseHexColor(it) }
}

fun parseHexColor(hex: String): Int {
    val stripped = if (hex.startsWith("#")) hex.substring(1) else hex
    return when (stripped.length) {
        6 -> (0xFF000000.toInt() or stripped.toLong(16).toInt())
        8 -> stripped.toLong(16).toInt()
        else -> throw IllegalArgumentException("Invalid hex color: $hex")
    }
}
