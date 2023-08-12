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
fun <T> Picker(
    list: List<T>,
    initialCenterIndex: Int,
    onValueChanged: (Int) -> Unit,
    columnHeightDp: Dp = 35.dp,
    PickerItemContent: @Composable (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(initialCenterIndex * columnHeightDp.value) }
        .apply {
            updateBounds(columnHeightDp.value * (-0.5f), columnHeightDp.value * (list.size - 0.5f))
        }
    val centerItemIndex = (animatedOffset.value / columnHeightDp.value).roundToInt().coerceAtMost(list.size - 1)

    /* localOffset
     * 기본값 0 (스크롤 하지 않았을 때)
     * 아래로 스크롤: item 높이 절반만큼 스크롤하면 -{columnHeightDp.value/2}, 경계에서 {columnHeightDp.value/2}으로 상승 후 item 높이만큼 스크롤 하면 다시 0
     * 위로 스크롤 : item 높이 절반만큼 스크롤하면 {columnHeightDp.value/2}, 경계에서 -{columnHeightDp.value/2}으로 하강 후 item 높이만큼 스크롤 하면 다시 0
     */
    val localOffset =
        if (animatedOffset.value % columnHeightDp.value > columnHeightDp.value / 2) {
            (columnHeightDp.value - (animatedOffset.value % columnHeightDp.value))
        } else {
            -(animatedOffset.value % columnHeightDp.value)
        }

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
                onDragStopped = { velocity ->
//                            animatedOffset.animateDecay(-velocity, decay)
                    scope.launch {
                        animatedOffset.animateTo(columnHeightDp.value * centerItemIndex)
                    }
                    onValueChanged(centerItemIndex)
                },
            ),
    ) {
        if (list.isNotEmpty()) {
            if (centerItemIndex > 1) {
                PickerItem(
                    modifier = Modifier
                        .offset(y = -columnHeightDp * 2 + localOffset.dp)
                        .alpha((columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
                    content = {
                        PickerItemContent(centerItemIndex - 2)
                    },
                )
            }
            if (centerItemIndex > 0) {
                PickerItem(
                    modifier = Modifier
                        .offset(y = -columnHeightDp + localOffset.dp)
                        .alpha((columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
                    content = { PickerItemContent(centerItemIndex - 1) },
                )
            }
            Box(modifier = Modifier.height(columnHeightDp).fillMaxWidth().background(SNUTTColors.Gray100, RoundedCornerShape(30f)).offset(y = -columnHeightDp / 2).zIndex(-5f))
            PickerItem(
                modifier = Modifier.offset(y = localOffset.dp),
                content = { PickerItemContent(centerItemIndex) },
            )
            if (centerItemIndex < list.size - 1) {
                PickerItem(
                    modifier = Modifier
                        .offset(y = columnHeightDp + localOffset.dp)
                        .alpha(-(-columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
                    content = { PickerItemContent(centerItemIndex + 1) },
                )
            }
            if (centerItemIndex < list.size - 2) {
                PickerItem(
                    modifier = Modifier
                        .offset(y = columnHeightDp * 2 + localOffset.dp)
                        .alpha(-(-columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
                    content = { PickerItemContent(centerItemIndex + 2) },
                )
            }
        }
    }
}

@Composable
fun PickerItem(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
