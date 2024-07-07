package com.wafflestudio.snutt2.core.model.data.theme

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

        fun fromCode(code: Int): BuiltInTheme {
            return when (code) {
                0 -> SNUTT
                1 -> MODERN
                2 -> AUTUMN
                3 -> CHERRY
                4 -> ICE
                5 -> GRASS
                else -> SNUTT
            }
        }
    }
}
