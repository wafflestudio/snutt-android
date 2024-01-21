package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.annotation.ColorInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.MagicIcon
import com.wafflestudio.snutt2.components.compose.ResetIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.trimByTrimParam
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.DrawLectures
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

    val backgroundLectureTimes =
        LocalTableState.current.table.lectureList.flatMap { it.class_time_json }
            .mapNotNull {
                it.trimByTrimParam(TableTrimParam.SearchOption)
            }
    val `빈_시간대_칠하기` = {
        // 드래그 상태 초기화
        draggedTimeBlock.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ ->
                draggedTimeBlock[i][j].value = true
            }
        }
        backgroundLectureTimes.forEach {
            for (timeIndex in (it.startMinute - TableTrimParam.SearchOption.hourFrom * 60) / 30 .. (it.endMinute - TableTrimParam.SearchOption.hourFrom * 60) / 30) {
                draggedTimeBlock[it.day][timeIndex].value = false
            }
        }
    }


    Column(
        modifier = Modifier
            .alpha(alphaAnimatedFloat)
            .padding(bottom = 20.dp)
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.85f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.common_cancel),
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
                text = stringResource(R.string.common_complete), style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(draggedTimeBlock.map { it.map { it.value } })
                },
            )
        }
        Row(modifier = Modifier.padding(start = 30.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier
                    .background(
                        color = SNUTTColors.Gray2,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .clicks {
                        // 빈 시간대 모두 색칠
                        빈_시간대_칠하기()
                    }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MagicIcon(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(3.dp),
                )
                Text(
                    text = "빈 시간대 선택하기",
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.AllWhite),
                )
            }
            Row(
                modifier = Modifier
                    .clicks {
                        // 드래그 상태 초기화
                        initialDraggedTimeBlock.value.forEachIndexed { i, row ->
                            row.forEachIndexed { j, initialValue ->
                                draggedTimeBlock[i][j].value = initialValue
                            }
                        }
                    }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ResetIcon(
                    modifier = Modifier
                        .size(20.dp),
                )
                Text(
                    text = "초기화",
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray2),
                )
            }
        }
        Text(
            text = "드래그하여 시간대를 선택해보세요",
            style = SNUTTTypography.body1.copy(color = SNUTTColors.VacancyGray),
            modifier = Modifier.padding(start = 30.dp, bottom = 9.dp),
        )
        Box(modifier = Modifier.padding(horizontal = 20.5.dp)) {
            TimeTableDragSheet(draggedTimeBlock)
        }
    }
}

@Composable
fun TimeTableDragSheet(
    draggedTimeBlock: List<List<MutableState<Boolean>>>,
) {
    DrawTableGrid(TableTrimParam.SearchOption)
    for (day in draggedTimeBlock.indices) {
        for (time in draggedTimeBlock.first().indices) {
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
        erase = { dayIndex, timeIndex ->
            draggedTimeBlock[dayIndex][timeIndex].value = false
        },
    )
    DrawLectures(
        fittedTrimParam = TableTrimParam.SearchOption,
        lectures = LocalTableState.current.table.lectureList.map {
            it.copy(
                colorIndex = 0L,
                color = ColorDto(
                    fgRaw = "#FFFFFF",
                    bgRaw = "#B3DADADA",
                ),
            )
        },
    )
}

@Composable
private fun DrawTimeBlock(
    day: Int,
    startMinute: Int,
    @ColorInt bgColor: Int = Color.argb(153, 27, 208, 200),
) {
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val hourRangeOffset = Pair(startMinute / 60f - 8f, (startMinute + 30f) / 60f - 8f)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f),
    ) {
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
    erase: (dayIndex: Int, timeIndex: Int) -> Unit,
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

    // 드래그 속도가 빠를 때, 중간중간 터치 콜백이 비는 칸을 채워주기 위해 가장 마지막으로 처리한 칸을 저장한다.
    // 가장 마지막으로 처리한 칸과 현재 터치 콜백이 온 칸 사이의 모든 칸들을 처리한다.
    var touchedTimeIndex: Int? = remember { null } // 8시 ~ 8시30분 칸 : 0, 8시 30분 ~ 9시 칸 : 1, ...
    var touchedDayIndex: Int? = remember { null }
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

                        // 터치를 시작한 칸이 칠해져 있으면 지우기 모드
                        // 터치를 시작한 칸이 비어 있으면 칠하기 모드
                        eraseMode = isSelected(dayIndex, timeIndex)
                        if (eraseMode) erase(dayIndex, timeIndex)
                        else select(dayIndex, timeIndex)

                        // 처리한 칸 저장
                        touchedTimeIndex = timeIndex
                        touchedDayIndex = dayIndex
                    },
                    onDrag = { change, _ ->
                        val dayIndex = ((change.position.x - hourLabelWidth) / unitWidth).toInt()
                        val timeIndex = ((change.position.y - dayLabelHeight) / unitHeight).toInt()

                        if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                            return@detectDragGestures
                        }

                        // nullable var 이라서 지역 변수로 저장
                        val lastTouchedTimeIndex = touchedTimeIndex
                        val lastTouchedDayIndex = touchedDayIndex
                        if (lastTouchedTimeIndex != null && lastTouchedDayIndex != null) {
                            if (lastTouchedTimeIndex < timeIndex) {
                                // vertical drag, 방향은 위
                                // 터치 콜백이 온 마지막 칸과 현재 칸 사이의 모든 칸을 처리해 준다.
                                for (t in lastTouchedTimeIndex + 1..timeIndex) {
                                    if (eraseMode) erase(dayIndex, t)
                                    else select(dayIndex, t)
                                }
                            } else if (lastTouchedTimeIndex > timeIndex) {
                                // vertical drag, 방향은 아래
                                // 터치 콜백이 온 마지막 칸과 현재 칸 사이의 모든 칸을 처리해 준다.
                                for (t in timeIndex..<lastTouchedTimeIndex) {
                                    if (eraseMode) erase(dayIndex, t)
                                    else select(dayIndex, t)
                                }
                            } else if (lastTouchedDayIndex != dayIndex) {
                                // 여긴 horizontal drag에 해당
                                if (eraseMode) erase(dayIndex, timeIndex)
                                else select(dayIndex, timeIndex)
                            }
                        }
                        // 처리한 칸 저장
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
                    // drag가 아닌 단순 tap도 따로 콜백을 달아야 한다.
                    onTap = { offset ->
                        // 여기서는 drag 콜백에서 썼던 touchedTimeIndex, erase 등등을 사용하지 않는다. (충돌 방지)
                        if (touchedDayIndex == null && touchedTimeIndex == null) {
                            val dayIndex =
                                ((offset.x - hourLabelWidth) / unitWidth).toInt()
                            val timeIndex =
                                ((offset.y - dayLabelHeight) / unitHeight).toInt()

                            if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                                return@detectTapGestures
                            }

                            if (isSelected(dayIndex, timeIndex)) erase(dayIndex, timeIndex)
                            else select(dayIndex, timeIndex)
                        }
                    },
                )
            },
    ) {
        canvasSize = size
    }
}
