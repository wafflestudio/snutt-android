package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.logged_in.home.timetable.DrawTableGrid
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableCanvasObjects
import kotlinx.coroutines.sync.Semaphore

@Composable
fun TimeSelectSheet(
    basedAnimatedFloat: State<Float>,
    initialDraggedTimeBlock: State<List<List<Boolean>>>,
    onCancel: () -> Unit,
    onConfirm: (List<List<Boolean>>) -> Unit,
) {
    val alphaAnimatedFloat by remember {
        derivedStateOf { basedAnimatedFloat.value }
    }
    val draggedTimeBlock = remember {
        initialDraggedTimeBlock.value.map { it.map { mutableStateOf(it) } }
    }

    Column(
        modifier = Modifier
            .alpha(alphaAnimatedFloat)
            .padding(bottom = 20.dp)
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(200f)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "취소",
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    draggedTimeBlock.forEachIndexed { dayIndex, dayColumn ->
                        dayColumn.forEachIndexed { timeIndex, value ->
                            draggedTimeBlock[dayIndex][timeIndex].value = initialDraggedTimeBlock.value[dayIndex][timeIndex]
                        }
                    }
                    onCancel()
                },
            )
            Text(
                text = "완료", style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(draggedTimeBlock.map { it.map { it.value } })
                },
            )
        }
        Box {
            TimeTableDragSheet(draggedTimeBlock)
        }
    }
}

@Composable
fun TimeTableDragSheet(draggedTimeBlock: List<List<MutableState<Boolean>>>) {
    DrawTableGrid(TableTrimParam.SearchOption)
    for (day in 0..4) {
        for (time in 0..27) {
            if (draggedTimeBlock[day][time].value) {
                DrawTimeBlock(day = day, (16 + time) * 30)
            }
        }
    }
    DrawDragEventCanvas(
        isSelected = { day, time ->
            draggedTimeBlock[day][(time * 2).toInt() - 16].value
        },
        select = { day, time ->
            draggedTimeBlock[day][(time * 2).toInt() - 16].value = true
        },
        unSelect = { day, time ->
            draggedTimeBlock[day][(time * 2).toInt() - 16].value = false
        },
    )
}

@Composable
private fun DrawTimeBlock(
    day: Int,
    startMinute: Int,
    bgColor: Int = Color.parseColor("#1bd0c8"),
) {
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val hourRangeOffset = Pair(startMinute / 60f - 8f, (startMinute + 30f) / 60f - 8f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (size.width - hourLabelWidth) / 5
        val unitHeight =
            (size.height - dayLabelHeight) / 14

        val left = hourLabelWidth + day * unitWidth
        val right = hourLabelWidth + day * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom = dayLabelHeight + (hourRangeOffset.second) * unitHeight

        val rect = RectF(left, top, right, bottom)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRect(rect, Paint().apply { color = bgColor })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DrawDragEventCanvas(
    isSelected: (day: Int, time: Float) -> Boolean,
    select: (day: Int, time: Float) -> Unit,
    unSelect: (day: Int, time: Float) -> Unit,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val fittedTrimParam = TableTrimParam.SearchOption

    var touchedBefore by remember {
        mutableStateOf<Pair<Int, Int>?>(synchronized(Semaphore(permits = 1)) { null })
    }

    val unitWidth by remember {
        derivedStateOf {
            (canvasSize.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        }
    }
    val unitHeight by remember {
        derivedStateOf {
            (canvasSize.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                // FIXME: 로직 정리하기
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    val day =
                        (((event.x - hourLabelWidth) / unitWidth).toInt() + fittedTrimParam.dayOfWeekFrom)
                    val time =
                        (((event.y - dayLabelHeight) / unitHeight) + fittedTrimParam.hourFrom)
                    if (day < fittedTrimParam.dayOfWeekFrom || day > fittedTrimParam.dayOfWeekTo) return@pointerInteropFilter false
                    if (time < fittedTrimParam.hourFrom || time > fittedTrimParam.hourTo + 1) return@pointerInteropFilter false

                    if (touchedBefore?.first != day || touchedBefore?.second != (time * 2).toInt()) {
                        touchedBefore = null
                    }

                    if (event.action == MotionEvent.ACTION_MOVE) {
                        if (touchedBefore != null) return@pointerInteropFilter true
                    }

                    touchedBefore = day to (time * 2).toInt()
                    if (isSelected(day, time)) {
                        unSelect(day, time)
                    } else {
                        select(day, time)
                    }
                }
                true
            },
    ) {
        canvasSize = size
    }
}
