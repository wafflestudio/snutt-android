package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CircularPicker
import com.wafflestudio.snutt2.components.compose.Picker
import com.wafflestudio.snutt2.components.compose.RoundBorderButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalModalState

@Composable
fun DayTimePickerSheet(
    classTime: ClassTimeDto,
    onDismiss: () -> Unit,
    onConfirm: (ClassTimeDto) -> Unit,
) {
    val modalState = LocalModalState.current
    val context = LocalContext.current
    val dayList = remember {
        context.resources.getStringArray(R.array.week_days).map {
            it + context.getString(R.string.settings_timetable_config_week_day)
        }
    }
    var dayIndex by remember { mutableStateOf(classTime.day) }

    val amPmList = remember { listOf(context.getString(R.string.morning), context.getString(R.string.afternoon)) }
    val hourList = remember { List(12) { if (it == 0) "12" else it.toString() } }
    val minuteList = remember { List(12) { "%02d".format(it * 5) } }

    var startTime: Time12 by remember { mutableStateOf(classTime.startTime12()) }
    var endTime: Time12 by remember { mutableStateOf(classTime.endTime12()) }

    var editingStartTime by remember { mutableStateOf(false) }
    var editingEndTime by remember { mutableStateOf(false) }

    /* 시작 시간이 끝나는 시간보다 같거나 더 나중일 때, 경계값 신경써서 조정하는 함수 */
    val checkBoundary = {
        if (startTime >= endTime) {
            // 시작 시간을 끝나는 시간보다 나중으로 수정했으면, 끝나는 시간을 5분 뒤로 설정
            if (editingStartTime) {
                if (startTime.isLast()) {
                    startTime = startTime.prev()
                    endTime = startTime.next()
                } else endTime = startTime.next()
                // 끝나는 시간을 시작 시간보다 앞서게 수정했으면, 시작 시간을 5분 앞으로 설정
            } else if (editingEndTime) {
                if (endTime.isFirst()) {
                    endTime = endTime.next()
                    startTime = endTime.prev()
                } else startTime = endTime.prev()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(15.dp)
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks { onDismiss() },
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onConfirm(
                        classTime.copy(
                            day = dayIndex,
                            start_time = startTime.toString24(),
                            end_time = endTime.toString24(),
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.settings_timetable_config_week_day), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempDayIndex by mutableStateOf(dayIndex)
                    modalState
                        .set(
                            onDismiss = {
                                dayIndex = tempDayIndex
                                modalState.hide()
                            },
                            width = 150.dp,
                        ) {
                            Picker(
                                list = dayList,
                                initialCenterIndex = dayIndex,
                                columnHeightDp = 45.dp,
                                onValueChanged = { tempDayIndex = it }
                            ) {
                                Text(
                                    text = dayList[it],
                                    style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                )
                            }
                        }.show()
                },
            ) {
                Text(text = dayList[dayIndex], style = SNUTTTypography.button)
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_start_time_label), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempStartTime by mutableStateOf(startTime.copy())
                    editingStartTime = true
                    modalState
                        .set(
                            onDismiss = {
                                startTime = tempStartTime
                                checkBoundary()
                                editingStartTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = startTime.amPm,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(amPm = it)
                                        }
                                    ) {
                                        Text(
                                            text = amPmList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = hourList,
                                        initialCenterIndex = startTime.hour,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(hour = it)
                                        }
                                    ) {
                                        Text(
                                            text = hourList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = minuteList,
                                        initialCenterIndex = startTime.minute / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartTime = tempStartTime.copy(minute = it * 5)
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            ) {
                Text(
                    text = startTime.toString(),
                    style = SNUTTTypography.button
                )
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_end_time_label), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
                onClick = {
                    var tempEndTime by mutableStateOf(endTime.copy())
                    editingEndTime = true
                    modalState
                        .set(
                            onDismiss = {
                                endTime = tempEndTime
                                checkBoundary()
                                editingEndTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = endTime.amPm,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(amPm = it)
                                        }
                                    ) {
                                        Text(
                                            text = amPmList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = hourList,
                                        initialCenterIndex = endTime.hour,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(hour = it)
                                        }
                                    ) {
                                        Text(
                                            text = hourList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = minuteList,
                                        initialCenterIndex = endTime.minute / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndTime = tempEndTime.copy(minute = it * 5)
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            ) {
                Text(
                    text = endTime.toString(),
                    style = SNUTTTypography.button
                )
            }
        }
    }
}
