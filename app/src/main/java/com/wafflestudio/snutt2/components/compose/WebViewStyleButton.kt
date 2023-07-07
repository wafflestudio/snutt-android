package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun WebViewStyleButton(
    modifier: Modifier = Modifier,
    color: Color = SNUTTColors.SNUTTTheme,
    cornerRadius: Dp = 0.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clicks { onClick() }
            .background(color, RoundedCornerShape(cornerRadius))
            .height(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
