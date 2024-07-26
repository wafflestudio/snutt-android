package com.wafflestudio.snutt2.components.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect

class TextRect(private var paint: Paint) {
    private var metrics: FontMetricsInt = paint.fontMetricsInt
    private var lineheight = 0
    private var maxWidth = 0
    private var lines = 0
    private var maxlines = 0
    private val starts = mutableListOf<Int>()
    private val stops = mutableListOf<Int>()
    private var textHeights = mutableListOf<Int>()
    private val bounds = Rect()
    private var text: String = ""
    private var wasCut = false
    private var toDraw = true

    fun prepare(text: String, maxWidth: Int, toDraw: Boolean) {
        clear()
        this.toDraw = toDraw
        if (text.isEmpty()) this.toDraw = false
        if (!this.toDraw) return
        this.text = text
        this.maxWidth = maxWidth
        cutToLines()
        maxlines = lines
    }

    fun draw(canvas: Canvas, left: Int, top: Int, bubbleWidth: Int, fgColor: Int) {
        if (toDraw && maxlines > 0) {
            attachEllipsis()
            val before = -metrics.ascent
            val after = metrics.descent + metrics.leading
            var y = top
            for (n in 0 until maxlines) {
                y += before
                val t = if (wasCut && n == maxlines - 1) {
                    text.substring(starts[n], stops[n]) + "..."
                } else {
                    text.substring(starts[n], stops[n])
                }

                // 텍스트 가운데 정렬
                var leftResult = left
                paint.getTextBounds(t, 0, t.length, bounds)
                leftResult += (bubbleWidth - (bounds.right - bounds.left)) / 2

                //
                paint.color = fgColor
                canvas.drawText(t, leftResult.toFloat(), y.toFloat(), paint)
                y += after
            }
        }
    }

    fun getMaxLines() = maxlines

    fun getTextHeight(reduceLine: Boolean = false): Int {
        if (!toDraw || maxlines == 0) return 0 // 항목이 공백으로만 되어있는 예외적인 경우 처리
        if (reduceLine) {
            maxlines -= 1
            wasCut = true
        }
        return textHeights[maxlines - 1]
    }

    fun getLeading(): Int = metrics.leading

    private fun clear() {
        lines = 0
        maxlines = 0
        starts.clear()
        stops.clear()
    }

    private fun cutToLines() {
        var textToPrepare = text
        while (textToPrepare.isNotEmpty()) {
            val textToCut = cutToSingleLine(textToPrepare)
            if (textToCut.isEmpty()) break // 끝이 공백으로 끝나는 예외적인 경우 처리

            starts.add(textToPrepare.indexOf(textToCut) + (if (lines == 0) 0 else stops[lines - 1]))
            stops.add(starts[lines] + textToCut.length)
            textHeights.add((if (lines == 0) 0 else textHeights[lines - 1]) + lineheight)
            textToPrepare = text.substring(startIndex = stops[lines])
            lines += 1
        }
    }

    private fun cutToSingleLine(text: String): String {
        var result = ""
        for (char in text) {
            when {
                (char == '\n' && result.isNotEmpty()) -> break
                (char == '\n' && result.isEmpty()) -> continue
                (char == ' ' && result.isEmpty()) -> continue
            }

            result += char
            paint.getTextBounds(result, 0, result.length, bounds)
            if (bounds.width() > maxWidth) {
                while (result.lastIndexOf(' ') in 0 until result.length - 1) {
                    result = result.dropLast(1)
                }
                result = result.dropLast(1)
                break
            }
        }
        return result
    }

    private fun attachEllipsis() {
        if (wasCut) {
            while (true) {
                val lastLineText = text.substring(starts[maxlines - 1] until stops[maxlines - 1])
                paint.getTextBounds("$lastLineText...", 0, lastLineText.length + 3, bounds)
                if (bounds.width() <= maxWidth) break
                stops[maxlines - 1] -= 1
            }
        }
    }

    init {
        this.lineheight = -metrics.ascent + metrics.descent + metrics.leading
    }
}
