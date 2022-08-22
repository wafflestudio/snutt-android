package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.TextRect
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.contains
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.dto.core.*
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.lib.rx.sp
import com.wafflestudio.snutt2.lib.toDayString
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.HomeDrawerStateContext
import com.wafflestudio.snutt2.views.logged_in.home.TableContext
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class CanvasContext(
    val context: Context,
    val fittedTrimParam: TableTrimParam
) {
    val hourLabelWidth = 24.5f.dp(context)
    val dayLabelHeight = 28.5f.dp(context)
    val cellPadding = 4.dp(context)

    val dayLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_light)
        }
    val hourLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.RIGHT
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_light)
        }
    val lectureCellTextRect =
        TextRect(
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 10f.sp(context)
                typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_regular)
            }
        )
    val lectureCellSubTextRect =
        TextRect(
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f.sp(context)
                typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_bold)
            }
        )
}

val LocalCanvasContext = compositionLocalOf<CanvasContext> {
    throw RuntimeException("")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimetablePage(
    uncheckedNotification: Boolean
) {
    val navController = NavControllerContext.current
    val drawerState = HomeDrawerStateContext.current
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val keyboardManager = LocalSoftwareKeyboardController.current
    val table = TableContext.current.table
    val scope = rememberCoroutineScope()
    var changeTitleDialogState by remember {
        mutableStateOf(false)
    }
    if (changeTitleDialogState) {
        ChangeTitleDialog(
            onDismiss = {
                changeTitleDialogState = false
                keyboardManager?.hide()
            },
            onConfirm = { newTitle ->
                tableListViewModel.changeNameTable(
                    tableId = table.id,
                    name = newTitle
                )
                    .subscribeBy(
                        onError = {}
                    )
                changeTitleDialogState = false
                keyboardManager?.hide()
            },
            oldTitle = table.title
        )
    }

    Column {
        TopAppBar(
            title = {
                val creditText = stringResource(
                    id = R.string.timetable_credit,
                    table.lectureList.fold(0L) { acc, lecture -> acc + lecture.credit }
                )
                Row {
                    Text(
                        text = table.title,
                        modifier = Modifier.clicks {
                            changeTitleDialogState = true
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = creditText)
                }
            },
            navigationIcon = {
                Image(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .clicks {
                            scope.launch { drawerState.open() }
                        },
                    painter = painterResource(id = R.drawable.ic_drawer),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
            },
            actions = {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.LecturesOfTable) },
                    painter = painterResource(id = R.drawable.ic_lecture_list),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.Notification) },
                    painter = painterResource(
                        id = if (uncheckedNotification) R.drawable.ic_alarm_active else R.drawable.ic_alarm_default
                    ),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TimeTable(selectedLecture = Optional.empty())
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeTable(
    touchEnabled: Boolean = true,
    selectedLecture: Optional<LectureDto>
) {
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>()

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val context = LocalContext.current
    val navigator = NavControllerContext.current
    val lectures = TableContext.current.table.lectureList
    val trimParam = TableContext.current.trimParam

    val fittedTrimParam =
        if (trimParam.forceFitLectures) {
            (selectedLecture.value?.let { lectures + it } ?: lectures)
                .getFittingTrimParam(TableTrimParam.Default)
        } else trimParam

    val canvasContext = CanvasContext(
        context = context,
        fittedTrimParam = fittedTrimParam
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if (touchEnabled) {
                            val unitWidth =
                                (canvasSize.width - canvasContext.hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
                            val unitHeight =
                                (canvasSize.height - canvasContext.dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

                            val day =
                                ((event.x - canvasContext.hourLabelWidth) / unitWidth).toInt() + fittedTrimParam.dayOfWeekFrom
                            val time =
                                ((event.y - canvasContext.dayLabelHeight) / unitHeight) + fittedTrimParam.hourFrom - 8

                            for (lecture in lectures) {
                                if (lecture.contains(day, time)) {
                                    lectureDetailViewModel.initializeSelectedLectureFlow(lecture)
                                    navigator.navigate(NavigationDestination.LectureDetail)
                                    break
                                }
                            }
                        }
                    }
                }
                true
            }
    ) {
        canvasSize = size
    }

    CompositionLocalProvider(
        LocalCanvasContext provides canvasContext
    ) {
        DrawTableGrid()
        lectures.forEach {
            DrawLecture(lecture = it)
        }
        selectedLecture.value?.let {
            DrawSelectedLecture(it)
        }
    }
}

@Composable
private fun DrawTableGrid() {
    val context = LocalContext.current
    val fittedTrimParam = LocalCanvasContext.current.fittedTrimParam
    val hourLabelWidth = LocalCanvasContext.current.hourLabelWidth
    val dayLabelHeight = LocalCanvasContext.current.dayLabelHeight
    val dayLabelTextPaint = LocalCanvasContext.current.dayLabelTextPaint
    val hourLabelTextPaint = LocalCanvasContext.current.hourLabelTextPaint

    Canvas(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (size.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val textHeight = getTextHeight("월", dayLabelTextPaint) // TODO: 다른 방법 찾아보기

        val verticalLines = fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1
        var startWidth = hourLabelWidth
        val horizontalLines = fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1
        var startHeight = dayLabelHeight

        repeat(verticalLines) {
            drawLine(
                start = Offset(x = startWidth, y = 0f),
                end = Offset(x = startWidth, y = size.height),
                color = Color(235, 235, 235),
                strokeWidth = 0.5f
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    (fittedTrimParam.dayOfWeekFrom + it).toDayString(context),
                    startWidth + unitWidth * 0.5f,
                    (dayLabelHeight + textHeight) / 2f,
                    dayLabelTextPaint
                )
            }
            startWidth += unitWidth
        }
        repeat(horizontalLines) {
            drawLine(
                start = Offset(x = 0f, y = startHeight),
                end = Offset(x = size.width, y = startHeight),
                color = Color(235, 235, 235),
                strokeWidth = (0.5f).dp(context)
            )
            drawLine(
                start = Offset(x = hourLabelWidth, y = startHeight + (unitHeight * 0.5f)),
                end = Offset(x = size.width, y = startHeight + (unitHeight * 0.5f)),
                color = Color(243, 243, 243),
                strokeWidth = (0.5f).dp(context)
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    (fittedTrimParam.hourFrom + it).toString(),
                    hourLabelWidth - 4.dp(context),
                    startHeight + textHeight + 6.dp(context),
                    hourLabelTextPaint
                )
            }
            startHeight += unitHeight
        }
    }
}

@Composable
private fun DrawLecture(lecture: LectureDto) {
    val context = LocalContext.current
    val theme = TableContext.current.theme

    lecture.class_time_json.forEach {
        DrawClassTime(
            classTime = it,
            courseTitle = lecture.course_title,
            bgColor = if (lecture.colorIndex == 0L && lecture.color.bgColor != null)
                lecture.color.bgColor!! else theme.getColorByIndex(context, lecture.colorIndex),
            fgColor = if (lecture.colorIndex == 0L && lecture.color.fgColor != null)
                lecture.color.fgColor!! else context.getColor(R.color.white)
        )
    }
}

@Composable
private fun DrawClassTime(
    classTime: ClassTimeDto,
    courseTitle: String,
    bgColor: Int,
    fgColor: Int,
) {
    val context = LocalContext.current
    val fittedTrimParam = LocalCanvasContext.current.fittedTrimParam
    val hourLabelWidth = LocalCanvasContext.current.hourLabelWidth
    val dayLabelHeight = LocalCanvasContext.current.dayLabelHeight
    val cellPadding = LocalCanvasContext.current.cellPadding
    val lectureCellTextRect = LocalCanvasContext.current.lectureCellTextRect
    val lectureCellSubTextRect = LocalCanvasContext.current.lectureCellSubTextRect

    val dayOffset = classTime.day - fittedTrimParam.dayOfWeekFrom
    val hourRangeOffset = Pair(
        max(classTime.start - fittedTrimParam.hourFrom + 8, 0f),
        min(
            classTime.start + classTime.len - fittedTrimParam.hourFrom + 8,
            fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (size.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val left = hourLabelWidth + (dayOffset) * unitWidth
        val right =
            hourLabelWidth + (dayOffset) * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom =
            dayLabelHeight + (hourRangeOffset.second) * unitHeight

        val rect = RectF(left, top, right, bottom)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRect(rect, Paint().apply { color = bgColor })
            canvas.nativeCanvas.drawRect(
                rect,
                Paint().apply {
                    style = Paint.Style.STROKE
                    color = 0x0d000000
                    strokeWidth = 1.dp(context)
                }
            )
        }

        val cellHeight = bottom - top - cellPadding * 2
        val cellWidth = right - left - cellPadding * 2

        val courseTitleHeight = lectureCellTextRect.prepare(
            courseTitle,
            cellWidth.toInt(),
            cellHeight.toInt()
        )
        val locationHeight = lectureCellSubTextRect.prepare(
            classTime.place,
            cellWidth.toInt(),
            cellHeight.toInt() - courseTitleHeight
        )

        drawIntoCanvas { canvas ->
            lectureCellTextRect.draw(
                canvas.nativeCanvas,
                (left + cellPadding).toInt(),
                (top + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
                cellWidth.toInt(),
                fgColor
            )
            lectureCellSubTextRect.draw(
                canvas.nativeCanvas,
                (left + cellPadding).toInt(),
                (top + courseTitleHeight + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
                cellWidth.toInt(),
                fgColor
            )
        }
    }
}

@Composable
private fun DrawSelectedLecture(selectedLecture: LectureDto) {
    for (classTime in selectedLecture.class_time_json) {
        selectedLecture.color.bgRaw
        DrawClassTime(
            classTime,
            selectedLecture.course_title,
            -0x1f1f20,
            -0xcccccd
        )
    }
}

@Composable
private fun ChangeTitleDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    oldTitle: String
) {
    var text by remember { mutableStateOf(oldTitle) }
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(text) },
        title = stringResource(R.string.home_drawer_change_name_dialog_title)
    ) {
        EditText(value = text, onValueChange = { text = it })
    }
}

object Defaults {
    val defaultTableDto = TableDto(
        id = "",
        year = 2023,
        semester = 1,
        title = "",
        lectureList = emptyList(),
        updatedAt = "default",
        totalCredit = null,
        theme = TimetableColorTheme.SNUTT
    )
    val defaultSimpleTableDto = SimpleTableDto(
        id = "",
        year = 2022,
        semester = 1L,
        title = "",
        updatedAt = "",
        totalCredit = null
    )
    val defaultLectureDto = LectureDto(
        id = "",
        course_title = "",
        instructor = "",
        colorIndex = 0L,
        color = ColorDto(),
        department = null,
        academic_year = null,
        credit = 0,
        category = null,
        classification = null,
        course_number = null,
        lecture_number = null,
        remark = "",
        class_time_json = listOf(),
        class_time_mask = listOf()
    )
}

private fun getTextHeight(text: String, paint: Paint): Float {
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    return bounds.height().toFloat()
}

@Preview(showBackground = true)
@Composable
fun TimetablePagePreview() {
    CompositionLocalProvider(
        NavControllerContext provides rememberNavController(),
        HomeDrawerStateContext provides rememberDrawerState(initialValue = DrawerValue.Closed)
    ) {
        TimetablePage(
            uncheckedNotification = false
        )
    }
}
