package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.annotation.ColorInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.MagicIcon
import com.wafflestudio.snutt2.components.compose.ResetIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.trimByTrimParam
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalCompactState
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
        initialDraggedTimeBlock.value.map { row -> row.map { mutableStateOf(it) } }
    }

    val currentTableLectures = LocalTableState.current.table.lectureList
    val backgroundLectureTimes = remember {
        currentTableLectures.flatMap { it.class_time_json }.mapNotNull {
            it.trimByTrimParam(TableTrimParam.SearchOption)
        }
    }
    val selectComplementBlocks = {
        draggedTimeBlock.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ ->
                draggedTimeBlock[i][j].value = true
            }
        }
        backgroundLectureTimes.forEach {
            for (timeIndex in (it.startMinute - TableTrimParam.SearchOption.hourFrom * 60) / 30..(it.endMinute - TableTrimParam.SearchOption.hourFrom * 60) / 30) {
                draggedTimeBlock[it.day][timeIndex].value = false
            }
        }
    }
    val resetToInitialBlocks = {
        draggedTimeBlock.forEachIndexed { dayIndex, dayColumn ->
            dayColumn.forEachIndexed { timeIndex, _ ->
                draggedTimeBlock[dayIndex][timeIndex].value =
                    initialDraggedTimeBlock.value[dayIndex][timeIndex]
            }
        }
    }

    // 시간대 선택에서 뒤로가기 하면 태그 선택으로 가기
    BackHandler(basedAnimatedFloat.value != 0f) {
        resetToInitialBlocks()
        onCancel()
    }

    Column(
        modifier = Modifier
            .alpha(alphaAnimatedFloat)
            .padding(bottom = 20.dp)
            .height(LocalConfiguration.current.screenHeightDp.dp * SearchOptionSheetConstants.MaxHeightRatio),
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
                    resetToInitialBlocks()
                    onCancel()
                },
            )
            Text(
                text = stringResource(R.string.common_complete), style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(draggedTimeBlock.map { row -> row.map { it.value } })
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
                    .clicks { selectComplementBlocks() }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MagicIcon(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(3.dp),
                )
                Text(
                    text = stringResource(R.string.search_option_select_empty_time_slots),
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.AllWhite),
                )
            }
            Row(
                modifier = Modifier
                    .clicks {
                        // 드래그 상태 초기화
                        draggedTimeBlock.forEachIndexed { i, row ->
                            row.forEachIndexed { j, _ ->
                                draggedTimeBlock[i][j].value = false
                            }
                        }
                    }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ResetIcon(
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.search_option_select_clear_time_slots),
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray2),
                )
            }
        }
        Text(
            text = stringResource(R.string.search_option_select_guide),
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
                DrawTimeBlock(day = day, (2 * TableTrimParam.SearchOption.hourFrom + time) * 30)
            }
        }
    }

    DrawDragEventDetector(
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
    CompositionLocalProvider(
        LocalCompactState provides true,
    ) {
        DrawLectures(
            fittedTrimParam = TableTrimParam.SearchOption,
            lectures = LocalTableState.current.table.lectureList.map {
                it.copy(
                    colorIndex = 0L,
                    color = SearchOptionSheetConstants.TimeBlockColor,
                )
            },
        )
    }
}

@Composable
private fun DrawTimeBlock(
    day: Int,
    startMinute: Int,
    @ColorInt bgColor: Int = SearchOptionSheetConstants.BackgroundLectureBlockColor,
) {
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val hourRangeOffset = Pair(
        startMinute / 60f - TableTrimParam.SearchOption.hourFrom,
        (startMinute + 30f) / 60f - TableTrimParam.SearchOption.hourFrom,
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f),
    ) {
        val unitWidth =
            (size.width - hourLabelWidth) / (TableTrimParam.SearchOption.dayOfWeekTo - TableTrimParam.SearchOption.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (TableTrimParam.SearchOption.hourTo - TableTrimParam.SearchOption.hourFrom + 1)

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
private fun DrawDragEventDetector(
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
    var touchedTimeIndex: Int? by remember { mutableStateOf(null) } // 8시 ~ 8시30분 칸 : 0, 8시 30분 ~ 9시 칸 : 1, ...
    var touchedDayIndex: Int? by remember { mutableStateOf(null) }
    var eraseMode: Boolean by remember { mutableStateOf(false) } // true면 지우는 모드, false면 칠하는 모드

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                val dayIndex = ((it.x - hourLabelWidth) / unitWidth).toInt()
                val timeIndex = ((it.y - dayLabelHeight) / unitHeight).toInt()

                if (dayIndex < 0 || dayIndex > 4 || timeIndex < 0 || timeIndex > 27) {
                    return@pointerInteropFilter false
                }

                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 터치를 시작한 칸이 칠해져 있으면 지우기 모드, 비어 있으면 칠하기 모드
                        eraseMode = isSelected(dayIndex, timeIndex)
                        if (eraseMode) {
                            erase(dayIndex, timeIndex)
                        } else {
                            select(dayIndex, timeIndex)
                        }
                        // 처리한 칸 저장
                        touchedTimeIndex = timeIndex
                        touchedDayIndex = dayIndex
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        // nullable var 이라서 지역 변수로 저장
                        val lastTouchedTimeIndex = touchedTimeIndex
                        val lastTouchedDayIndex = touchedDayIndex
                        if (lastTouchedTimeIndex != null && lastTouchedDayIndex != null) {
                            if (lastTouchedTimeIndex < timeIndex) {
                                // vertical drag, 방향은 아래
                                // 터치 콜백이 온 마지막 칸과 현재 칸 사이의 모든 칸을 처리해 준다.
                                for (t in lastTouchedTimeIndex + 1..timeIndex) {
                                    if (eraseMode) {
                                        erase(dayIndex, t)
                                    } else {
                                        select(dayIndex, t)
                                    }
                                }
                            } else if (lastTouchedTimeIndex > timeIndex) {
                                // vertical drag, 방향은 위
                                // 터치 콜백이 온 마지막 칸과 현재 칸 사이의 모든 칸을 처리해 준다.
                                for (t in timeIndex until lastTouchedTimeIndex) {
                                    if (eraseMode) {
                                        erase(dayIndex, t)
                                    } else {
                                        select(dayIndex, t)
                                    }
                                }
                            } else if (lastTouchedDayIndex != dayIndex) {
                                // horizontal drag
                                if (eraseMode) {
                                    erase(dayIndex, timeIndex)
                                } else {
                                    select(dayIndex, timeIndex)
                                }
                            }
                        }

                        // 처리한 칸 저장
                        touchedTimeIndex = timeIndex
                        touchedDayIndex = dayIndex
                        true
                    }

                    else -> {
                        false
                    }
                }
            },
    ) {
        canvasSize = size
    }
}
