package com.wafflestudio.snutt2.model

import android.graphics.Color
import android.util.Log
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance

/**
 * Created by makesource on 2016. 8. 15..
 */
class Color {
    private var bg: String? = null
    private var fg: String? = null

    constructor() {}
    constructor(bgColor: Int, fgColor: Int) {
        this.bgColor = bgColor
        this.fgColor = fgColor
    }

    var fgColor: Int
        get() {
            if (fg == null) {
                Log.e("Color.java", "foreground color is null object!")
                return instance!!.defaultFgColor
            }
            return Color.parseColor(fg)
        }
        set(fgColor) {
            val fg = String.format("#%06X", 0xFFFFFF and fgColor)
            this.fg = fg
        }
    var bgColor: Int
        get() {
            if (bg == null) {
                Log.e("Color.java", "background color is null object!")
                return instance!!.defaultBgColor
            }
            return Color.parseColor(bg)
        }
        set(bgColor) {
            val bg = String.format("#%06X", 0xFFFFFF and bgColor)
            this.bg = bg
        }
}