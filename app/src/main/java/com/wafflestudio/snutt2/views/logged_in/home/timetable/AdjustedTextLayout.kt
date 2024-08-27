package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Constraints

data class AdjustedTextLayout(
    val text: String,
    val style: TextStyle,
    val maxLines: Int,
)

fun calculateAdjustedTextLayout(
    input: List<LectureCellInfo>,
    textMeasurer: TextMeasurer,
    constraints: Constraints,
): List<AdjustedTextLayout> {
    val maxHeight = constraints.maxHeight
    val maxWidth = constraints.maxWidth
    val cellInfoList = input.filter { it.enabled && it.text.isNotEmpty() && it.text.isNotBlank() }

    // 모든 정보를 다 그릴 수 있을 때
    val fullRenderParagraph = buildAnnotatedString {
        cellInfoList.forEach {
            val idealMaxLines = textMeasurer.measure(
                text = it.text,
                style = it.style,
                constraints = Constraints(maxWidth = maxWidth),
            ).lineCount

            pushStyle(it.style.toSpanStyle())
            repeat(idealMaxLines) { appendLine() }
        }
    }
    if (textMeasurer.measure(fullRenderParagraph).multiParagraph.height <= maxHeight) {
        return cellInfoList.map {
            AdjustedTextLayout(
                it.text, it.style, Int.MAX_VALUE,
            )
        }
    }

    // 폰트를 줄이면 모든 정보를 다 그릴 수 있을 때
    val minifiedRenderParagraph = buildAnnotatedString {
        cellInfoList.forEach {
            val idealMaxLines = textMeasurer.measure(
                text = it.text,
                style = it.minifiedStyle,
                constraints = Constraints(maxWidth = maxWidth),
            ).lineCount

            pushStyle(it.style.toSpanStyle())
            repeat(idealMaxLines) { appendLine() }
        }
    }
    if (textMeasurer.measure(minifiedRenderParagraph).multiParagraph.height <= maxHeight) {
        return cellInfoList.map {
            AdjustedTextLayout(
                it.text, it.minifiedStyle, Int.MAX_VALUE,
            )
        }
    }

    // 한 줄로 다 줄여도 다 그릴 수 없을 때는 그릴 수 있는 만큼 한 줄로 그린다.
    val allOneLineParagraph = buildAnnotatedString {
        cellInfoList.forEach {
            pushStyle(it.minifiedStyle.toSpanStyle())
            appendLine()
        }
    }
    if (textMeasurer.measure(allOneLineParagraph).multiParagraph.height > maxHeight) {
        return cellInfoList.fold(emptyList<LectureCellInfo>()) { acc, current ->
            val paragraph = buildAnnotatedString {
                acc.forEach {
                    pushStyle(it.minifiedStyle.toSpanStyle())
                    appendLine()
                }
            }
            if (textMeasurer.measure(paragraph).multiParagraph.height < maxHeight) {
                acc + current
            } else {
                acc
            }
        }.map {
            AdjustedTextLayout(
                it.text, it.minifiedStyle, 1,
            )
        }
    }

    // 각 정보보다 하위 정보를 모두 한 줄로만 렌더링 했을 때 그릴 수 있는 최대 줄 수를 하나씩 계산한다.
    val eachInfoCalculatedMaxLines = mutableListOf<Int>()
    cellInfoList.forEachIndexed { idx, currentInfo ->
        // 높이 제약이 없을 때의 최대 라인 수
        val idealMaxLine = textMeasurer.measure(
            currentInfo.text,
            currentInfo.style,
            constraints = Constraints(maxWidth = constraints.maxWidth),
        ).lineCount

        var currentMaxLines = 0
        while (
            textMeasurer.measure(
                buildAnnotatedString {
                    // 상위 정보는 이전에 계산한 최대 줄 수로 그린다
                    cellInfoList.zip(eachInfoCalculatedMaxLines).forEach { (upperInfo, maxLines) ->
                        pushStyle(upperInfo.minifiedStyle.toSpanStyle())
                        repeat(maxLines) { appendLine() }
                    }

                    // 현재 정보를 ${currentMaxLines + 1} 줄로 그려서, 최대 높이를 초과 하는지 테스트
                    pushStyle(currentInfo.minifiedStyle.toSpanStyle())
                    repeat(currentMaxLines + 1) { appendLine() }

                    // 하위 정보는 모두 한 줄로 그리기
                    cellInfoList.subList(idx + 1, cellInfoList.size).forEach {
                        pushStyle(it.minifiedStyle.toSpanStyle())
                        appendLine()
                    }
                },
            ).multiParagraph.height <= maxHeight
        ) {
            currentMaxLines++
            // 높이 제약이 없을 때의 라인 수에 도달하면, 더 라인 수를 늘려서 계산해 볼 필요가 없으니 break
            if (currentMaxLines >= idealMaxLine) {
                break
            }
        }
        eachInfoCalculatedMaxLines.add(currentMaxLines)
    }
    return cellInfoList.zip(eachInfoCalculatedMaxLines).map { (info, maxLines) ->
        AdjustedTextLayout(
            text = info.text,
            style = info.minifiedStyle,
            maxLines = maxLines,
        )
    }
}
