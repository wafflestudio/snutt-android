package com.wafflestudio.snutt2.lib.network.dto.core

data class CustomThemeDto(
    val name: String = "",
    val colors: List<ColorDto> = listOf(ColorDto(fgColor = 0xffffff, bgColor = 0x1BD0C8)),
)
