package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun ColorCircle(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = Brush.sweepGradient(
                    listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Red),
                ),
            ),
    ) {
        Box(
            modifier = modifier
                .padding(3.dp)
                .border(
                    width = 2.dp,
                    color = if (color == MaterialTheme.colors.surface) SNUTTColors.Gray20 else MaterialTheme.colors.surface,    // color가 surface일 경우 배경(surface)과 구분이 되지 않으므로 Gray20으로 칠함
                    shape = CircleShape,
                )
                .clip(CircleShape)
                .background(color),
        )
    }
}
