package com.wafflestudio.snutt2.components.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import android.graphics.Color as AndroidColor

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
) {
    var hsv by remember {
        mutableStateOf(colorToHsv(initialColor))
    }
    var hexCode by remember {
        mutableStateOf(
            String.format(
                "#%06X",
                0xFFFFFF and initialColor.toArgb(),
            ),
        )
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SatValPanel(
            hue = hsv.first,
            satVal = hsv.second to hsv.third,
            onSatValChanged = { sat, value ->
                hsv = Triple(hsv.first, sat, value)
                hexCode = hsvToString(hsv)
                onColorChanged(Color.hsv(hsv.first, hsv.second, hsv.third))
            },
        )
        HueBar(
            hue = hsv.first,
            onHueChanged = { hue ->
                hsv = Triple(hue, hsv.second, hsv.third)
                hexCode = hsvToString(hsv)
                onColorChanged(Color.hsv(hsv.first, hsv.second, hsv.third))
            },
        )
        Row {
            Row(
                modifier = Modifier
                    .size(width = 140.dp, height = 40.dp)
                    .border(
                        width = 0.5.dp,
                        color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = initialColor),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = Color.hsv(hsv.first, hsv.second, hsv.third)),
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            BasicTextField(
                value = hexCode,
                onValueChange = {
                    hexCode = it
                },
                modifier = Modifier
                    .clearFocusOnKeyboardDismiss()
                    .weight(1f)
                    .height(40.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        try {
                            hsv = colorToHsv(Color(AndroidColor.parseColor(hexCode)))
                        } catch (e: Exception) {
                            hexCode = hsvToString(hsv)
                        }
                    },
                ),
                textStyle = SNUTTTypography.body1.copy(textAlign = TextAlign.Center),
                singleLine = true,
                decorationBox = {
                    Row(
                        modifier = Modifier
                            .border(
                                width = 0.5.dp,
                                color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2,
                                shape = RoundedCornerShape(10.dp),
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        it()
                    }
                },
            )
        }
    }
}

@Composable
fun HueBar(
    hue: Float,
    onHueChanged: (Float) -> Unit,
) {
    Canvas(
        modifier = Modifier
            .height(20.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    onHueChanged(input.position.x.coerceIn(0f..size.width.toFloat()) * 360f / size.width)
                }
            }
            .clip(CircleShape),
    ) {
        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = Canvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val hueColors = IntArray(huePanel.width().toInt()) {
            AndroidColor.HSVToColor(floatArrayOf((360f / huePanel.width().toInt()) * it, 1f, 1f))
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
            center = Offset(hue * size.width / 360f, size.height / 2),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

@Composable
fun SatValPanel(
    hue: Float,
    satVal: Pair<Float, Float>,
    onSatValChanged: (Float, Float) -> Unit,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    onSatValChanged(
                        1f / size.width * input.position.x.coerceIn(0f..size.width.toFloat()),
                        1f - 1f / size.height * input.position.y.coerceIn(0f..size.height.toFloat()),
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp)),
    ) {
        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val rgb = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
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
            center = Offset(satVal.first * size.width, (1f - satVal.second) * size.height),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

fun showColorPickerDialog(
    context: Context,
    modalState: ModalState,
    initialColor: Color,
    onColorPicked: (Color) -> Unit,
) {
    var currentColor = initialColor
    modalState.setOkCancel(
        context = context,
        onDismiss = {
            modalState.hide()
        },
        onConfirm = {
            onColorPicked(currentColor)
            modalState.hide()
        },
        title = "색상 선택",
    ) {
        ColorPicker(
            initialColor = initialColor,
            onColorChanged = {
                currentColor = it
            },
        )
    }.show()
}

private fun colorToHsv(color: Color): Triple<Float, Float, Float> {
    val temp = floatArrayOf(0f, 0f, 0f)
    AndroidColor.colorToHSV(color.toArgb(), temp)
    return Triple(temp[0], temp[1], temp[2])
}

private fun hsvToString(hsv: Triple<Float, Float, Float>): String {
    return String.format(
        "#%06X",
        0xFFFFFF and Color.hsv(hsv.first, hsv.second, hsv.third).toArgb(),
    )
}
