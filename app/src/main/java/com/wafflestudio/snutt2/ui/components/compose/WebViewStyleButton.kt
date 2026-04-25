package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
fun WebViewStyleButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledColor: Color = SNUTTColors.SNUTTTheme,
    disabledColor: Color = SNUTTColors.Gray400,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clicks { if (enabled) onClick() }
            .background(shape = RoundedCornerShape(6.dp), color = if (enabled) enabledColor else disabledColor)
            .height(47.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@SnuttPreview
@Composable
private fun WebViewStyleButton_Enabled() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            WebViewStyleButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
            ) {
                Text(
                    text = "확인",
                    style = SNUTTTypography.button.copy(color = SNUTTColors.White900),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun WebViewStyleButton_Disabled() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            WebViewStyleButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = {},
            ) {
                Text(
                    text = "확인",
                    style = SNUTTTypography.button.copy(color = SNUTTColors.White900),
                )
            }
        }
    }
}
