package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.TextRect
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.lib.network.dto.core.*
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.lib.rx.sp
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.TableContext
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModelNew
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModelNew
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

@Stable
object CanvasPalette {
    val hourLabelWidth @Composable get() = 24.5f.dp(LocalContext.current)
    val dayLabelHeight @Composable get() = 28.5f.dp(LocalContext.current)
    val cellPadding @Composable get() = 4.dp(LocalContext.current)

    val dayLabelTextPaint
        @Composable get() =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (isDarkMode()) Color.argb(180, 119, 119, 119)
                    else Color.argb(180, 0, 0, 0)
                textSize = 12.sp(LocalContext.current)
                textAlign = Paint.Align.CENTER
                typeface = LocalContext.current.resources.getFont(R.font.spoqa_han_sans_light)
            }
    val dayLabelTextHeight: Float
        @Composable get() {
            val text = "월"
            val bounds = Rect()
            dayLabelTextPaint.getTextBounds(text, 0, text.length, bounds)
            return bounds.height().toFloat()
        }

    val hourLabelTextPaint
        @Composable get() =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (isDarkMode()) Color.argb(180, 119, 119, 119)
                    else Color.argb(180, 0, 0, 0)
                textSize = 12.sp(LocalContext.current)
                textAlign = Paint.Align.RIGHT
                typeface = LocalContext.current.resources.getFont(R.font.spoqa_han_sans_light)
            }

    val lectureCellTextRect
        @Composable get() =
            TextRect(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 10f.sp(LocalContext.current)
                    typeface = LocalContext.current.resources.getFont(R.font.spoqa_han_sans_regular)
                }
            )

    val lectureCellSubTextRect
        @Composable get() =
            TextRect(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 11f.sp(LocalContext.current)
                    typeface = LocalContext.current.resources.getFont(R.font.spoqa_han_sans_bold)
                }
            )

    val lectureCellBorderPaint: Paint =
        Paint().apply {
            style = Paint.Style.STROKE
            color = 0x0d000000
            strokeWidth = 1.dp.value
        }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimetablePage() {
    val context = LocalContext.current
    val view = LocalView.current
    var timetableHeight by remember { mutableStateOf(0) }
    var topBarHeight by remember { mutableStateOf(0) }

    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val tableListViewModel = hiltViewModel<TableListViewModelNew>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val keyboardManager = LocalSoftwareKeyboardController.current
    val table = TableContext.current.table
    val scope = rememberCoroutineScope()
    val newSemesterNotify by tableListViewModel.newSemesterNotify.collectAsState(false)
    val firstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()
    var changeTitleDialogState by remember {
        mutableStateOf(false)
    }
    if (changeTitleDialogState) {
        ChangeTitleDialog(
            onDismiss = {
                changeTitleDialogState = false
                keyboardManager?.hide()
            }, onConfirm = { newTitle ->
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    tableListViewModel.changeNameTableNew(
                        tableId = table.id,
                        name = newTitle
                    )
                    changeTitleDialogState = false
                    keyboardManager?.hide()
                }
            }
        }, oldTitle = table.title
        )
    }

    Column(Modifier.background(SNUTTColors.White900)) {
        TopBar(
            title = {
                val creditText = stringResource(
                    R.string.timetable_credit,
                    getCreditSumFromLectureList(table.lectureList)
                )
                Text(
                    text = table.title,
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clicks { changeTitleDialogState = true }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = creditText,
                    style = SNUTTTypography.body2,
                    color = SNUTTColors.Gray200
                )
            },
            navigationIcon = {
                IconWithAlertDot(newSemesterNotify) { centerAlignedModifier ->
                    DrawerIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { scope.launch { drawerState.open() } },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }
            },
            actions = {
                LectureListIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.LecturesOfTable) },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Spacer(modifier = Modifier.width(8.dp))
                ShareIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            shareScreenshotFromView(view, context, topBarHeight, timetableHeight)
                        },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconWithAlertDot(firstBookmarkAlert) { centerAlignedModifier ->
                    BookmarkPageIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { navController.navigate(NavigationDestination.Bookmark) { launchSingleTop = true } },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            },
            modifier = Modifier.onGloballyPositioned {
                topBarHeight = it.size.height
            }
        ) // top bar 높이 측정
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { timetableHeight = it.size.height } // timetable 높이 측정
        ) {
            TimeTable(selectedLecture = null)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeTable(
    touchEnabled: Boolean = true,
    selectedLecture: LectureDto?,
) {
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModelNew>()

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val navigator = LocalNavController.current
    val lectures = TableContext.current.table.lectureList
    val hourLabelWidth = CanvasPalette.hourLabelWidth
    val dayLabelHeight = CanvasPalette.dayLabelHeight

    val trimParam = TableContext.current.trimParam
    val fittedTrimParam =
        if (trimParam.forceFitLectures) {
            (selectedLecture?.let { lectures + it } ?: lectures).getFittingTrimParam(
                TableTrimParam.Default
            )
        } else trimParam

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if (touchEnabled && navigator.backQueue.size == 2) { // FIXME: 다른 방법으로 대응해야...
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
                                    lectureDetailViewModel.initializeEditingLectureDetail(lecture)
                                    navigator.navigate(NavigationDestination.LectureDetail) {
                                        launchSingleTop = true
                                    }
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

    DrawTableGrid(fittedTrimParam)
    lectures.forEach { lecture ->
        lecture.class_time_json
            .mapNotNull {
                it.trimByTrimParam(fittedTrimParam)
            }
            .forEach { classTime ->
                DrawLecture(lecture, classTime, fittedTrimParam)
            }
    }
    selectedLecture?.let {
        DrawSelectedLecture(it, fittedTrimParam)
    }
}

@Composable
private fun DrawTableGrid(fittedTrimParam: TableTrimParam) {
    val context = LocalContext.current
    val hourLabelWidth = CanvasPalette.hourLabelWidth
    val dayLabelHeight = CanvasPalette.dayLabelHeight
    val dayLabelTextPaint = CanvasPalette.dayLabelTextPaint
    val hourLabelTextPaint = CanvasPalette.hourLabelTextPaint
    val textHeight = CanvasPalette.dayLabelTextHeight

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
                strokeWidth = (0.5f).dp(context)
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
                color = gridColor,
                strokeWidth = (0.5f).dp(context)
            )
            drawLine(
                start = Offset(x = hourLabelWidth, y = startHeight + (unitHeight * 0.5f)),
                end = Offset(x = size.width, y = startHeight + (unitHeight * 0.5f)),
                color = gridColor2,
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
private fun DrawLecture(
    lecture: LectureDto,
    classTime: ClassTimeDto,
    fittedTrimParam: TableTrimParam
) {
    val context = LocalContext.current
    val theme = TableContext.current.previewTheme ?: TableContext.current.table.theme

    DrawClassTime(
        fittedTrimParam = fittedTrimParam,
        classTime = classTime,
        courseTitle = lecture.course_title,
        bgColor =
        if (lecture.colorIndex == 0L && lecture.color.bgColor != null) lecture.color.bgColor!!
        else theme.getColorByIndexComposable(
            lecture.colorIndex
        ).toArgb(),
        fgColor = if (lecture.colorIndex == 0L && lecture.color.fgColor != null) lecture.color.fgColor!! else context.getColor(
            R.color.white
        ),
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
    val hourLabelWidth = CanvasPalette.hourLabelWidth
    val dayLabelHeight = CanvasPalette.dayLabelHeight
    val cellPadding = CanvasPalette.cellPadding
    val lectureCellTextRect = CanvasPalette.lectureCellTextRect
    val lectureCellSubTextRect = CanvasPalette.lectureCellSubTextRect
    val lectureCellBorderPaint = CanvasPalette.lectureCellBorderPaint
    val compactMode = LocalCompactState.current

    val dayOffset = classTime.day - fittedTrimParam.dayOfWeekFrom
    val hourRangeOffset =
        Pair(
            max(classTime.startTimeInFloat - fittedTrimParam.hourFrom, 0f),
            min(
                classTime.endTimeInFloat.let { if (isCustom.not() && compactMode) roundToCompact(it) else it } - fittedTrimParam.hourFrom,
                fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1
            )
        )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (size.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (size.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val left = hourLabelWidth + (dayOffset) * unitWidth
        val right = hourLabelWidth + (dayOffset) * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom = dayLabelHeight + (hourRangeOffset.second) * unitHeight

        val rect = RectF(left, top, right, bottom)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRect(rect, Paint().apply { color = bgColor })
            canvas.nativeCanvas.drawRect(
                rect,
                lectureCellBorderPaint,
            )
        }

        val cellHeight = bottom - top - cellPadding * 2
        val cellWidth = right - left - cellPadding * 2

        val courseTitleHeight = lectureCellTextRect.prepare(
            courseTitle, cellWidth.toInt(), cellHeight.toInt()
        )
        val locationHeight = lectureCellSubTextRect.prepare(
            classTime.place, cellWidth.toInt(), cellHeight.toInt() - courseTitleHeight
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
private fun DrawSelectedLecture(selectedLecture: LectureDto, fittedTrimParam: TableTrimParam) {
    for (classTime in selectedLecture.class_time_json) {
        DrawClassTime(
            fittedTrimParam, classTime, selectedLecture.course_title, -0x1f1f20, -0xcccccd
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

private fun bitmapToUri(image: Bitmap, context: Context): Uri {
    val imagesFolder = File(FacebookSdk.getCacheDir(), "images")
    imagesFolder.mkdirs()
    val file = File(imagesFolder, "shared_image.png")
    val stream = FileOutputStream(file)
    image.compress(Bitmap.CompressFormat.PNG, 90, stream)
    stream.flush()
    stream.close()
    return FileProvider.getUriForFile(
        context,
        context.getString(R.string.file_provider_authorities),
        file
    )
}

private fun shareScreenshotFromView(
    view: View,
    context: Context,
    topBarHeight: Int,
    timetableHeight: Int
) {
    val bitmap =
        Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    // FIXME: 이 방법의 문제점 -> 위아래를 잘라내야 한다.
    val bitmapResized = Bitmap.createBitmap(bitmap, 0, topBarHeight, bitmap.width, timetableHeight)
    val uri = bitmapToUri(bitmapResized, context)
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
    }
    context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
}

@Preview(showBackground = true)
@Composable
fun TimetablePagePreview() {
    TimetablePage()
}
