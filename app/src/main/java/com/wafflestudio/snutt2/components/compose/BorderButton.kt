package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun BorderButton(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 0.dp,
    bgColor: Color = SNUTTColors.White900,
    color: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clicks { onClick() }
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(cornerRadius),
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .height(45.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
