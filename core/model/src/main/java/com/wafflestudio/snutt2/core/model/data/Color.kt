package com.wafflestudio.snutt2.core.model.data

data class LectureColor(
    val foreground: Int,
    val background: Int,
) {
    constructor(
        foregroundString: String,
        backgroundString: String,
    ) : this(
        parseColor(foregroundString),
        parseColor(backgroundString)
    )
}

private fun parseColor(colorString: String): Int {  // from android.graphics.Color
    if (colorString[0] == '#') {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            // Set the alpha value
            color = color or 0x00000000ff000000L
        } else require(colorString.length == 9) { "Unknown color" }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}