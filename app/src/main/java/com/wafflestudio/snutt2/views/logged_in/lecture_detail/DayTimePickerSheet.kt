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
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.model.LectureTime
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalModalState

@Composable
fun DayTimePickerSheet(
    bottomSheet: BottomSheet,
    classTime: ClassTimeDto,
    onDismiss: () -> Unit,
    onConfirm: (ClassTimeDto) -> Unit,
) {
    val modalState = LocalModalState.current
    val context = LocalContext.current
    val dayList = remember { context.resources.getStringArray(R.array.week_days).map { it + context.getString(R.string.settings_timetable_config_week_day) } }
    val amPmList = remember { listOf(context.getString(R.string.morning), context.getString(R.string.afternoon)) }
    val hourList = remember { List(12) { if (it == 0) "12" else it.toString() } }
    val minuteList = remember { List(12) { "%02d".format(it * 5) } }

    var dayIndex by remember(classTime, bottomSheet.isVisible) { mutableStateOf(classTime.day) }
    var startMinute by remember(classTime, bottomSheet.isVisible) { mutableStateOf(classTime.startMinute) }
    var endMinute by remember(classTime, bottomSheet.isVisible) { mutableStateOf(classTime.endMinute) }

    var editingStartTime by remember { mutableStateOf(false) }
    var editingEndTime by remember { mutableStateOf(false) }

    /* 시작 시간이 끝나는 시간보다 같거나 더 나중일 때, 경계값 신경써서 조정하는 함수 */
    val checkBoundary = {
        if (startMinute >= endMinute) {
            // 시작 시간을 끝나는 시간보다 나중으로 수정했으면, 끝나는 시간을 5분 뒤로 설정
            if (editingStartTime) {
                if (startMinute == LectureTime.LAST) {
                    startMinute = LectureTime.LAST - 5
                    endMinute = LectureTime.LAST
                } else endMinute = startMinute + 5
                // 끝나는 시간을 시작 시간보다 앞서게 수정했으면, 시작 시간을 5분 앞으로 설정
            } else if (editingEndTime) {
                if (endMinute == LectureTime.FIRST) {
                    startMinute = LectureTime.FIRST
                    endMinute = LectureTime.FIRST + 5
                } else startMinute = endMinute - 5
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
                            startMinute = startMinute,
                            endMinute = endMinute,
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth()
                .clicks {
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
                                    text = dayList[it].tempBlank(it),
                                    style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                )
                            }
                        }.show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.settings_timetable_config_week_day), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
            ) {
                Text(text = dayList[dayIndex], style = SNUTTTypography.button)
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth()
                .clicks {
                    var tempStartMinute by mutableStateOf(startMinute)
                    editingStartTime = true
                    modalState
                        .set(
                            onDismiss = {
                                startMinute = tempStartMinute
                                checkBoundary()
                                editingStartTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = if (startMinute < LectureTime.MIDDAY) 0 else 1,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartMinute = (tempStartMinute % LectureTime.MIDDAY) + LectureTime.MIDDAY * it
                                        }
                                    ) {
                                        Text(
                                            text = amPmList[it].tempBlank(it),
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = hourList,
                                        initialCenterIndex = startMinute / 60,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartMinute = it * 60 + tempStartMinute % 60 + if (tempStartMinute < LectureTime.MIDDAY) 0 else LectureTime.MIDDAY
                                        }
                                    ) {
                                        Text(
                                            text = hourList[it].tempBlank(it),
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    CircularPicker(
                                        list = minuteList,
                                        initialCenterIndex = (startMinute % 60) / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempStartMinute = (tempStartMinute / 60) * 60 + it * 5
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it].tempBlank(it),
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp)
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_start_time_label), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
            ) {
                Text(
                    text = startMinute.toFormattedTimeString(),
                    style = SNUTTTypography.button
                )
            }
        }
        Divider(color = SNUTTColors.Black250)
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth()
                .clicks {
                    var tempEndMinute by mutableStateOf(endMinute)
                    editingEndTime = true
                    modalState
                        .set(
                            onDismiss = {
                                endMinute = tempEndMinute
                                checkBoundary()
                                editingEndTime = false
                                modalState.hide()
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Picker(
                                        list = amPmList,
                                        initialCenterIndex = if (endMinute < LectureTime.MIDDAY) 0 else 1,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndMinute = tempEndMinute % LectureTime.MIDDAY + LectureTime.MIDDAY * it
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
                                        initialCenterIndex = endMinute / 60,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndMinute = it * 60 + tempEndMinute % 60 + if (tempEndMinute < LectureTime.MIDDAY) 0 else LectureTime.MIDDAY
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
                                        initialCenterIndex = (endMinute % 60) / 5,
                                        columnHeightDp = 45.dp,
                                        onValueChanged = {
                                            tempEndMinute = (tempEndMinute / 60) * 60 + it * 5
                                        }
                                    ) {
                                        Text(
                                            text = minuteList[it],
                                            style = SNUTTTypography.button.copy(fontSize = 24.sp),
                                        )
                                    }
                                }
                            }
                        }.show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.lecture_detail_edit_class_time_sheet_end_time_label), style = SNUTTTypography.button)
            Spacer(modifier = Modifier.weight(1f))
            RoundBorderButton(
                color = SNUTTColors.Gray400,
            ) {
                Text(
                    text = endMinute.toFormattedTimeString(),
                    style = SNUTTTypography.button,
                )
            }
        }
    }
}

/* FIXME
 * Picker의 인접한 item끼리 Text에 들어갈 String의 길이가 같으면 드래그할 때 글리치가 생긴다. (원인 불명)
 * 길이가 다르면 문제가 없다. 임시 대처용 함수
 */
private fun String.tempBlank(a: Int): String {
    return if (a % 2 == 0) this
    else " $this "
}
