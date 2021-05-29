package com.wafflestudio.snutt2

object ColorConst {
    val DEFAULT_FG = intArrayOf(-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0xcccccd)
    val DEFAULT_BG = intArrayOf(-0x1abba7, -0xa72c3, -0x53ad3, -0x5926d0, -0xd43c9a, -0xe42f37, -0xe26617, -0xb0b73c, -0x50a94d, -0x1f1f20)
    private val DEFAULT_NAME = arrayOf(
        "석류",
        "감귤",
        "들국",
        "완두",
        "비취",
        "지중해",
        "하늘",
        "라벤더",
        "자수정",
        "직접 지정하기"
    )

    val defaultBgColor: Int
        get() = DEFAULT_BG[DEFAULT_BG.size - 1]
    val defaultFgColor: Int
        get() = DEFAULT_FG[DEFAULT_FG.size - 1]
    val defaultColorName: String
        get() = DEFAULT_NAME[DEFAULT_NAME.size - 1]
}
