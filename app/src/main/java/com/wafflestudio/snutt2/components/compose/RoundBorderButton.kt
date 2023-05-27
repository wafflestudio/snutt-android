package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundBorderButton(
    modifier: Modifier = Modifier,
    color: Color,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color, RoundedCornerShape(30))
            .padding(horizontal = 10.dp)
            .height(35.dp)
            .then(
                if(onClick != null)
                    Modifier.clicks { onClick() }
                else
                    Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
