package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.view.MotionEvent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.contains
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.roundToCompact
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.lib.toDayString
import com.wafflestudio.snutt2.lib.trimByTrimParam
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalCompactState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import kotlin.math.max
import kotlin.math.min

@Composable
fun TimeTable(
    touchEnabled: Boolean = true,
    selectedLecture: LectureDto?,
) {
    val previewTheme = LocalTableState.current.previewTheme
    val lectures = LocalTableState.current.table.lectureList.let { // 테마 미리보기용 색 배치 로직. 서버와 통일되어 있다(2024-01-12)
        previewTheme?.let { theme ->
            if (previewTheme is CustomTheme) {
                it.mapIndexed { idx, lecture ->
                    lecture.copy(
                        colorIndex = 0,
                        color = (theme as CustomTheme).colors[idx % previewTheme.colors.size],
                    )
                }
            } else {
                it.mapIndexed { idx, lecture ->
                    lecture.copy(
                        colorIndex = idx % 9L + 1,
                    )
                }
            }
        } ?: it
    }

    val trimParam = LocalTableState.current.trimParam
    val fittedTrimParam =
        if (trimParam.forceFitLectures) {
            (selectedLecture?.let { lectures + it } ?: lectures).getFittingTrimParam(
                TableTrimParam.Default,
            )
        } else {
            trimParam
        }

    if (touchEnabled) DrawClickEventDetector(lectures, fittedTrimParam)
    DrawTableGrid(fittedTrimParam)
    DrawLectures(lectures, fittedTrimParam)
    DrawSelectedLecture(selectedLecture, fittedTrimParam)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DrawClickEventDetector(lectures: List<LectureDto>, fittedTrimParam: TableTrimParam) {
    val navigator = LocalNavController.current
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>()
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val unitWidth =
                        (canvasSize.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
                    val unitHeight =
                        (canvasSize.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

                    val day =
                        ((event.x - hourLabelWidth) / unitWidth).toInt() + fittedTrimParam.dayOfWeekFrom
                    val time =
                        ((event.y - dayLabelHeight) / unitHeight) + fittedTrimParam.hourFrom

                    for (lecture in lectures) {
                        if (lecture.contains(day, time)) {
                            lectureDetailViewModel.initializeEditingLectureDetail(lecture, ModeType.Normal)
                            navigator.navigate(NavigationDestination.LectureDetail) {
                                launchSingleTop = true
                            }
                            break
                        }
                    }
                }
                true
            },
    ) {
        canvasSize = size
    }
}

@Composable
fun DrawTableGrid(fittedTrimParam: TableTrimParam) {
    val context = LocalContext.current
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    val dayLabelTextPaint = TimetableCanvasObjects.dayLabelTextPaint
    val hourLabelTextPaint = TimetableCanvasObjects.hourLabelTextPaint
    val textHeight = TimetableCanvasObjects.dayLabelTextHeight

    val gridColor = SNUTTColors.TableGrid
    val gridColor2 = SNUTTColors.TableGrid2

    Canvas(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (size.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val verticalLines = fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1
        var startWidth = hourLabelWidth
        val horizontalLines = fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1
        var startHeight = dayLabelHeight

        repeat(verticalLines) {
            drawLine(
                start = Offset(x = startWidth, y = 0f),
                end = Offset(x = startWidth, y = size.height),
                color = gridColor,
                strokeWidth = (0.5f).dp(context),
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    (fittedTrimParam.dayOfWeekFrom + it).toDayString(context),
                    startWidth + unitWidth * 0.5f,
                    (dayLabelHeight + textHeight) / 2f,
                    dayLabelTextPaint,
                )
            }
            startWidth += unitWidth
        }
        repeat(horizontalLines) {
            drawLine(
                start = Offset(x = 0f, y = startHeight),
                end = Offset(x = size.width, y = startHeight),
                color = gridColor,
                strokeWidth = (0.5f).dp(context),
            )
            drawLine(
                start = Offset(x = hourLabelWidth, y = startHeight + (unitHeight * 0.5f)),
                end = Offset(x = size.width, y = startHeight + (unitHeight * 0.5f)),
                color = gridColor2,
                strokeWidth = (0.5f).dp(context),
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    (fittedTrimParam.hourFrom + it).toString(),
                    hourLabelWidth - 4.dp(context),
                    startHeight + textHeight + 6.dp(context),
                    hourLabelTextPaint,
                )
            }
            startHeight += unitHeight
        }
    }
}

@Composable
fun DrawLectures(lectures: List<LectureDto>, fittedTrimParam: TableTrimParam) {
    lectures.forEach { lecture ->
        lecture.class_time_json
            .mapNotNull {
                it.trimByTrimParam(fittedTrimParam)
            }
            .forEach { classTime ->
                DrawLecture(lecture, classTime, fittedTrimParam)
            }
    }
}

@Composable
private fun DrawLecture(
    lecture: LectureDto,
    classTime: ClassTimeDto,
    fittedTrimParam: TableTrimParam,
) {
    val context = LocalContext.current
    val code = (LocalTableState.current.previewTheme as? BuiltInTheme)?.code ?: LocalTableState.current.table.theme

    DrawClassTime(
        fittedTrimParam = fittedTrimParam,
        classTime = classTime,
        courseTitle = lecture.course_title,
        bgColor =
        if (lecture.colorIndex == 0L && lecture.color.bgColor != null) {
            lecture.color.bgColor!!
        } else {
            BuiltInTheme.fromCode(code).getColorByIndexComposable(
                lecture.colorIndex,
            ).toArgb()
        },
        fgColor = if (lecture.colorIndex == 0L && lecture.color.fgColor != null) {
            lecture.color.fgColor!!
        } else {
            context.getColor(
                R.color.white,
            )
        },
        isCustom = lecture.isCustom,
    )
}

@Composable
private fun DrawClassTime(
    fittedTrimParam: TableTrimParam,
    classTime: ClassTimeDto,
    courseTitle: String,
    bgColor: Int,
    fgColor: Int,
    isCustom: Boolean = false,
) {
    val hourLabelWidth = 24.5.dp
    val dayLabelHeight = 28.5.dp
    val cellPadding = 4.dp
    val compactMode = LocalCompactState.current

    val dayOffset = classTime.day - fittedTrimParam.dayOfWeekFrom
    val hourRangeOffset =
        Pair(
            max(classTime.startTimeInFloat - fittedTrimParam.hourFrom, 0f),
            min(
                classTime.endTimeInFloat.let { if (isCustom.not() && compactMode) roundToCompact(it) else it } - fittedTrimParam.hourFrom,
                fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1,
            ),
        )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (maxWidth - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (maxHeight - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val left = hourLabelWidth + unitWidth * dayOffset
        val top = dayLabelHeight + unitHeight * hourRangeOffset.first

        Column(
            modifier = Modifier
                .size(width = unitWidth, height = unitHeight * (hourRangeOffset.second - hourRangeOffset.first))
                .offset(x = left, y = top)
                .border(width = 1.dp, color = SNUTTColors.Black050)
                .background(color = Color(bgColor))
                .padding(horizontal = cellPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = courseTitle,
                color = Color(fgColor),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
            )
            Text(
                text = classTime.place,
                color = Color(fgColor),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun DrawSelectedLecture(selectedLecture: LectureDto?, fittedTrimParam: TableTrimParam) {
    selectedLecture?.run {
        for (classTime in class_time_json) {
            DrawClassTime(
                fittedTrimParam, classTime, course_title, -0x1f1f20, -0xcccccd,
            )
        }
    }
}
