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
    PickerItemContent: @Composable (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val columnHeightDp = 35.dp

    val animatedOffset = remember { Animatable(initialCenterIndex * columnHeightDp.value) }
        .apply {
            updateBounds(0f, (columnHeightDp.value * list.size))
        }
    // 중앙에 두 item의 중간이 위치할 때 threshold 및 centerItemIndex 값이 변경
    val threshold = (animatedOffset.value + columnHeightDp.value / 2) % columnHeightDp.value
    var centerItemIndex by remember(threshold) {
        mutableStateOf(
            (animatedOffset.value / columnHeightDp.value).roundToInt().coerceAtMost(list.size - 1)
        )
    }
    /* localOffset
     * 기본값 0 (스크롤 하지 않았을 때)
     * 아래로 스크롤: item 높이 절반만큼 스크롤하면 -{columnHeightDp.value/2}, 경계에서 {columnHeightDp.value/2}으로 상승 후 item 높이만큼 스크롤 하면 다시 0
     * 위로 스크롤 : item 높이 절반만큼 스크롤하면 {columnHeightDp.value/2}, 경계에서 -{columnHeightDp.value/2}으로 하강 후 item 높이만큼 스크롤 하면 다시 0
     */
    val localOffset =
        if (animatedOffset.value % columnHeightDp.value > columnHeightDp.value / 2) (columnHeightDp.value - (animatedOffset.value % columnHeightDp.value))
        else -(animatedOffset.value % columnHeightDp.value)

    // list 자체의 변경에 따른 변화를 반영한다. (recompose 로 되는 방법 찾기)
    // FIXME: 시작 시간의 변경에 따라 끝나는 시간의 list 가 바뀌는데, 이때 미세한 떨림 존재
    LaunchedEffect(list.size) {
        centerItemIndex = initialCenterIndex
        animatedOffset.snapTo(columnHeightDp.value * centerItemIndex)
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
                }
            ),
    ) {
        if (centerItemIndex > 1) {
            PickerItem(
                modifier = Modifier
                    .offset(y = -columnHeightDp * 2 + localOffset.dp)
                    .alpha((columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
                content = {
                    PickerItemContent(centerItemIndex - 2)
                }
            )
        }
        if (centerItemIndex > 0) {
            PickerItem(
                modifier = Modifier
                    .offset(y = -columnHeightDp + localOffset.dp)
                    .alpha((columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
                content = { PickerItemContent(centerItemIndex - 1) }
            )
        }
        Box(modifier = Modifier.height(columnHeightDp).fillMaxWidth().background(SNUTTColors.Gray100, RoundedCornerShape(30f)).offset(y = -columnHeightDp / 2).zIndex(-5f))
        PickerItem(
            modifier = Modifier.offset(y = localOffset.dp),
            content = { PickerItemContent(centerItemIndex) }
        )
        if (centerItemIndex < list.size - 1) {
            PickerItem(
                modifier = Modifier
                    .offset(y = columnHeightDp + localOffset.dp)
                    .alpha(-(-columnHeightDp * 3 / 4 + localOffset.dp / 2) / columnHeightDp),
                content = { PickerItemContent(centerItemIndex + 1) }
            )
        }
        if (centerItemIndex < list.size - 2) {
            PickerItem(
                modifier = Modifier
                    .offset(y = columnHeightDp * 2 + localOffset.dp)
                    .alpha(-(-columnHeightDp / 4 + localOffset.dp / 2) / columnHeightDp),
                content = { PickerItemContent(centerItemIndex + 2) }
            )
        }
    }
}

@Composable
fun PickerItem(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
