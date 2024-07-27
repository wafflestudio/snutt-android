package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.content.Context
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import com.wafflestudio.snutt2.lib.rx.dp

data class Level2Output(
    val textToDraw: String,
    val left: Float,
    val top: Float,
)

data class Info(
    val text: String,
    val font: TextPaint,
    val reducedPaint: TextPaint,
    val enabled: Boolean = true,
)

data class ReturnInfo (
    val text: String,
    val font: TextPaint,
)


fun level2_1(
    context: Context,
    lectureInfo: Info,
    placeInfo: Info,
    instructorInfo: Info,
    lectureNumberInfo: Info,
    rect: RectF,
): List<ReturnInfo> {
    val cellPadding = 4.dp(context)
    val cellHeight = rect.bottom - rect.top - cellPadding * 2
    val cellWidth = rect.right - rect.left - cellPadding * 2

    // TextRect 정책 시작

    // 강의명이 on일 때
    if (lectureInfo.enabled) {
        val lectureLines = LineBreakPolicy.splitTextIntoLines(lectureInfo.text, lectureInfo.font, cellWidth)
        val placeLines = LineBreakPolicy.splitTextIntoLines(placeInfo.text, placeInfo.font, cellWidth)
        val lectureNumberLines = LineBreakPolicy.splitTextIntoLines(lectureNumberInfo.text, lectureNumberInfo.font, cellWidth)
        val instructorLines = LineBreakPolicy.splitTextIntoLines(instructorInfo.text, instructorInfo.font, cellWidth)


        // 모든 옵션을 다 그릴 수 있을 때
        if (calculateTotalHeight2(listOf(lectureInfo, placeInfo, instructorInfo, lectureNumberInfo)) <= cellHeight) {
            return buildList {
                if (lectureInfo.enabled) {
                    addAll(lectureLines.map { ReturnInfo(it, lectureInfo.font) })
                }
                if (placeInfo.enabled) {
                    addAll(lectureLines.map { ReturnInfo(it, placeInfo.font) })
                }
                if (lectureNumberInfo.enabled) {
                    addAll(lectureLines.map { ReturnInfo(it, lectureNumberInfo.font) })
                }
                if (instructorInfo.enabled) {
                    addAll(lectureLines.map { ReturnInfo(it, instructorInfo.font) })
                }
            }
        }


        // 강의명을 제외한 옵션의 폰트를 줄이면
        val placeLinesReduced = LineBreakPolicy.splitTextIntoLines(placeName, placeReducedTextPaint, cellWidth)
        val placeLinesHeightReduced = calculateTotalHeight(placeLines.size, placeReducedTextPaint)

        val lectureNumberLinesReduced = LineBreakPolicy.splitTextIntoLines(lectureNumber, lectureNumberReducedTextPaint, cellWidth)
        val lectureNumberLinesHeightReduced = calculateTotalHeight(lectureNumberLines.size, lectureNumberReducedTextPaint)

        val instructorLinesReduced = LineBreakPolicy.splitTextIntoLines(instructor, instructorReducedTextPaint, cellWidth)
        val instructorLinesHeightReduced = calculateTotalHeight(instructorLines.size, instructorReducedTextPaint)

        // 강의명을 제외한 옵션의 폰트를 줄이면 모든 옵션을 다 그릴 수 있을 때
        if (lectureLinesHeight + placeLinesHeightReduced + lectureNumberLinesHeightReduced + instructorLinesHeightReduced <= cellHeight) {
            // return ~~
        }

        // 모든 옵션을 한 줄로 줄이면
        val lectureFirstLine = lectureLines[0]
        val lectureFirstLineHeight = calculateTotalHeight(1, lectureTextPaint)

        val placeFirstLine = placeLines[0]
        val placeFirstLineHeight = calculateTotalHeight(1, placeReducedTextPaint)

        val lectureNumberFirstLine = lectureNumberLines[0]
        val lectureNumberFirstLineHeight = calculateTotalHeight(1, lectureNumberReducedTextPaint)

        val instructorFirstLine = instructorLines[0]
        val instructorFirstLineHeight = calculateTotalHeight(1, instructorReducedTextPaint)

        // 모든 옵션을 한 줄로 줄여도 다 그릴 수 없을 때
        if (lectureFirstLineHeight + placeFirstLineHeight + lectureNumberFirstLineHeight + instructorFirstLineHeight > cellHeight) {
            // 그릴 수 있는 옵션들만 한 줄로 쩜쩜쩜 로직으로 그린다.
            // return ~~
        }
    }
}

fun getSingleLineHeight(textPaint: TextPaint): Float {
    val fontMetricsInt = textPaint.fontMetricsInt
    return (fontMetricsInt.bottom - fontMetricsInt.top).toFloat()
}

private fun createTextPaint(
    textSize: Float,
    typeface: Typeface?,
): TextPaint = TextPaint().apply {
    setTypeface(typeface)
    setTextSize(textSize)
}

private fun calculateTotalHeight(lineNumber: Int, textPaint: TextPaint): Float {
    val lineHeight = getSingleLineHeight(textPaint)
    val leading = textPaint.fontMetricsInt.leading
    return lineNumber * lineHeight + (lineNumber - 1) * leading
}

/**
 *
 *
 *
 * @param fonts 각 줄의 폰트
 * @return 줄이지 않은 폰트로 leading을 포함해 렌더링 될 전체 높이
 */
private fun calculateTotalHeight2(
    fonts: List<Info>,
): Float =
    fonts.filter { it.enabled }.dropLast(1).sumOf { it.font.fontMetricsInt.bottom.toDouble() - it.font.fontMetricsInt.top.toDouble() + it.font.fontMetricsInt.leading }.toFloat() +
        (fonts.lastOrNull()?.let { it.font.fontMetricsInt.bottom.toDouble() - it.font.fontMetricsInt.top.toDouble() }?.toFloat() ?: 0F)

