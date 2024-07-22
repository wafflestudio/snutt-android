package com.wafflestudio.snutt2.components.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect

/**
 * Created by makesource on 2016. 1. 24..
 */
class TextRectForWidget(paint: Paint) {
    // those members are stored per instance to minimize
    // the number of allocations to avoid triggering the
    // GC too much
    private var metrics: FontMetricsInt? = null
    private var paint: Paint? = null
    private val starts = IntArray(MAX_LINES)
    private val stops = IntArray(MAX_LINES)
    private var lines = 0
    private var textHeight = 0
    private val bounds = Rect()
    private var text: String? = null
    private var wasCut = false

    /**
     * Calculate height of text block and prepare to draw it.
     *
     * @param text - text to draw
     * @param maxWidth - maximum width in pixels
     * @param maxHeight - maximum height in pixels
     * @returns height of text in pixels
     */
    fun prepare(text: String, maxWidth: Int, maxHeight: Int): Int {
        lines = 0
        textHeight = 0
        this.text = text
        wasCut = false

        // get maximum number of characters in one line
        paint!!.getTextBounds("i", 0, 1, bounds)
        val maximumInLine = maxWidth / bounds.width()
        val length = text.length
        if (length > 0) {
            val lineHeight = -metrics!!.ascent + metrics!!.descent + metrics!!.leading
            var start = 0
            var stop = if (maximumInLine > length) length else maximumInLine
            // stop = min(length,maximuminline);
            while (true) {
                // skip LF and spaces
                while (start < length) {
                    val ch = text[start]
                    if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ') break
                    ++start
                }
                var o = stop + 1
                while (stop < o && stop > start) {
                    o = stop
                    var lowest = text.indexOf("\n", start)
                    // 강의명만 생각
                    paint!!.getTextBounds(text, start, stop, bounds)
                    if (start <= lowest && lowest < stop || bounds.width() > maxWidth) // start ~ stop 까지 한 줄에 넣으려고 하는데 한 줄에 넘치면
                        {
                            --stop
                            if (lowest < start || lowest > stop) {
                                val blank = text.lastIndexOf(" ", stop)
                                if (blank > start) lowest = blank
                            }
                            if (start <= lowest && lowest <= stop) {
                                val ch = text[stop]
                                if (ch != '\n' && ch != ' ') ++lowest
                                stop = lowest
                            }
                            continue
                        }
                    break
                }
                if (start >= stop) break
                var minus = 0

                // cut off lf or space
                if (stop < length) {
                    val ch = text[stop - 1]
                    if (ch == '\n' ||
                        ch == ' '
                    ) {
                        minus = 1
                    }
                }
                starts[lines] = start
                stops[lines] = stop - minus
                if (++lines > MAX_LINES) {
                    wasCut = true
                    break
                }
                if (textHeight + lineHeight > maxHeight) {
                    wasCut = true
                    break
                }
                textHeight += lineHeight
                if (stop >= length) break
                start = stop
                stop = length
            }
        }
        return textHeight
    }

    /**
     * Calculate height of text block and prepare to draw it in a single line.
     *
     * @param text - text to draw
     * @param maxWidth - maximum width in pixels
     * @param maxHeight - maximum height in pixels
     * @returns height of text in pixels
     */
    fun prepareSingleLine(text: String, maxWidth: Int, maxHeight: Int): Int {
        paint!!.getTextBounds(text, 0, text.length, bounds)
        if (bounds.width() == 0) return 0
        if (bounds.width() <= maxWidth) {
            val lineHeight = -metrics!!.ascent + metrics!!.descent + metrics!!.leading
            if (lineHeight > maxHeight) {
                wasCut = true
                return 0
            }
            wasCut = false
            lines = 1
            starts[0] = 0
            stops[0] = text.length
            textHeight = lineHeight
            return textHeight
        }

        lines = 0
        textHeight = 0
        this.text = text
        wasCut = true

        // get maximum number of characters in one line
        paint!!.getTextBounds("i", 0, 1, bounds)
        val maximumInLine = maxWidth / bounds.width()
        val length = text.length
        if (length > 0) {
            val lineHeight = -metrics!!.ascent + metrics!!.descent + metrics!!.leading
            var start = 0
            var stop = if (maximumInLine > length) length else maximumInLine
            // stop = min(length,maximuminline);
            while (true) {
                // skip LF and spaces
                while (start < length) {
                    val ch = text[start]
                    if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ') break
                    ++start
                }
                var o = stop + 1
                while (stop < o && stop > start) {
                    o = stop
                    var lowest = text.indexOf("\n", start)
                    // 강의명만 생각
                    paint!!.getTextBounds("$text...", start, stop + 3, bounds)
                    if (start <= lowest && lowest < stop || bounds.width() > maxWidth) // start ~ stop 까지 한 줄에 넣으려고 하는데 한 줄에 넘치면
                        {
                            --stop
                            if (lowest < start || lowest > stop) {
                                val blank = text.lastIndexOf(" ", stop)
                                if (blank > start) lowest = blank
                            }
                            if (start <= lowest && lowest <= stop) {
                                val ch = text[stop]
                                if (ch != '\n' && ch != ' ') ++lowest
                                stop = lowest
                            }
                            continue
                        }
                    break
                }
                if (start >= stop) break
                var minus = 0

                // cut off lf or space
                if (stop < length) {
                    val ch = text[stop - 1]
                    if (ch == '\n' ||
                        ch == ' '
                    ) {
                        minus = 1
                    }
                }
                if (textHeight + lineHeight > maxHeight) {
                    wasCut = true
                    break
                }
                starts[lines] = start
                stops[lines] = stop - minus + 3
                if (++lines > MAX_LINES) {
                    wasCut = true
                    break
                }
                textHeight += lineHeight
                if (stop >= length) break
                break
            }
        }
        return textHeight
    }

    /**
     * Draw prepared text at given position.
     *
     * @param canvas - canvas to draw text into
     * @param left - left corner
     * @param top - top corner
     */
    fun draw(canvas: Canvas, left: Int, top: Int, bubbleWidth: Int, fgColor: Int) {
        if (textHeight == 0) return
        val before = -metrics!!.ascent
        val after = metrics!!.descent + metrics!!.leading
        var y = top
        --lines
        for (n in 0..lines) {
            var t: String
            y += before
            t = if (wasCut && n == lines) {
                text!!.substring(
                    starts[n],
                    if (stops[n] - 3 < starts[n]) starts[n] else stops[n] - 3,
                ) + "..."
            } else {
                text!!.substring(starts[n], stops[n])
            }

            // 텍스트 가운데 정렬
            var leftResult = left
            paint!!.getTextBounds(t, 0, t.length, bounds)
            val textWidth = bounds.right - bounds.left
            leftResult += (bubbleWidth - textWidth) / 2
            //
            paint!!.color = fgColor
            canvas.drawText(t, leftResult.toFloat(), y.toFloat(), paint!!)
            y += after
        }
    }

    /** Returns true if text was cut to fit into the maximum height  */
    fun wasCut(): Boolean {
        return wasCut
    }

    fun getLeading(): Int {
        return metrics?.leading ?: 0
    }

    companion object {
        // maximum number of lines; this is a fixed number in order
        // to use a predefined array to avoid ArrayList (or something
        // similar) because filling it does involve allocating memory
        private const val MAX_LINES = 256
    }

    /**
     * Create reusable text rectangle (use one instance per font).
     *
     * @param paint - paint specifying the font
     */
    init {
        metrics = paint.fontMetricsInt
        val temp = metrics.toString()
        println("metrics$temp")
        this.paint = paint
    }
}
