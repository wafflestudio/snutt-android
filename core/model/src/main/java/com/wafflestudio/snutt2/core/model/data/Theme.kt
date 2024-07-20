package com.wafflestudio.snutt2.core.model.data

data class Theme(
    val id: String,
    val name: String,
    val colors: List<LectureColor>,
    val isCustom: Boolean
) {
    object BuiltIn {
        val SNUTT = Theme(
            id = "SNUTT",
            name = "SNUTT",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xE54459,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xD95F71,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xF58D3D,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xDF6E3C,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xFAC42D,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xE68937,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xA6D930,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x95B03E,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x2BC267,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x419343,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x1BD0C8,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x5BA0D7,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x1D99E8,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x58C1B7,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x4F48C4,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x3E35A7,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xAF56B3,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x783891,
                ),
            ),
            isCustom = false,
        )
        val MODERN = Theme(
            id = "모던",
            name = "모던",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xF0652A,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xBB592F,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xF5AD3E,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xE08B45,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x998F36,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xB4B194,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x89C291,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x5B967C,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x266F55,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x266F55,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x13808F,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x13808F,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x366689,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x426586,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x432920,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x5C4335,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xD82F3D,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xAD2F31,
                ),
            ),
            isCustom = false,
        )
        val AUTUMN = Theme(
            id = "가을",
            name = "가을",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xB82E31,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xA93A36,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xDB701C,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xD56738,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xEAA32A,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xCC973F,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xC6C013,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xA0942F,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x3A856E,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x4E8370,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x19B2AC,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x29625A,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x3994CE,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x4171A2,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x3F3A9C,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x4F48C4,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x924396,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x783891,
                ),
            ),
            isCustom = false,
        )
        val CHERRY = Theme(
            id = "벚꽃",
            name = "벚꽃",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xFD79A8,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xA43C58,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xFEC9DD,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x7C164F,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xFEB0CC,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x99446E,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xFE93BF,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xA77085,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xE9B1D0,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xB290B8,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xC67D97,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xBDB4BF,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xBB8EA7,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xBB8EA7,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xBDB4BF,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x736C75,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xE16597,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xC76F92,
                ),
            ),
            isCustom = false,
        )
        val ICE = Theme(
            id = "얼음",
            name = "얼음",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xAABDCF,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x014D79,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xC0E9E8,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x788DA4,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x66B6CA,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xAEC1C9,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x015F95,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x48595B,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xA8D0DB,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x1C6C8E,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x458ED0,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x64909C,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x62A9D1,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x88B1C6,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x20363D,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x44576B,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x6D8A96,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x757C80,
                ),
            ),
            isCustom = false,
        )
        val GRASS = Theme(
            id = "잔디",
            name = "잔디",
            colors = listOf(
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x4FBEAA,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x2D5A45,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x9FC1A4,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x429587,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x5A8173,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x86A99A,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x84AEB1,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x597B6A,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x266F55,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x42635B,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0xD0E0C4,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x586C5D,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x59886D,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x324845,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x476060,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0xAAB6B1,
                ),
                LectureColor(
                    lightForeGround = 0xFFFFFF,
                    lightBackGround = 0x3D7068,
                    darkForeGround = 0xFFFFFF,
                    darkBackGround = 0x747877,
                ),
            ),
            isCustom = false,
        )

        fun fromCode(code: Int): Theme {
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

    companion object {
        val DefaultCustomTheme = Theme(
            id = "",
            name = "새 커스텀 테마",
            colors = listOf(LectureColor(0xffffff, 0x1bd0c8)),
            isCustom = true,
        )
    }
}