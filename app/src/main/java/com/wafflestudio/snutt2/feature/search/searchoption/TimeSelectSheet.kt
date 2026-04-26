package com.wafflestudio.snutt2.feature.search.searchoption

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.trimByTrimParam
import com.wafflestudio.snutt2.feature.home.timetable.DrawClassTime
import com.wafflestudio.snutt2.feature.home.timetable.DrawTableGrid
import com.wafflestudio.snutt2.feature.home.timetable.TimetableCanvasObjects
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.theme.onSurfaceVariant

@Composable
fun TimeSelectSheet(
    modifier: Modifier = Modifier,
    backHandlerEnabled: Boolean,
    initialDraggedTimeBlock: List<List<Boolean>>,
    currentTableLectures: List<LocalLecture>,
    tableLectureCustomOptions: TableLectureCustom,
    onCancel: () -> Unit,
    onConfirm: (List<List<Boolean>>) -> Unit,
) {
    val draggedTimeBlock = remember {
        initialDraggedTimeBlock.map { row -> row.map { mutableStateOf(it) } }
    }

    val backgroundLectureSessions = remember(currentTableLectures) {
        currentTableLectures.flatMap { it.lectureSessions }
    }
    val selectComplementBlocks = {
        val complementGrid = calculateComplementBlocks(backgroundLectureSessions, TableTrimParam.SearchOption)
        draggedTimeBlock.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ ->
                draggedTimeBlock[i][j].value = complementGrid[i][j]
            }
        }
    }
    val resetToInitialBlocks = {
        draggedTimeBlock.forEachIndexed { dayIndex, dayColumn ->
            dayColumn.forEachIndexed { timeIndex, _ ->
                draggedTimeBlock[dayIndex][timeIndex].value =
                    initialDraggedTimeBlock[dayIndex][timeIndex]
            }
        }
    }

    BackHandler(backHandlerEnabled) {
        resetToInitialBlocks()
        onCancel()
    }

    Column(
        modifier = modifier
            .padding(bottom = 20.dp)
            .height(
                with(LocalDensity.current) {
                    LocalWindowInfo.current.containerSize.height.toDp()
                } * SearchOptionSheetConstants.MAX_HEIGHT_RATIO,
            ),
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
                text = stringResource(R.string.common_complete),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(draggedTimeBlock.map { row -> row.map { it.value } })
                },
            )
        }
        Row(modifier = Modifier.padding(start = 30.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier
                    .background(
                        color = if (isDarkMode()) SNUTTColors.DarkerGray else SNUTTColors.Gray2,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .clicks { selectComplementBlocks() }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SnuttIcon(
                    R.drawable.ic_magic,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(3.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray20 else SNUTTColors.White),
                )
                Text(
                    text = stringResource(R.string.search_option_select_empty_time_slots),
                    style = SNUTTTypography.body1.copy(color = if (isDarkMode()) SNUTTColors.Gray20 else SNUTTColors.White),
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
                SnuttIcon(
                    R.drawable.ic_reset,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.search_option_select_clear_time_slots),
                    style = SNUTTTypography.body1.copy(color = MaterialTheme.colors.onSurfaceVariant),
                )
            }
        }
        Text(
            text = stringResource(R.string.search_option_select_guide),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.VacancyGray),
            modifier = Modifier.padding(start = 30.dp, bottom = 9.dp),
        )
        Box(modifier = Modifier.padding(horizontal = 20.5.dp)) {
            TimeTableDragSheet(
                draggedTimeBlock = draggedTimeBlock,
                backgroundLectures = currentTableLectures,
                tableLectureCustomOptions = tableLectureCustomOptions,
            )
        }
    }
}

@Composable
private fun TimeTableDragSheet(
    draggedTimeBlock: List<List<MutableState<Boolean>>>,
    backgroundLectures: List<LocalLecture>,
    tableLectureCustomOptions: TableLectureCustom,
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

    val isDark = isDarkMode()
    val fgColor = if (isDark) SearchOptionSheetConstants.TimeBlockFgColorDark else SearchOptionSheetConstants.TimeBlockFgColorLight
    val bgColor = if (isDark) SearchOptionSheetConstants.TimeBlockBgColorDark else SearchOptionSheetConstants.TimeBlockBgColorLight

    backgroundLectures.forEach { lecture ->
        val isCustom = lecture is CustomLecture
        val lectureNumber = (lecture as? LectureSyllabusInfo)?.lectureNumber ?: ""
        lecture.lectureSessions.mapNotNull { it.trimByTrimParam(TableTrimParam.SearchOption) }
            .forEach { session ->
                DrawClassTime(
                    session = session,
                    foregroundColor = fgColor,
                    backgroundColor = bgColor,
                    courseTitle = lecture.courseTitle,
                    lectureNumber = lectureNumber,
                    instructorName = lecture.instructor,
                    isCustom = isCustom,
                    fittedTrimParam = TableTrimParam.SearchOption,
                    compactMode = true,
                    tableLectureCustomOptions = tableLectureCustomOptions,
                )
            }
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
            (canvasSize.height - dayLabelHeight) / ((fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1) * 2)
        }
    }

    // 드래그 속도가 빠를 때, 중간중간 터치 콜백이 비는 칸을 채워주기 위해 가장 마지막으로 처리한 칸을 저장한다.
    // 가장 마지막으로 처리한 칸과 현재 터치 콜백이 온 칸 사이의 모든 칸들을 처리한다.
    var touchedTimeIndex: Int? by remember { mutableStateOf(null) }
    var touchedDayIndex: Int? by remember { mutableStateOf(null) }
    var eraseMode: Boolean by remember { mutableStateOf(false) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                val dayIndex = ((it.x - hourLabelWidth) / unitWidth).toInt()
                val timeIndex = ((it.y - dayLabelHeight) / unitHeight).toInt()

                if (dayIndex < 0 ||
                    dayIndex > fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom ||
                    timeIndex < 0 ||
                    timeIndex > (fittedTrimParam.hourTo - fittedTrimParam.hourFrom) * 2 + 1
                ) {
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

private fun calculateComplementBlocks(
    sessions: List<LectureSession>,
    trimParam: TableTrimParam,
): List<List<Boolean>> {
    val dayCount = trimParam.dayOfWeekTo - trimParam.dayOfWeekFrom + 1
    val timeSlotCount = (trimParam.hourTo - trimParam.hourFrom + 1) * 2
    val grid = List(dayCount) { MutableList(timeSlotCount) { true } }

    val offsetMinute = trimParam.hourFrom * 60
    sessions.forEach { session ->
        val trimmed = session.trimByTrimParam(trimParam) ?: return@forEach
        val dayIndex = trimmed.day.ordinal - trimParam.dayOfWeekFrom
        val startMinute = trimmed.startTime.hour * 60 + trimmed.startTime.minute
        val endMinute = trimmed.endTime.hour * 60 + trimmed.endTime.minute
        for (minute in startMinute until endMinute step 30) {
            val timeIndex = (minute - offsetMinute) / 30
            if (timeIndex in 0 until timeSlotCount) {
                grid[dayIndex][timeIndex] = false
            }
        }
    }

    return grid
}

// region Preview

@SnuttPreview
@Composable
private fun TimeSelectSheet_Empty() {
    SnuttPreviewSurface {
        TimeSelectSheet(
            backHandlerEnabled = false,
            initialDraggedTimeBlock = TableTrimParam.TimeBlockGridDefault,
            currentTableLectures = listOf(PreviewData.syllabusLecture),
            tableLectureCustomOptions = TableLectureCustom.Default,
            onCancel = {},
            onConfirm = {},
        )
    }
}

@SnuttPreview
@Composable
private fun TimeSelectSheet_SomeSelected() {
    val grid = TableTrimParam.TimeBlockGridDefault.mapIndexed { dayIndex, column ->
        column.mapIndexed { timeIndex, _ ->
            dayIndex in 0..2 && timeIndex in 4..7
        }
    }
    SnuttPreviewSurface {
        TimeSelectSheet(
            backHandlerEnabled = false,
            initialDraggedTimeBlock = grid,
            currentTableLectures = listOf(PreviewData.syllabusLecture),
            tableLectureCustomOptions = TableLectureCustom.Default,
            onCancel = {},
            onConfirm = {},
        )
    }
}

// endregion
