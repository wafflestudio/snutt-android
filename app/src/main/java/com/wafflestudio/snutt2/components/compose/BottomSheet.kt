package com.wafflestudio.snutt2.components.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@Composable
fun BottomSheet(
    content: @Composable () -> Unit,
    animatedOffset: Animatable<Float, AnimationVector1D>,
    animateDim: Animatable<Color, AnimationVector4D>,
    onDismiss: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var dismissTrigger by remember { mutableStateOf(false) }
    BackHandler { dismissTrigger = true }
    if (dismissTrigger) {
        scope.launch {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(with(LocalDensity.current) { DrawerDefaults.Elevation.toPx() }),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .background(animateDim.value)
                .fillMaxSize()
                .clicks { dismissTrigger = true }
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStartPercent = 8, topEndPercent = 8))
                .zIndex(with(LocalDensity.current) { (DrawerDefaults.Elevation + 1.dp).toPx() })
                .offset(y = animatedOffset.value.dp)
        ) {
            content()
        }
    }
}
