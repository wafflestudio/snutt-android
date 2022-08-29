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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TimetableConfigPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
//    val viewModel = hiltViewModel<UserViewModel>()
//    val trimParam = viewModel.trimParam.collectAsState()

    var temp by remember { mutableStateOf(false) }

    var startDay by remember { mutableStateOf(0) }
    var endDay by remember { mutableStateOf(4) }

    var startTime by remember { mutableStateOf(0) }
    var endTime by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.Gray100) // TODO: Color
    ) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.timetable_settings_app_bar_title)) },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            navController.popBackStack()
                        }
                )
            }
        )
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_timetable_config_force_fit),
            modifier = Modifier.background(Color.White),
            onClick = {
                temp = temp.not()
//                scope.launch {
//                    viewModel.setAutoTrim(trimParam.value.forceFitLectures.not())
//                }
            },
            content = {
                PoorSwitch(state = temp)
            }
        )
        Margin(height = 10.dp)
        AnimatedVisibility(visible = temp) {
            Column {
                RangeBarCell(title = stringResource(R.string.settings_timetable_config_week_day)) {
                    RangeBar(
                        initStart = startDay,
                        initEnd = endDay,
                        labelArray = stringArrayResource(R.array.week_days)
                    ) { start, end ->
                        startDay = start
                        endDay = end
                    }
                }
                Margin(height = 10.dp)
                RangeBarCell(title = stringResource(R.string.settings_timetable_config_time)) {
                    RangeBar(
                        initStart = startTime,
                        initEnd = endTime,
                        labelArray = Array(16) { (it + 8).toString() }
                    ) { start, end ->
                        startTime = start
                        endTime = end
                    }
                }
            }
        }
    }
}

@Composable
private fun PoorSwitch(state: Boolean) {
    val switchOffset by animateDpAsState(
        targetValue = if (state) 10.dp else 30.dp
    )

    Box(
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(80))
                    .background(Color.Gray)
                    .zIndex(1f)
            ) {}
            Spacer(modifier = Modifier.width(20.dp))
        }
        Row {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (state) Color.Black
                        else Color.LightGray
                    )
                    .zIndex(5f)
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
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = title, modifier = Modifier.weight(18f))
            Spacer(modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                content()
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}

@Composable
private fun RangeBar(
    initStart: Int,
    initEnd: Int,
    labelArray: Array<String>,
    onChange: (Int, Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val localDensity = LocalDensity.current

    val tickNum = labelArray.size - 1
    val widthPx = with(localDensity) {
        (LocalConfiguration.current.screenWidthDp - 60).dp.toPx()
    }

    val tickPx: Float = widthPx / tickNum
    val barStart = remember { Animatable(initStart * tickPx) }
    val startTick = (barStart.value / tickPx).roundToInt()
    val barEnd = remember { Animatable(initEnd * tickPx) }
    val endTick = (barEnd.value / tickPx).roundToInt()

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val lineOffset = size.height - 30.dp.toPx()

        drawLine(
            color = Color.LightGray,
            start = Offset(x = 0f, y = lineOffset),
            end = Offset(x = size.width, y = lineOffset),
            strokeWidth = 1.dp.toPx()
        )
        for (tick in 0..tickNum) {
            drawCircle(Color.Black, (1.5).dp.toPx(), Offset(x = tickPx * tick, y = lineOffset))
        }
        drawLine(
            color = Color.Black,
            start = Offset(x = barStart.value, y = lineOffset),
            end = Offset(x = barEnd.value, y = lineOffset),
            strokeWidth = 3.dp.toPx()
        )
        drawCircle(
            color = Color.Black,
            radius = 6.dp.toPx(),
            center = Offset(x = barStart.value, y = lineOffset)
        )
        drawCircle(
            color = Color.Black,
            radius = 6.dp.toPx(),
            center = Offset(x = barEnd.value, y = lineOffset)
        )
    }
    Label(
        offset = barStart,
        widthPx = widthPx,
        tickPx = tickPx,
        labelText = labelArray[startTick]
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
    onChange: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (offset.value - 13.dp.toPx()).roundToInt(),
                    5.dp
                        .toPx()
                        .roundToInt()
                )
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        offset.snapTo(
                            (offset.value + delta).coerceIn(0f, widthPx)
                        )
                    }
                },
                onDragStopped = {
                    offset.animateTo(
                        (offset.value / tickPx).roundToInt() * tickPx
                    )
                    onChange()
                }
            )
            .clip(CircleShape)
            .width(26.dp)
            .height(26.dp)
            .background(Color.Black)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = labelText, color = Color.White)
    }
}

@Preview
@Composable
fun TimetableConfigPagePreview() {
    TimetableConfigPage()
}
