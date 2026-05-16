package com.wafflestudio.snutt2.storage.model

import com.wafflestudio.snutt2.domain.model.ThemeMode

enum class ThemeModeLocalEntity {
    DARK,
    LIGHT,
    AUTO,
}

fun ThemeModeLocalEntity.toDomainModel(): ThemeMode = when (this) {
    ThemeModeLocalEntity.DARK -> ThemeMode.DARK
    ThemeModeLocalEntity.LIGHT -> ThemeMode.LIGHT
    ThemeModeLocalEntity.AUTO -> ThemeMode.AUTO
}

fun ThemeMode.toLocalEntity(): ThemeModeLocalEntity = when (this) {
    ThemeMode.DARK -> ThemeModeLocalEntity.DARK
    ThemeMode.LIGHT -> ThemeModeLocalEntity.LIGHT
    ThemeMode.AUTO -> ThemeModeLocalEntity.AUTO
}
