package com.wafflestudio.snutt2.lib

fun roundToCompact(f: Float): Float = if (f - f.toInt() == 0f) {
    f
} else if (f - f.toInt() <= 0.5) {
    f.toInt() + 0.5f
} else {
    f.toInt() + 1f
}
