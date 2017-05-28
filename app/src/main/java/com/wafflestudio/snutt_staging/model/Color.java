package com.wafflestudio.snutt_staging.model;

import android.util.Log;

import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.LectureManager;

/**
 * Created by makesource on 2016. 8. 15..
 */
public class Color {
    private String bg;
    private String fg;

    public Color() {

    }

    public Color(int bgColor, int fgColor) {
        setBg(bgColor);
        setFg(fgColor);
    }

    public int getFg() {
        if (fg == null) {
            Log.e("Color.java", "foreground color is null object!");
            return LectureManager.getInstance().getDefaultFgColor();
        }
        int fgColor = android.graphics.Color.parseColor(fg);
        return fgColor;
    }

    public void setFg(int fgColor) {
        String fg = String.format("#%06X", (0xFFFFFF & fgColor));
        this.fg = fg;
    }

    public int getBg() {
        if (bg == null) {
            Log.e("Color.java", "background color is null object!");
            return LectureManager.getInstance().getDefaultBgColor();
        }
        int bgColor = android.graphics.Color.parseColor(bg);
        return bgColor;
    }

    public void setBg(int bgColor) {
        String bg = String.format("#%06X", (0xFFFFFF & bgColor));
        this.bg = bg;
    }
}
