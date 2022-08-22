package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> Picker(
    list: List<T>,
    value: T,
    onValueChanged: (T) -> Unit,
    PickerItemContent: @Composable (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val columnHeightDp = 60.dp

    val animatedOffset = remember { Animatable(list.indexOf(value) * columnHeightDp.value) }
        .apply {
            updateBounds(0f, (columnHeightDp.value * list.size))
        }
    val threshold = (animatedOffset.value + columnHeightDp.value / 2) % columnHeightDp.value
    val centerItemIndex = remember(threshold) {
        (animatedOffset.value / columnHeightDp.value).roundToInt().coerceAtMost(list.size - 1)
    }
    val localOffset =
        if (animatedOffset.value % columnHeightDp.value > columnHeightDp.value / 2) (columnHeightDp.value - (animatedOffset.value % columnHeightDp.value))
        else -(animatedOffset.value % columnHeightDp.value)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(columnHeightDp * 3)
            .padding(horizontal = 10.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        animatedOffset.snapTo(animatedOffset.value - delta / 2)
                    }
                },
                onDragStopped = { velocity ->
//                            animatedOffset.animateDecay(-velocity, decay)
                    scope.launch {
                        animatedOffset.animateTo(columnHeightDp.value * centerItemIndex)
                    }
                    onValueChanged(list[centerItemIndex])
                }
            ),
    ) {
        if (centerItemIndex > 0) {
            PickerItem(
                modifier = Modifier
                    .offset(y = -columnHeightDp + localOffset.dp)
                    .alpha((columnHeightDp / 2 + localOffset.dp) / columnHeightDp),
                content = { PickerItemContent(centerItemIndex - 1) }
            )
        }
        Divider(
            thickness = 2.dp,
            modifier = Modifier.offset(y = -columnHeightDp / 2)
        )
        PickerItem(
            modifier = Modifier.offset(y = localOffset.dp),
            content = { PickerItemContent(centerItemIndex) }
        )
        Divider(
            thickness = 2.dp,
            modifier = Modifier.offset(y = columnHeightDp / 2)
        )
        if (centerItemIndex < list.size - 1) {
            PickerItem(
                modifier = Modifier
                    .offset(y = columnHeightDp + localOffset.dp)
                    .alpha(-(-columnHeightDp / 2 + localOffset.dp) / columnHeightDp),
                content = { PickerItemContent(centerItemIndex + 1) }
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
