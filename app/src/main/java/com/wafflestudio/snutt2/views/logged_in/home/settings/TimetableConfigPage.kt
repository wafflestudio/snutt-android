package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TimetableConfigPage() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<UserViewModel>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val trimParam by viewModel.trimParam.collectAsState()
    val compactMode by viewModel.compactMode.collectAsState()

    val table by timetableViewModel.currentTable.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val tableState =
        TableState(table ?: TableDto.Default, trimParam, previewTheme)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.Gray100)
            .verticalScroll(rememberScrollState()),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.timetable_settings_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() },
        )
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_timetable_config_force_fit),
            hasNextPage = false,
            onClick = {
                scope.launch {
                    viewModel.setAutoTrim(trimParam.forceFitLectures.not())
                }
            },
        ) {
            PoorSwitch(state = trimParam.forceFitLectures)
        }
        Margin(height = 10.dp)
        AnimatedVisibility(visible = trimParam.forceFitLectures.not()) {
            Column {
                RangeBarCell(title = stringResource(R.string.settings_timetable_config_week_day)) {
                    RangeBar(
                        initStart = trimParam.dayOfWeekFrom,
                        initEnd = trimParam.dayOfWeekTo,
                        labelArray = stringArrayResource(R.array.week_days),
                    ) { start, end ->
                        scope.launch {
                            viewModel.setDayOfWeekRange(start, end)
                        }
                    }
                }
                Margin(height = 10.dp)
                RangeBarCell(title = stringResource(R.string.settings_timetable_config_time)) {
                    RangeBar(
                        initStart = trimParam.hourFrom,
                        initEnd = trimParam.hourTo,
                        labelArray = Array(24) { it.toString() },
                    ) { start, end ->
                        scope.launch {
                            viewModel.setHourRange(start, end)
                        }
                    }
                }
                Margin(height = 10.dp)
            }
        }
        SettingItem(
            title = stringResource(R.string.settings_compact_mode),
            hasNextPage = false,
            onClick = {
                scope.launch {
                    viewModel.setCompactMode(compactMode.not())
                }
            },
        ) {
            PoorSwitch(state = compactMode)
        }
        Row(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 20.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (compactMode) {
                Text(
                    text = stringResource(R.string.settings_compact_mode_message),
                    style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                )
            }
        }
        Margin(height = 10.dp)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5))
                .background(SNUTTColors.White900)
                .padding(5.dp)
                .size(
                    (LocalConfiguration.current.screenWidthDp * 0.8).dp,
                    (LocalConfiguration.current.screenHeightDp * 0.6).dp,
                )
                .align(Alignment.CenterHorizontally),
        ) {
            CompositionLocalProvider(LocalTableState provides tableState) {
                TimeTable(selectedLecture = null, touchEnabled = false)
            }
        }
        Margin(height = 25.dp)
    }
}

@Composable
fun PoorSwitch(state: Boolean) {
    val switchOffset by animateDpAsState(
        targetValue = if (state) 10.dp else 30.dp,
    )

    Box(
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(80))
                    .background(Color.Gray)
                    .zIndex(1f),
            ) {}
            Spacer(modifier = Modifier.width(20.dp))
        }
        Row {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (state) {
                            SNUTTColors.Black600
                        } else {
                            Color.LightGray
                        },
                    )
                    .zIndex(5f),
            )
            Spacer(modifier = Modifier.width(switchOffset))
        }
    }
}

@Composable
private fun RangeBarCell(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(horizontal = 35.dp, vertical = 10.dp),
    ) {
        Text(text = title, style = SNUTTTypography.body1)
        Box(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Composable
private fun RangeBar(
    initStart: Int,
    initEnd: Int,
    labelArray: Array<String>,
    onChange: (Int, Int) -> Unit,
) {
    val localDensity = LocalDensity.current

    val tickNum = labelArray.size - 1
    val widthPx = with(localDensity) {
        (LocalConfiguration.current.screenWidthDp - 70).dp.toPx()
    }

    val tickPx: Float = widthPx / tickNum
    val barStart = remember { Animatable(initStart * tickPx) }
    val startTick = (barStart.value / tickPx).roundToInt()
    val barEnd = remember { Animatable(initEnd * tickPx) }
    val endTick = (barEnd.value / tickPx).roundToInt()
    val Black = SNUTTColors.Black600

    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val lineOffset = size.height - 30.dp.toPx()

        drawLine(
            color = Color.LightGray,
            start = Offset(x = 0f, y = lineOffset),
            end = Offset(x = size.width, y = lineOffset),
            strokeWidth = 1.dp.toPx(),
        )
        for (tick in 0..tickNum) {
            drawCircle(Black, (1.5).dp.toPx(), Offset(x = tickPx * tick, y = lineOffset))
        }
        drawLine(
            color = Black,
            start = Offset(x = barStart.value, y = lineOffset),
            end = Offset(x = barEnd.value, y = lineOffset),
            strokeWidth = 3.dp.toPx(),
        )
        drawCircle(
            color = Black,
            radius = 6.dp.toPx(),
            center = Offset(x = barStart.value, y = lineOffset),
        )
        drawCircle(
            color = Black,
            radius = 6.dp.toPx(),
            center = Offset(x = barEnd.value, y = lineOffset),
        )
    }
    Label(
        offset = barStart, widthPx = widthPx, tickPx = tickPx, labelText = labelArray[startTick],
    ) {
        onChange(min(startTick, endTick), max(startTick, endTick))
    }
    Label(offset = barEnd, widthPx = widthPx, tickPx = tickPx, labelText = labelArray[endTick]) {
        onChange(min(startTick, endTick), max(startTick, endTick))
    }
}

@Composable
private fun Label(
    offset: Animatable<Float, AnimationVector1D>,
    widthPx: Float,
    tickPx: Float,
    labelText: String,
    onChange: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (offset.value - 13.dp.toPx()).roundToInt(),
                    5.dp
                        .toPx()
                        .roundToInt(),
                )
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        offset.snapTo(
                            (offset.value + delta).coerceIn(0f, widthPx),
                        )
                    }
                },
                onDragStopped = {
                    offset.animateTo(
                        (offset.value / tickPx).roundToInt() * tickPx,
                    )
                    onChange()
                },
            )
            .clip(CircleShape)
            .width(26.dp)
            .height(26.dp)
            .background(SNUTTColors.Black600)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = labelText, color = SNUTTColors.White900)
    }
}

@Preview
@Composable
fun TimetableConfigPagePreview() {
    TimetableConfigPage()
}
