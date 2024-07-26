package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.text.TextPaint

/**
 * 주어진 너비 내에 맞도록 텍스트를 여러 줄로 나눕니다.
 * 단어가 너무 길어서 maxWidth를 초과할 경우 단어를 쪼개서 줄 바꿈을 합니다.
 *
 * @param text 나눌 텍스트
 * @param paint 텍스트 측정을 위한 Paint 객체
 * @param maxWidth 각 줄의 최대 너비
 * @return 각 줄이 maxWidth 내에 맞도록 나눈 줄들의 리스트
 */
fun splitTextIntoLines(text: String, paint: TextPaint, maxWidth: Float): List<String> {
    return text.split(" ").fold(mutableListOf()) { lines, word ->
        val currentLine = if (lines.isNotEmpty()) lines.last() else ""
        val testLine = if (currentLine.isNotEmpty()) "$currentLine $word" else word

        if (paint.measureText(testLine) <= maxWidth) {
            if (lines.isNotEmpty()) {
                lines[lines.lastIndex] = testLine
            } else {
                lines.add(testLine)
            }
        } else {
            val splitWords = splitLongWord(word, paint, maxWidth)
            lines.addAll(splitWords)
        }
        lines
    }
}

/**
 * 단어가 너무 길어서 maxWidth를 초과하는 경우 단어를 쪼개어 줄을 나눕니다.
 * 단어가 길지 않은 경우, 리스트의 첫 원소에 그대로 넣어 반환합니다.
 *
 * @param word 나눌 단어
 * @param paint 텍스트 측정을 위한 Paint 객체
 * @param maxWidth 각 줄의 최대 너비
 * @return 각 줄이 maxWidth 내에 맞도록 나눈 단어의 리스트
 */
private fun splitLongWord(word: String, paint: TextPaint, maxWidth: Float): List<String> {
    return if (paint.measureText(word) <= maxWidth) {
        listOf(word)
    } else {
        val breakIndex = paint.breakText(word, true, maxWidth, null)
        listOf(word.substring(0, breakIndex)) + splitLongWord(word.substring(breakIndex), paint, maxWidth)
    }
}

