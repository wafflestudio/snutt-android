package com.wafflestudio.snutt2.components.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect

class TextRect(private var paint: Paint) {
    private var metrics: FontMetricsInt = paint.fontMetricsInt
    private var singleLineHeight = 0
    private var cellWidth = 0
    private var lines = 0
    private var availableLines = 0
    private val starts = mutableListOf<Int>()
    private val stops = mutableListOf<Int>()
    private var textHeights = mutableListOf<Int>()
    private val bounds = Rect()
    private var text: String = ""
    private var wasCut = false
    private var toDraw = true

    fun prepare(text: String, cellWidth: Int, toDraw: Boolean) {
        clear()
        this.toDraw = toDraw
        if (text.isEmpty()) this.toDraw = false
        if (!this.toDraw) return
        this.text = text
        this.cellWidth = cellWidth
        cutToLines()
        availableLines = lines
    }

    fun draw(canvas: Canvas, left: Int, top: Int, fgColor: Int) {
        if (toDraw && availableLines > 0) {
            attachEllipsis()
            val before = -metrics.ascent
            val after = metrics.descent + metrics.leading
            var y = top
            for (n in 0 until availableLines) {
                y += before
                val textWithEllipsis = if (wasCut && n == availableLines - 1) {
                    text.substring(starts[n], stops[n]) + "..."
                } else {
                    text.substring(starts[n], stops[n])
                }

                // 텍스트 가운데 정렬
                var leftResult = left
                paint.getTextBounds(textWithEllipsis, 0, textWithEllipsis.length, bounds)
                leftResult += (cellWidth - (bounds.right - bounds.left)) / 2

                //
                paint.color = fgColor
                canvas.drawText(textWithEllipsis, leftResult.toFloat(), y.toFloat(), paint)
                y += after
            }
        }
    }

    fun getAvailableLines() = availableLines

    fun getTextHeight(reduceLine: Boolean = false): Int {
        if (!toDraw || availableLines == 0) return 0 // 항목이 공백으로만 되어있는 예외적인 경우 처리
        if (reduceLine) {
            availableLines -= 1
            wasCut = true
        }
        return textHeights[availableLines - 1]
    }

    fun getLeading(): Int = metrics.leading

    private fun clear() {
        lines = 0
        availableLines = 0
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
            textHeights.add((if (lines == 0) 0 else textHeights[lines - 1]) + singleLineHeight)
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
            if (bounds.width() > cellWidth) {
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
                val lastLineText = text.substring(starts[availableLines - 1] until stops[availableLines - 1])
                paint.getTextBounds("$lastLineText...", 0, lastLineText.length + 3, bounds)
                if (bounds.width() <= cellWidth) break
                stops[availableLines - 1] -= 1
            }
        }
    }

    init {
        this.singleLineHeight = -metrics.ascent + metrics.descent + metrics.leading
    }
}
