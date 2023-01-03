package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun WebViewStyleButton(
    modifier: Modifier = Modifier,
    color: Color = SNUTTColors.SNUTTTheme,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clicks { onClick() }
            .background(color)
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
