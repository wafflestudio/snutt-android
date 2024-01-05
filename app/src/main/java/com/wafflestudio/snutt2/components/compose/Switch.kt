package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float,
): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return remember {
        derivedStateOf { BiasAlignment.Horizontal(bias) }
    }
}

@Composable
fun Switch(
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalBias by remember(checked) { mutableFloatStateOf(if (checked) 1f else -1f) }
    val alignment by animateHorizontalAlignmentAsState(targetBiasValue = horizontalBias)
    val color by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colors.secondary else SNUTTColors.Gray10,
        label = "",
    )
    Column(
        modifier = modifier
            .size(width = 50.dp, height = 30.dp)
            .clip(shape = CircleShape)
            .background(color)
            .padding(2.dp)
            .clicks { onCheckChanged(checked.not()) },
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .shadow(elevation = 5.dp, shape = CircleShape)
                .background(color = MaterialTheme.colors.surface, shape = CircleShape)
                .align(alignment),
        )
    }
}
