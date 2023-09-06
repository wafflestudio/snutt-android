package com.wafflestudio.snutt2.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.isDarkMode

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

    // TODO: 나중에 위에꺼 지우고 이름 바꾸기
    @Composable
    fun getColorByIndexComposable(colorIndex: Long): androidx.compose.ui.graphics.Color {
        return if (isDarkMode()) {
            when (this) {
                SNUTT -> listOf(
                    colorResource(R.color.theme_snutt_dark_0),
                    colorResource(R.color.theme_snutt_dark_1),
                    colorResource(R.color.theme_snutt_dark_2),
                    colorResource(R.color.theme_snutt_dark_3),
                    colorResource(R.color.theme_snutt_dark_4),
                    colorResource(R.color.theme_snutt_dark_5),
                    colorResource(R.color.theme_snutt_dark_6),
                    colorResource(R.color.theme_snutt_dark_7),
                    colorResource(R.color.theme_snutt_dark_8),
                )
                MODERN -> listOf(
                    colorResource(R.color.theme_modern_dark_0),
                    colorResource(R.color.theme_modern_dark_1),
                    colorResource(R.color.theme_modern_dark_2),
                    colorResource(R.color.theme_modern_dark_3),
                    colorResource(R.color.theme_modern_dark_4),
                    colorResource(R.color.theme_modern_dark_5),
                    colorResource(R.color.theme_modern_dark_6),
                    colorResource(R.color.theme_modern_dark_7),
                    colorResource(R.color.theme_modern_dark_8),
                )
                AUTUMN -> listOf(
                    colorResource(R.color.theme_autumn_dark_0),
                    colorResource(R.color.theme_autumn_dark_1),
                    colorResource(R.color.theme_autumn_dark_2),
                    colorResource(R.color.theme_autumn_dark_3),
                    colorResource(R.color.theme_autumn_dark_4),
                    colorResource(R.color.theme_autumn_dark_5),
                    colorResource(R.color.theme_autumn_dark_6),
                    colorResource(R.color.theme_autumn_dark_7),
                    colorResource(R.color.theme_autumn_dark_8),
                )
                CHERRY -> listOf(
                    colorResource(R.color.theme_cherry_dark_0),
                    colorResource(R.color.theme_cherry_dark_1),
                    colorResource(R.color.theme_cherry_dark_2),
                    colorResource(R.color.theme_cherry_dark_3),
                    colorResource(R.color.theme_cherry_dark_4),
                    colorResource(R.color.theme_cherry_dark_5),
                    colorResource(R.color.theme_cherry_dark_6),
                    colorResource(R.color.theme_cherry_dark_7),
                    colorResource(R.color.theme_cherry_dark_8),
                )
                ICE -> listOf(
                    colorResource(R.color.theme_ice_dark_0),
                    colorResource(R.color.theme_ice_dark_1),
                    colorResource(R.color.theme_ice_dark_2),
                    colorResource(R.color.theme_ice_dark_3),
                    colorResource(R.color.theme_ice_dark_4),
                    colorResource(R.color.theme_ice_dark_5),
                    colorResource(R.color.theme_ice_dark_6),
                    colorResource(R.color.theme_ice_dark_7),
                    colorResource(R.color.theme_ice_dark_8),
                )
                GRASS -> listOf(
                    colorResource(R.color.theme_grass_dark_0),
                    colorResource(R.color.theme_grass_dark_1),
                    colorResource(R.color.theme_grass_dark_2),
                    colorResource(R.color.theme_grass_dark_3),
                    colorResource(R.color.theme_grass_dark_4),
                    colorResource(R.color.theme_grass_dark_5),
                    colorResource(R.color.theme_grass_dark_6),
                    colorResource(R.color.theme_grass_dark_7),
                    colorResource(R.color.theme_grass_dark_8),
                )
            }[colorIndex.toInt() - 1]
        } else {
            when (this) {
                SNUTT -> listOf(
                    colorResource(R.color.theme_snutt_0),
                    colorResource(R.color.theme_snutt_1),
                    colorResource(R.color.theme_snutt_2),
                    colorResource(R.color.theme_snutt_3),
                    colorResource(R.color.theme_snutt_4),
                    colorResource(R.color.theme_snutt_5),
                    colorResource(R.color.theme_snutt_6),
                    colorResource(R.color.theme_snutt_7),
                    colorResource(R.color.theme_snutt_8),
                )
                MODERN -> listOf(
                    colorResource(R.color.theme_modern_0),
                    colorResource(R.color.theme_modern_1),
                    colorResource(R.color.theme_modern_2),
                    colorResource(R.color.theme_modern_3),
                    colorResource(R.color.theme_modern_4),
                    colorResource(R.color.theme_modern_5),
                    colorResource(R.color.theme_modern_6),
                    colorResource(R.color.theme_modern_7),
                    colorResource(R.color.theme_modern_8),
                )
                AUTUMN -> listOf(
                    colorResource(R.color.theme_autumn_0),
                    colorResource(R.color.theme_autumn_1),
                    colorResource(R.color.theme_autumn_2),
                    colorResource(R.color.theme_autumn_3),
                    colorResource(R.color.theme_autumn_4),
                    colorResource(R.color.theme_autumn_5),
                    colorResource(R.color.theme_autumn_6),
                    colorResource(R.color.theme_autumn_7),
                    colorResource(R.color.theme_autumn_8),
                )
                CHERRY -> listOf(
                    colorResource(R.color.theme_cherry_0),
                    colorResource(R.color.theme_cherry_1),
                    colorResource(R.color.theme_cherry_2),
                    colorResource(R.color.theme_cherry_3),
                    colorResource(R.color.theme_cherry_4),
                    colorResource(R.color.theme_cherry_5),
                    colorResource(R.color.theme_cherry_6),
                    colorResource(R.color.theme_cherry_7),
                    colorResource(R.color.theme_cherry_8),
                )
                ICE -> listOf(
                    colorResource(R.color.theme_ice_0),
                    colorResource(R.color.theme_ice_1),
                    colorResource(R.color.theme_ice_2),
                    colorResource(R.color.theme_ice_3),
                    colorResource(R.color.theme_ice_4),
                    colorResource(R.color.theme_ice_5),
                    colorResource(R.color.theme_ice_6),
                    colorResource(R.color.theme_ice_7),
                    colorResource(R.color.theme_ice_8),
                )
                GRASS -> listOf(
                    colorResource(R.color.theme_grass_0),
                    colorResource(R.color.theme_grass_1),
                    colorResource(R.color.theme_grass_2),
                    colorResource(R.color.theme_grass_3),
                    colorResource(R.color.theme_grass_4),
                    colorResource(R.color.theme_grass_5),
                    colorResource(R.color.theme_grass_6),
                    colorResource(R.color.theme_grass_7),
                    colorResource(R.color.theme_grass_8),
                )
            }[colorIndex.toInt() - 1]
        }
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
