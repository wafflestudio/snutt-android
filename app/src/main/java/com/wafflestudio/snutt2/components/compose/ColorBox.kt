package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun ColorBox(
    foreground: Color,
    background: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = SNUTTColors.Black250,
    borderThickness: Dp = 0.5.dp,
) {
    Row(
        modifier = modifier
            .border(width = borderThickness, color = borderColor)
            .size(width = 40.dp, height = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .background(foreground)
                .weight(1f)
                .fillMaxHeight(),
        )
        Box(
            modifier = Modifier
                .background(background)
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}
