package com.wafflestudio.snutt2.components.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.util.Log

class TextRectTemp(paint: Paint) {
    private var metrics: FontMetricsInt? = null
    private var paint: Paint
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

    fun prepare(text: String, maxWidth: Int) {
        clear()
        this.text = text
        this.maxWidth = maxWidth
        cutToLines()
        maxlines = lines
    }

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
            if (textToCut.isEmpty()) break // 끝이 공백으로 끝나는 예외적인 경우 처

            starts.add(textToPrepare.indexOf(textToCut))
            stops.add(starts[lines] + textToCut.length)
            textHeights[lines] = (if (lines==0) 0 else textHeights[lines-1]) + lineheight
            textToPrepare = textToPrepare.substring(stops[lines])
            lines+=1
        }
    }

    private fun cutToSingleLine(text: String): String {
        var result = ""
        for (i in 0..<text.length) {
            val char = text[i]
            if (char == '\n' && result.isNotEmpty()) return result
            if (char == ' ' && result.isEmpty()) continue

            result += char
            paint.getTextBounds(result, 0, result.length, bounds)
            if (bounds.width() > maxWidth) {
                while (result.lastIndexOf(' ') in 0..<result.length-1) {
                    result = result.dropLast(1)
                }
                result = result.dropLast(1)
                break
            }
        }
        return result
    }

    fun getLines() = lines

    fun getTextHeight(reduceLine: Boolean = false): Int {
        if (reduceLine) maxlines-=1
        return textHeights[maxlines-1]
    }

    init {
        metrics = paint.fontMetricsInt
        this.paint = paint
        this.lineheight = -metrics!!.ascent + metrics!!.descent + metrics!!.leading
    }
}

fun main() {
    val a = "초급한문 1"
    println(a.indexOf("초급한문"))
    println(a.substring(0..<3))
}
