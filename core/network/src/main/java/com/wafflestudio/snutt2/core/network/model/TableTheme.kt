package com.wafflestudio.snutt2.core.network.model

abstract class TableTheme(
    open val name: String,
)

data class CustomTheme(
    val id: String,
    override val name: String,
    val colors: List<ColorDto>,
) : TableTheme(name) {

    companion object {
        val Default = CustomTheme(
            id = "",
            name = "새 커스텀 테마",
            colors = listOf(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8)),
        )
    }
}

data class BuiltInTheme(
    val code: Int,
    override val name: String,
) : TableTheme(name) {

    companion object {
        val SNUTT = BuiltInTheme(
            code = 0,
            name = "SNUTT",
        )
        val MODERN = BuiltInTheme(
            code = 1,
            name = "모던",
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
        )
    }
}
