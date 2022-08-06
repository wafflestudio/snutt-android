package com.wafflestudio.snutt2.data

import android.content.Context
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.wafflestudio.snutt2.R

enum class TimetableColorTheme(val code: Int) {

    SNUTT(0),
    MODERN(1),
    AUTUMN(2),
    CHERRY(3),
    ICE(4),
    GRASS(5);

    fun getColorByIndex(context: Context, colorIndex: Long): Int {
        return when (this) {
            SNUTT -> listOf(
                context.getColor(R.color.theme_snutt_0),
                context.getColor(R.color.theme_snutt_1),
                context.getColor(R.color.theme_snutt_2),
                context.getColor(R.color.theme_snutt_3),
                context.getColor(R.color.theme_snutt_4),
                context.getColor(R.color.theme_snutt_5),
                context.getColor(R.color.theme_snutt_6),
                context.getColor(R.color.theme_snutt_7),
                context.getColor(R.color.theme_snutt_8),
            )
            MODERN -> listOf(
                context.getColor(R.color.theme_modern_0),
                context.getColor(R.color.theme_modern_1),
                context.getColor(R.color.theme_modern_2),
                context.getColor(R.color.theme_modern_3),
                context.getColor(R.color.theme_modern_4),
                context.getColor(R.color.theme_modern_5),
                context.getColor(R.color.theme_modern_6),
                context.getColor(R.color.theme_modern_7),
                context.getColor(R.color.theme_modern_8),
            )
            AUTUMN -> listOf(
                context.getColor(R.color.theme_autumn_0),
                context.getColor(R.color.theme_autumn_1),
                context.getColor(R.color.theme_autumn_2),
                context.getColor(R.color.theme_autumn_3),
                context.getColor(R.color.theme_autumn_4),
                context.getColor(R.color.theme_autumn_5),
                context.getColor(R.color.theme_autumn_6),
                context.getColor(R.color.theme_autumn_7),
                context.getColor(R.color.theme_autumn_8),
            )
            CHERRY -> listOf(
                context.getColor(R.color.theme_cherry_0),
                context.getColor(R.color.theme_cherry_1),
                context.getColor(R.color.theme_cherry_2),
                context.getColor(R.color.theme_cherry_3),
                context.getColor(R.color.theme_cherry_4),
                context.getColor(R.color.theme_cherry_5),
                context.getColor(R.color.theme_cherry_6),
                context.getColor(R.color.theme_cherry_7),
                context.getColor(R.color.theme_cherry_8),
            )
            ICE -> listOf(
                context.getColor(R.color.theme_ice_0),
                context.getColor(R.color.theme_ice_1),
                context.getColor(R.color.theme_ice_2),
                context.getColor(R.color.theme_ice_3),
                context.getColor(R.color.theme_ice_4),
                context.getColor(R.color.theme_ice_5),
                context.getColor(R.color.theme_ice_6),
                context.getColor(R.color.theme_ice_7),
                context.getColor(R.color.theme_ice_8),
            )
            GRASS -> listOf(
                context.getColor(R.color.theme_grass_0),
                context.getColor(R.color.theme_grass_1),
                context.getColor(R.color.theme_grass_2),
                context.getColor(R.color.theme_grass_3),
                context.getColor(R.color.theme_grass_4),
                context.getColor(R.color.theme_grass_5),
                context.getColor(R.color.theme_grass_6),
                context.getColor(R.color.theme_grass_7),
                context.getColor(R.color.theme_grass_8),
            )
        }[colorIndex.toInt() - 1]
    }

    companion object {
        fun fromInt(type: Int) = values().associateBy(TimetableColorTheme::code)[type]
    }
}

class TimetableColorThemeAdapter {
    @ToJson
    fun toJson(type: TimetableColorTheme): Int = type.code

    @FromJson
    fun fromJson(value: String): TimetableColorTheme? = TimetableColorTheme.fromInt(value.toInt())
}
