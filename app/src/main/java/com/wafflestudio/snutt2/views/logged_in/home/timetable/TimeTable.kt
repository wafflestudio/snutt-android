package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.contains
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.roundToCompact
import com.wafflestudio.snutt2.lib.toDayString
import com.wafflestudio.snutt2.lib.trimByTrimParam
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode
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
    val lectures =
        LocalTableState.current.table.lectureList.let { // 테마 미리보기용 색 배치 로직. 서버와 통일되어 있다(2024-01-12)
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
                            lectureDetailViewModel.initializeEditingLectureDetail(
                                lecture,
                                ModeType.Normal,
                            )
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
    val gridColor = SNUTTColors.TableGrid
    val gridColor2 = SNUTTColors.TableGrid2

    val hourLabelWidth = 24.5.dp
    val dayLabelHeight = 28.5.dp

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (maxWidth - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (maxHeight - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val verticalLines = fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1
        val horizontalLines = fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1

        repeat(verticalLines) { idx ->
            Box(
                modifier = Modifier
                    .offset(x = hourLabelWidth + unitWidth * idx)
                    .size(width = 0.5.dp, height = maxHeight)
                    .background(gridColor),
            )
            Box(
                modifier = Modifier
                    .offset(x = hourLabelWidth + unitWidth * idx)
                    .size(width = unitWidth, height = dayLabelHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (fittedTrimParam.dayOfWeekFrom + idx).toDayString(context),
                    textAlign = TextAlign.Center,
                    color = if (isDarkMode()) {
                        Color(119, 119, 119, 180)
                    } else {
                        Color(0, 0, 0, 180)
                    },
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                )
            }
        }
        repeat(horizontalLines) { idx ->
            Box(
                modifier = Modifier
                    .offset(y = dayLabelHeight + unitHeight * idx)
                    .size(width = maxWidth, height = 0.5.dp)
                    .background(gridColor),
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = hourLabelWidth,
                        y = dayLabelHeight + unitHeight * idx + unitHeight * 0.5f,
                    )
                    .size(width = maxWidth, height = 0.5.dp)
                    .background(gridColor2),
            )
            Box(
                modifier = Modifier
                    .offset(y = dayLabelHeight + unitHeight * idx)
                    .size(width = hourLabelWidth, height = unitHeight)
                    .padding(top = 4.dp, end = 4.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                Text(
                    text = (fittedTrimParam.hourFrom + idx).toString(),
                    textAlign = TextAlign.Right,
                    color = if (isDarkMode()) {
                        Color(119, 119, 119, 180)
                    } else {
                        Color(0, 0, 0, 180)
                    },
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                )
            }
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
                val context = LocalContext.current
                val code = (LocalTableState.current.previewTheme as? BuiltInTheme)?.code
                    ?: LocalTableState.current.table.theme

                DrawClassTime(
                    fittedTrimParam = fittedTrimParam,
                    classTime = classTime,
                    courseTitle = lecture.course_title,
                    lectureNumber = lecture.lecture_number.orEmpty(),
                    instructorName = lecture.instructor,
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
    }
}

@Composable
private fun DrawClassTime(
    fittedTrimParam: TableTrimParam,
    classTime: ClassTimeDto,
    courseTitle: String,
    lectureNumber: String,
    instructorName: String,
    bgColor: Int,
    fgColor: Int,
    isCustom: Boolean = false,
) {
    val hourLabelWidth = 24.5.dp
    val dayLabelHeight = 28.5.dp
    val cellPadding = 4.dp
    val compactMode = LocalCompactState.current
    val textMeasurer = rememberTextMeasurer()

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

        Column(
            modifier = Modifier
                .size(
                    width = unitWidth,
                    height = unitHeight * (hourRangeOffset.second - hourRangeOffset.first),
                )
                .offset(
                    x = hourLabelWidth + unitWidth * dayOffset,
                    y = dayLabelHeight + unitHeight * hourRangeOffset.first,
                )
                .border(width = 1.dp, color = SNUTTColors.Black050)
                .background(color = Color(bgColor))
                .padding(horizontal = cellPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BoxWithConstraints {
                val constraints = constraints
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val adjustedTextLayouts = remember(
                        constraints,
                        courseTitle,
                        classTime.place,
                        lectureNumber,
                        instructorName,
                        fittedTrimParam,
                    ) {
                        try {
                            calculateAdjustedTextLayout(
                                listOf(
                                    LectureCellInfo.titleTextLayout(courseTitle, true),
                                    LectureCellInfo.placeTextLayout(classTime.place, true),
                                    LectureCellInfo.lectureNumberTextLayout(lectureNumber, false),
                                    LectureCellInfo.instructorNameTextLayout(instructorName, false),
                                ),
                                textMeasurer,
                                constraints,
                            )
                        } catch (e: Exception) {
                            // NOTE(@JuTaK): 혹시 모를 크래시를 대비하여 try-catch를 추가하고 로그를 심는다.
                            FirebaseCrashlytics.getInstance().recordException(
                                Throwable(
                                    cause = e,
                                    message = "$courseTitle ${classTime.place} $lectureNumber $instructorName $constraints $fittedTrimParam"
                                )
                            )
                            emptyList()
                        }

                    }

                    adjustedTextLayouts
                        .forEach { textLayout ->
                            if (textLayout.maxLines > 0) {
                                Text(
                                    text = textLayout.text,
                                    style = textLayout.style.copy(color = Color(fgColor)),
                                    maxLines = textLayout.maxLines,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            } else {
                                // NOTE(@JuTaK): 혹시 모를 크래시를 대비하여 try-catch를 추가하고 로그를 심는다.
                                FirebaseCrashlytics.getInstance().recordException(
                                    Throwable(
                                        cause = IllegalStateException(),
                                        message = "$courseTitle ${classTime.place} $lectureNumber $instructorName $constraints $fittedTrimParam"
                                    )
                                )
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun DrawSelectedLecture(selectedLecture: LectureDto?, fittedTrimParam: TableTrimParam) {
    selectedLecture?.run {
        for (classTime in class_time_json) {
            DrawClassTime(
                fittedTrimParam,
                classTime,
                course_title,
                lecture_number.orEmpty(),
                instructor,
                -0x1f1f20,
                -0xcccccd,
            )
        }
    }
}
