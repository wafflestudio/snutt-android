package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CircularPicker
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.Picker
import com.wafflestudio.snutt2.components.compose.RoundBorderButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.toFormattedTimeString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import java.time.DayOfWeek
import java.time.LocalTime

private sealed interface PickerDialogType {
    data object None : PickerDialogType
    data object Day : PickerDialogType
    data object StartTime : PickerDialogType
    data object EndTime : PickerDialogType
}

@Composable
fun DayTimePickerSheetContent(
    session: LectureSession,
    onDismiss: () -> Unit,
    onConfirm: (LectureSession) -> Unit,
) {
    val dayList = rememberDayList()

    var dayIndex by remember(session) { mutableIntStateOf(session.day.value - 1) }
    var startMinute by remember(session) { mutableIntStateOf(session.startTime.hour * 60 + session.startTime.minute) }
    var endMinute by remember(session) { mutableIntStateOf(session.endTime.hour * 60 + session.endTime.minute) }

    var pickerDialog by remember { mutableStateOf<PickerDialogType>(PickerDialogType.None) }

    // Sub-picker dialogs
    when (pickerDialog) {
        PickerDialogType.None -> {}

        PickerDialogType.Day -> {
            DayPickerDialog(
                dayList = dayList,
                initialDayIndex = dayIndex,
                onSelected = { selected ->
                    dayIndex = selected
                    pickerDialog = PickerDialogType.None
                },
            )
        }

        PickerDialogType.StartTime -> {
            TimePickerDialog(
                initialMinute = startMinute,
                onSelected = { selected ->
                    startMinute = selected
                    if (startMinute >= endMinute) {
                        if (startMinute == SearchTime.LAST_MINUTE) {
                            startMinute = SearchTime.LAST_MINUTE - 5
                            endMinute = SearchTime.LAST_MINUTE
                        } else {
                            endMinute = startMinute + 5
                        }
                    }
                    pickerDialog = PickerDialogType.None
                },
            )
        }

        PickerDialogType.EndTime -> {
            TimePickerDialog(
                initialMinute = endMinute,
                onSelected = { selected ->
                    endMinute = selected
                    if (startMinute >= endMinute) {
                        if (endMinute == SearchTime.FIRST_MINUTE) {
                            startMinute = SearchTime.FIRST_MINUTE
                            endMinute = SearchTime.FIRST_MINUTE + 5
                        } else {
                            startMinute = endMinute - 5
                        }
                    }
                    pickerDialog = PickerDialogType.None
                },
            )
        }
    }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(15.dp),
    ) {
        SheetHeader(
            onCancel = onDismiss,
            onConfirm = {
                onConfirm(
                    session.copy(
                        day = DayOfWeek.of(dayIndex + 1),
                        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
                        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
                    ),
                )
            },
        )
        Spacer(modifier = Modifier.height(30.dp))
        LabeledValueRow(
            label = stringResource(R.string.settings_timetable_config_week_day),
            value = dayList[dayIndex],
            onClick = { pickerDialog = PickerDialogType.Day },
        )
        Divider(color = SNUTTColors.Black250)
        LabeledValueRow(
            label = stringResource(R.string.lecture_detail_edit_class_time_sheet_start_time_label),
            value = startMinute.toFormattedTimeString(LocalContext.current),
            onClick = { pickerDialog = PickerDialogType.StartTime },
        )
        Divider(color = SNUTTColors.Black250)
        LabeledValueRow(
            label = stringResource(R.string.lecture_detail_edit_class_time_sheet_end_time_label),
            value = endMinute.toFormattedTimeString(context = LocalContext.current),
            onClick = { pickerDialog = PickerDialogType.EndTime },
        )
    }
}

// --- Header ---

@Composable
private fun SheetHeader(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(modifier = Modifier.padding(5.dp)) {
        Text(
            text = stringResource(R.string.common_cancel),
            style = SNUTTTypography.body1,
            modifier = Modifier.clicks { onCancel() },
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.common_ok),
            style = SNUTTTypography.body1,
            modifier = Modifier.clicks { onConfirm() },
        )
    }
}

// --- Rows ---

@Composable
private fun LabeledValueRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 7.dp)
            .fillMaxWidth()
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = SNUTTTypography.button)
        Spacer(modifier = Modifier.weight(1f))
        RoundBorderButton(color = SNUTTColors.Gray400) {
            Text(text = value, style = SNUTTTypography.button)
        }
    }
}

// --- Dialogs ---

@Composable
private fun DayPickerDialog(
    dayList: List<String>,
    initialDayIndex: Int,
    onSelected: (Int) -> Unit,
) {
    var tempIndex by remember { mutableIntStateOf(initialDayIndex) }

    CustomDialog(
        onDismiss = { onSelected(tempIndex) },
        onConfirm = { onSelected(tempIndex) },
        positiveButtonText = null,
        negativeButtonText = null,
        width = 150.dp,
    ) {
        Picker(
            list = dayList,
            initialCenterIndex = initialDayIndex,
            columnHeightDp = 45.dp,
            onValueChanged = { tempIndex = it },
        ) {
            Text(
                text = dayList[it].tempBlank(it),
                style = SNUTTTypography.button.copy(fontSize = 24.sp),
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    initialMinute: Int,
    onSelected: (Int) -> Unit,
) {
    val amPmList = listOf(stringResource(R.string.morning), stringResource(R.string.afternoon))
    val hourList = remember { List(12) { if (it == 0) "12" else it.toString() } }
    val minuteList = remember { List(12) { "%02d".format(it * 5) } }

    var tempMinute by remember { mutableIntStateOf(initialMinute) }

    CustomDialog(
        onDismiss = { onSelected(tempMinute) },
        onConfirm = { onSelected(tempMinute) },
        positiveButtonText = null,
        negativeButtonText = null,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                Picker(
                    list = amPmList,
                    initialCenterIndex = if (initialMinute < SearchTime.MIDDAY_MINUTE) 0 else 1,
                    columnHeightDp = 45.dp,
                    onValueChanged = {
                        tempMinute = (tempMinute % SearchTime.MIDDAY_MINUTE) + SearchTime.MIDDAY_MINUTE * it
                    },
                ) {
                    Text(
                        text = amPmList[it].tempBlank(it),
                        style = SNUTTTypography.button.copy(fontSize = 24.sp),
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                CircularPicker(
                    list = hourList,
                    initialCenterIndex = initialMinute / 60,
                    columnHeightDp = 45.dp,
                    onValueChanged = {
                        tempMinute = it * 60 + tempMinute % 60 + if (tempMinute < SearchTime.MIDDAY_MINUTE) 0 else SearchTime.MIDDAY_MINUTE
                    },
                ) {
                    Text(
                        text = hourList[it].tempBlank(it),
                        style = SNUTTTypography.button.copy(fontSize = 24.sp),
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                CircularPicker(
                    list = minuteList,
                    initialCenterIndex = (initialMinute % 60) / 5,
                    columnHeightDp = 45.dp,
                    onValueChanged = {
                        tempMinute = (tempMinute / 60) * 60 + it * 5
                    },
                ) {
                    Text(
                        text = minuteList[it].tempBlank(it),
                        style = SNUTTTypography.button.copy(fontSize = 24.sp),
                    )
                }
            }
        }
    }
}

// --- Helpers ---

@Composable
private fun rememberDayList(): List<String> {
    val weekDays = stringArrayResource(R.array.week_days)
    val weekDaySuffix = stringResource(R.string.settings_timetable_config_week_day)
    return remember(weekDays, weekDaySuffix) { weekDays.map { it + weekDaySuffix } }
}

/* FIXME
 * Picker의 인접한 item끼리 Text에 들어갈 String의 길이가 같으면 드래그할 때 글리치가 생긴다. (원인 불명)
 * 길이가 다르면 문제가 없다. 임시 대처용 함수
 */
private fun String.tempBlank(a: Int): String {
    return if (a % 2 == 0) this else " $this "
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
private fun DayTimePickerSheetContentPreview() {
    DayTimePickerSheetContent(
        session = LectureSession.Default,
        onDismiss = {},
        onConfirm = {},
    )
}
