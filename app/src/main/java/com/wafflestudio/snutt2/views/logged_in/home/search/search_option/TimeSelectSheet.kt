package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.logged_in.home.timetable.DrawTableGrid
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableCanvasObjects

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
                            draggedTimeBlock[dayIndex][timeIndex].value =
                                initialDraggedTimeBlock.value[dayIndex][timeIndex]
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
        isSelected = { dayIndex, timeIndex ->
            draggedTimeBlock[dayIndex][timeIndex].value
        },
        select = { dayIndex, timeIndex ->
            draggedTimeBlock[dayIndex][timeIndex].value = true
        },
        unSelect = { dayIndex, timeIndex ->
            draggedTimeBlock[dayIndex][timeIndex].value = false
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

@Composable
private fun DrawDragEventCanvas(
    isSelected: (dayIndex: Int, timeIndex: Int) -> Boolean,
    select: (dayIndex: Int, timeIndex: Int) -> Unit,
    unSelect: (dayIndex: Int, timeIndex: Int) -> Unit,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val fittedTrimParam = TableTrimParam.SearchOption

    val unitWidth by remember {
        derivedStateOf {
            (canvasSize.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        }
    }
    val unitHeight by remember {
        derivedStateOf {
            (canvasSize.height - dayLabelHeight) / ((fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1) * 2) // 30분 칸의 높이
        }
    }

    var touchedDayIndex: Int? = remember { null }
    var touchedTimeIndex: Int? = remember { null } // 8시 ~ 8시30분 칸 : 0, 8시 30분 ~ 9시 칸 : 1, ...
    var eraseMode: Boolean = remember { false } // true면 지우는 모드, false면 칠하는 모드

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val dayIndex = ((offset.x - hourLabelWidth) / unitWidth).toInt()
                        val timeIndex = ((offset.y - dayLabelHeight) / unitHeight).toInt()

                        if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                            return@detectDragGestures
                        }

                        eraseMode = isSelected(dayIndex, timeIndex)
                        if (eraseMode) unSelect(dayIndex, timeIndex)
                        else select(dayIndex, timeIndex)

                        touchedTimeIndex = timeIndex
                        touchedDayIndex = dayIndex
                    },
                    onDrag = { change, _ ->
                        val dayIndex = ((change.position.x - hourLabelWidth) / unitWidth).toInt()
                        val timeIndex = ((change.position.y - dayLabelHeight) / unitHeight).toInt()

                        if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                            return@detectDragGestures
                        }

                        val _touchedTimeIndex = touchedTimeIndex
                        val _touchedDayIndex = touchedDayIndex
                        if (_touchedTimeIndex != null && _touchedDayIndex != null) {
                            if (_touchedTimeIndex < timeIndex) {
                                for (t in _touchedTimeIndex + 1..timeIndex) {
                                    if (eraseMode) unSelect(dayIndex, t)
                                    else select(dayIndex, t)
                                }
                            } else if (_touchedTimeIndex > timeIndex) {
                                for (t in timeIndex..<_touchedTimeIndex) {
                                    if (eraseMode) unSelect(dayIndex, t)
                                    else select(dayIndex, t)
                                }
                            } else if (_touchedDayIndex != dayIndex) {
                                if (eraseMode) unSelect(dayIndex, timeIndex)
                                else select(dayIndex, timeIndex)
                            }
                        }
                        touchedTimeIndex = timeIndex
                        touchedDayIndex = dayIndex
                    },
                    onDragEnd = {
                        touchedTimeIndex = null
                        touchedDayIndex = null
                    },
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        if (touchedDayIndex == null && touchedTimeIndex == null) {
                            val dayIndex =
                                ((offset.x - hourLabelWidth) / unitWidth).toInt()
                            val timeIndex =
                                ((offset.y - dayLabelHeight) / unitHeight).toInt()

                            if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                                return@detectTapGestures
                            }

                            if (isSelected(dayIndex, timeIndex)) unSelect(dayIndex, timeIndex)
                            else select(dayIndex, timeIndex)
                        }
                    },
                )
            },
    ) {
        canvasSize = size
    }
}
