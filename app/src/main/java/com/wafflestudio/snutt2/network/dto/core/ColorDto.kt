package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ColorDto(val fg: String, val bg: String)
