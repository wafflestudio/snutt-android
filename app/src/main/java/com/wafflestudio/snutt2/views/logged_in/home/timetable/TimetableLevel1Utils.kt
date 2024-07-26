package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.content.Context
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.roundToCompact
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.model.TableTrimParam
import kotlin.math.max
import kotlin.math.min

typealias RectCalculator = (Size, Context) -> RectF

fun rectCalculator(
    fittedTrimParam: TableTrimParam,
    classTime: ClassTimeDto,
    isCustom: Boolean = false,
    compactMode: Boolean,
): RectCalculator {
    return { size, context ->
        val hourLabelWidth = 24.5f.dp(context)
        val dayLabelHeight = 28.5f.dp(context)

        val dayOffset = classTime.day - fittedTrimParam.dayOfWeekFrom
        val hourRangeOffset =
            Pair(
                max(classTime.startTimeInFloat - fittedTrimParam.hourFrom, 0f),
                min(
                    classTime.endTimeInFloat.let { if (isCustom.not() && compactMode) roundToCompact(it) else it } - fittedTrimParam.hourFrom,
                    fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1,
                ),
            )

        val unitWidth =
            (size.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val left = hourLabelWidth + (dayOffset) * unitWidth
        val right = hourLabelWidth + (dayOffset) * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom = dayLabelHeight + (hourRangeOffset.second) * unitHeight

        RectF(left, top, right, bottom)
    }
}
