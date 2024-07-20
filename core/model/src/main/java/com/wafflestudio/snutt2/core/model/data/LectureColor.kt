package com.wafflestudio.snutt2.core.model.data

data class LectureColor(
    val lightForeGround: Int,
    val lightBackGround: Int,
    val darkForeGround: Int,
    val darkBackGround: Int,
) {
    constructor(
        fgString: String,
        bgString: String,
    ) : this(
        lightForeGround = parseColor(fgString),
        lightBackGround = parseColor(bgString),
        darkForeGround = parseColor(fgString),
        darkBackGround = parseColor(bgString),
    )

    constructor(
        fg: Int,
        bg: Int,
    ) : this(
        lightForeGround = fg,
        lightBackGround = bg,
        darkForeGround = fg,
        darkBackGround = bg,
    )
}

private fun parseColor(colorString: String): Int { // from android.graphics.Color
    if (colorString[0] == '#') {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            // Set the alpha value
            color = color or 0x00000000ff000000L
        } else {
            require(colorString.length == 9) { "Unknown color" }
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}
