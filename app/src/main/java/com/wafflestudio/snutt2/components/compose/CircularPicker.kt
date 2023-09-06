package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.ui.SNUTTColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> CircularPicker(
    list: List<T>,
    initialCenterIndex: Int,
    onValueChanged: (Int) -> Unit,
    columnHeightDp: Dp = 35.dp,
    PickerItemContent: @Composable (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val animatedOffset by remember { mutableStateOf(Animatable(initialCenterIndex * columnHeightDp.value)) }

    val offset =
        (animatedOffset.value % (columnHeightDp.value * list.size)).let {
            if (it > columnHeightDp.value * (list.size - 0.5f)) it - columnHeightDp.value * list.size
            else if (it < columnHeightDp.value * (- 0.5f)) it + columnHeightDp.value * list.size
            else it
        }

    // 중앙에 두 item의 중간이 위치할 때 threshold 및 centerItemIndex 값이 변경
    val threshold = (offset + columnHeightDp.value / 2) % columnHeightDp.value
    val centerItemIndex by remember(threshold) {
        mutableStateOf(
            (offset / columnHeightDp.value).roundToInt().coerceAtMost(list.size - 1)
        )
    }
    /* localOffset
     * 기본값 0 (스크롤 하지 않았을 때)
     * 아래로 스크롤: item 높이 절반만큼 스크롤하면 -{columnHeightDp.value/2}, 경계에서 {columnHeightDp.value/2}으로 상승 후 item 높이만큼 스크롤 하면 다시 0
     * 위로 스크롤 : item 높이 절반만큼 스크롤하면 {columnHeightDp.value/2}, 경계에서 -{columnHeightDp.value/2}으로 하강 후 item 높이만큼 스크롤 하면 다시 0
     */
    val localOffset =
        if (offset % columnHeightDp.value > columnHeightDp.value / 2) (columnHeightDp.value - (offset % columnHeightDp.value))
        else -(offset % columnHeightDp.value)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(columnHeightDp * 5)
            .padding(horizontal = 10.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        animatedOffset.snapTo(animatedOffset.value - delta / 4)
                    }
                },
                onDragStopped = { _ ->
//                            animatedOffset.animateDecay(-velocity, decay)
                    scope.launch {
                        val offsetLeft = (animatedOffset.value % columnHeightDp.value).let {
                            if (it < 0) it + columnHeightDp.value
                            else it
                        }
                        if (offsetLeft >= columnHeightDp.value / 2) {
                            animatedOffset.animateTo(animatedOffset.value + (columnHeightDp.value - offsetLeft))
                        } else {
                            animatedOffset.animateTo(animatedOffset.value - offsetLeft)
                        }
                    }
                    onValueChanged(centerItemIndex)
                }
            ),
    ) {
        PickerItem(
            modifier = Modifier
                .offset(y = -columnHeightDp * 2 + localOffset.dp)
                .alpha((columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
            content = {
                PickerItemContent(
                    if (centerItemIndex - 2 < 0) centerItemIndex - 2 + list.size
                    else centerItemIndex - 2
                )
            }
        )
        PickerItem(
            modifier = Modifier
                .offset(y = -columnHeightDp + localOffset.dp)
                .alpha((columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
            content = {
                PickerItemContent(
                    if (centerItemIndex - 1 < 0) centerItemIndex - 1 + list.size
                    else centerItemIndex - 1
                )
            }
        )
        Box(modifier = Modifier.height(columnHeightDp).fillMaxWidth().background(SNUTTColors.Gray100, RoundedCornerShape(30f)).offset(y = -columnHeightDp / 2).zIndex(-5f))
        PickerItem(
            modifier = Modifier.offset(y = localOffset.dp),
            content = { PickerItemContent(centerItemIndex) }
        )
        PickerItem(
            modifier = Modifier
                .offset(y = columnHeightDp + localOffset.dp)
                .alpha(-(-columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
            content = { PickerItemContent((centerItemIndex + 1) % list.size) }
        )
        PickerItem(
            modifier = Modifier
                .offset(y = columnHeightDp * 2 + localOffset.dp)
                .alpha(-(-columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
            content = { PickerItemContent((centerItemIndex + 2) % list.size) }
        )
    }
}
