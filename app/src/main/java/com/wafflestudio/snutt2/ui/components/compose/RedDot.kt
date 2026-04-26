package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun RedDot() {
    Canvas(modifier = Modifier.size(5.dp)) {
        drawCircle(SNUTTColors.Red)
    }
}

@Composable
fun RedDotWithNumber(
    modifier: Modifier = Modifier,
    number: Long,
) {
    Canvas(
        modifier = modifier.size(16.dp),
    ) {
        drawCircle(
            color = SNUTTColors.Red,
            radius = size.minDimension / 2,
        )

        drawContext.canvas.nativeCanvas.apply {
            val text = number.toString()

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                textSize = size.minDimension * 0.7f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val x = size.width / 2
            val y = size.height / 2 - (paint.descent() + paint.ascent()) / 2

            drawText(text, x, y, paint)
        }
    }
}

@Composable
fun IconWithAlertDot(
    redDotExist: Boolean = false,
    dotSize: Dp = 5.dp,
    dotYOffset: Dp = 0.dp,
    color: Color = SNUTTColors.Red,
    content: @Composable (Modifier) -> Unit,
) {
    Box {
        content(Modifier.align(Alignment.Center))
        if (redDotExist) {
            Canvas(
                modifier = Modifier
                    .size(dotSize)
                    .align(Alignment.TopEnd)
                    .offset(y = dotYOffset),
            ) {
                drawCircle(color)
            }
        }
    }
}

@SnuttPreview
@Composable
private fun RedDot_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            RedDot()
        }
    }
}

@SnuttPreview
@Composable
private fun RedDotWithNumber_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            RedDotWithNumber(number = 3)
        }
    }
}

@SnuttPreview
@Composable
private fun IconWithAlertDot_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            IconWithAlertDot(redDotExist = true) {
                SnuttIcon(R.drawable.ic_alarm_default, modifier = it.size(30.dp))
            }
        }
    }
}
