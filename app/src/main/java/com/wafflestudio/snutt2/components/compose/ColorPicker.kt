package com.wafflestudio.snutt2.components.compose

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import com.wafflestudio.snutt2.ui.SNUTTColors
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun ColorPicker(
    initialColor: Int,
    onColorChanged: (Int) -> Unit,
) {
    var hsv by remember {
        val temp = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(initialColor, temp)
        mutableStateOf(Triple(temp[0], temp[1], temp[2]))
    }
    val oldColor = ComposeColor(initialColor)
    Column(
        modifier = Modifier.padding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SatValPanel(
            initialColor = initialColor,
            hue = hsv.first,
            onSatValChanged = { sat, value ->
                hsv = Triple(hsv.first, sat, value)
                onColorChanged(Color.HSVToColor(floatArrayOf(hsv.first, hsv.second, hsv.third)))
            },
        )
        HueBar(
            initialColor = initialColor,
            onHueChanged = { hue ->
                hsv = Triple(hue, hsv.second, hsv.third)
                onColorChanged(Color.HSVToColor(floatArrayOf(hsv.first, hsv.second, hsv.third)))
            },
        )
        Row {
            Row(
                modifier = Modifier
                    .size(width = 140.dp, height = 40.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = oldColor),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = ComposeColor.hsv(hsv.first, hsv.second, hsv.third)),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun HueBar(
    initialColor: Int,
    onHueChanged: (Float) -> Unit,
) {
    var drawScopeWidth by remember { mutableFloatStateOf(0f) }
    var pressOffset by remember {
        val temp = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(initialColor, temp)
        mutableStateOf(Offset(temp[0] * drawScopeWidth / 360f, 0f))
    }
    Canvas(
        modifier = Modifier
            .height(20.dp)
            .width(300.dp)
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    pressOffset = Offset(input.position.x.coerceIn(0f..drawScopeWidth), 0f)
                    onHueChanged(pressOffset.x * 360f / drawScopeWidth)
                }
            }
            .clip(CircleShape),
    ) {
        drawScopeWidth = size.width
        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = Canvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val hueColors = IntArray(huePanel.width().toInt()) {
            Color.HSVToColor(floatArrayOf((360f / huePanel.width().toInt()) * it, 1f, 1f))
        }
        Paint().let { linePaint ->
            linePaint.strokeWidth = 0f
            hueColors.forEachIndexed { idx, col ->
                linePaint.color = col
                hueCanvas.drawLine(idx.toFloat(), 0f, idx.toFloat(), huePanel.bottom, linePaint)
            }
        }
        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, huePanel.toRect(), null)
        }
        drawCircle(
            color = SNUTTColors.White,
            radius = size.height / 2,
            center = Offset(pressOffset.x, size.height / 2),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

@Composable
fun SatValPanel(
    initialColor: Int,
    hue: Float,
    onSatValChanged: (Float, Float) -> Unit,
) {
    var drawScopeWidth by remember { mutableFloatStateOf(0f) }
    var drawScopeHeight by remember { mutableFloatStateOf(0f) }
    var pressOffset by remember {
        val temp = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(initialColor, temp)
        mutableStateOf(Offset(temp[1] * drawScopeWidth, (1 - temp[2]) * drawScopeHeight))
    }
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    pressOffset = Offset(
                        x = input.position.x.coerceIn(0f..drawScopeWidth),
                        y = input.position.y.coerceIn(0f..drawScopeHeight),
                    )
                    onSatValChanged(
                        1f / drawScopeWidth * pressOffset.x,
                        1f - 1f / drawScopeHeight * pressOffset.y,
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp)),
    ) {
        drawScopeWidth = size.width
        drawScopeHeight = size.height
        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val rgb = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP,
        )
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x10000000, Shader.TileMode.CLAMP,
        )
        canvas.drawRoundRect(
            satValPanel,
            12.dp.toPx(),
            12.dp.toPx(),
            Paint().apply {
                shader = ComposeShader(valShader, satShader, PorterDuff.Mode.MULTIPLY)
            },
        )
        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, satValPanel.toRect(), null)
        }
        drawCircle(
            color = SNUTTColors.White,
            radius = 8.dp.toPx(),
            center = pressOffset,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
