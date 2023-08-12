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
    @Json(name = "bg") val bgRaw: String? = null,
) : Parcelable {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor),
    )

    /* TODO: Native Canvas 에서 그릴 땐 Int 가 필요하지만, Compose Color 은 ULong 이다.
     *       native canvas 대체할 방법 찾으면 그때 개편하기
    */
    val fgColor: Int?
        get() = if (fgRaw != null) Color.parseColor(fgRaw) else null

    val bgColor: Int?
        get() = if (bgRaw != null) Color.parseColor(bgRaw) else null
}
