package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.text.TextPaint

data class LineInfo(val text: String, val baseline: Float, val startX: Float)

/**
 * 텍스트 블록을 수직으로 중앙 정렬하기 위한 시작 Y 좌표를 계산합니다.
 *
 * @param totalHeight 텍스트 블록의 전체 높이
 * @param availableHeight 그릴 수 있는 영역의 높이
 * @return 첫 줄의 시작 Y 좌표
 */
fun calculateStartY(totalHeight: Int, availableHeight: Int): Float {
    return (availableHeight - totalHeight) / 2f
}

/**
 * 각 줄의 텍스트와 베이스라인 위치를 계산합니다.
 *
 * @param lines 줄과 페인트 쌍들의 리스트
 * @param availableWidth 그릴 수 있는 영역의 너비
 * @param initialY 첫 줄의 시작 Y 좌표
 * @return LineInfo 리스트
 */
fun calculateLineInfo(
    lines: List<Pair<String, TextPaint>>,
    availableWidth: Float,
    initialY: Float,
): List<LineInfo> {
    val result = mutableListOf<LineInfo>()
    lines.fold(initialY) { startY, (text, paint) ->
        val lineWidth = paint.measureText(text)
        val startX = (availableWidth - lineWidth) / 2f

        result.add(LineInfo(text, startY - paint.fontMetricsInt.top, startX))

        startY + getSingleLineHeight(paint) + paint.fontMetricsInt.leading
    }

    return result
}
