package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

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
                if (onClick != null) {
                    Modifier.clicks { onClick() }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@SnuttPreview
@Composable
private fun RoundBorderButton_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            RoundBorderButton(
                color = SNUTTColors.SNUTTTheme,
                onClick = {},
            ) {
                Text(
                    text = "친구 추가",
                    style = SNUTTTypography.button.copy(color = SNUTTColors.White900),
                )
            }
        }
    }
}
