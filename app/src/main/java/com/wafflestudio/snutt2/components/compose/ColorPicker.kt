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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toRect
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
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
    val setHsvWithHexCode = {
        try {
            hsv = colorToHsv(Color(AndroidColor.parseColor(hexCode)))
            onColorChanged(Color.hsv(hsv.first, hsv.second, hsv.third))
        } catch (e: Exception) {
            hexCode = hsvToString(hsv)
        }
    }
    val textSelectionColors = TextSelectionColors(
        handleColor = SNUTTColors.Black900,
        backgroundColor = SNUTTColors.Black300,
    )
    val focusManager = LocalFocusManager.current

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
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        HueBar(
            hue = hsv.first,
            onHueChanged = { hue ->
                hsv = Triple(hue, hsv.second, hsv.third)
                hexCode = hsvToString(hsv)
                onColorChanged(Color.hsv(hsv.first, hsv.second, hsv.third))
            },
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
        )
        Row(
            modifier = Modifier.height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .width(120.dp)
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "HEX",
                modifier = Modifier.padding(end = 6.dp),
                color = MaterialTheme.colors.onSurfaceVariant,
                style = SNUTTTypography.body1,
            )
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                BasicTextField(
                    value = hexCode.substringAfter('#'),
                    onValueChange = {
                        hexCode = "#$it"
                    },
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .onFocusChanged {
                            if (it.isFocused.not()) {
                                setHsvWithHexCode()
                            }
                        }
                        .width(85.dp)
                        .fillMaxHeight(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        setHsvWithHexCode()
                        focusManager.clearFocus()
                    },),
                    textStyle = SNUTTTypography.body1.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp,
                    ),
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
}

@Composable
fun HueBar(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape),
    ) {
        HueBackground( // hue에 따라 변하는 부분과 변하지 않는 부분을 분리하여 recompose 최적화
            modifier = Modifier.fillMaxSize(),
        )
        HueCircle(
            hue = hue,
            onHueChanged = onHueChanged,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun HueBackground(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier,
    ) {
        val bitmap =
            Bitmap.createBitmap(
                size.width.toInt(),
                size.height.toInt(),
                Bitmap.Config.ARGB_8888,
            )
        val hueCanvas = Canvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val hueColors = IntArray(huePanel.width().toInt()) {
            AndroidColor.HSVToColor(
                floatArrayOf(
                    (360f / huePanel.width().toInt()) * it, // 색 -> 위치 변환. hue값을 0부터 360까지 균일하게 변화시켜 huePanel.width() 길이의 배열에 저장
                    1f,
                    1f,
                ),
            )
        }
        Paint().let { linePaint ->
            linePaint.strokeWidth = 0f
            hueColors.forEachIndexed { idx, col -> // 배열에 저장된 색을 x좌표 0부터 huePanel.width()까지 1픽셀씩 그림
                linePaint.color = col
                hueCanvas.drawLine(idx.toFloat(), 0f, idx.toFloat(), huePanel.bottom, linePaint)
            }
        }

        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, huePanel.toRect(), null)
        }
    }
}

@Composable
fun HueCircle(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    onHueChanged(input.position.x.coerceIn(0f..size.width.toFloat()) * 360f / size.width) // 색 -> 위치 변환의 역산
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    onHueChanged(it.x.coerceIn(0f..size.width.toFloat()) * 360f / size.width) // 색 -> 위치 변환의 역산
                }
            },
    ) {
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
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
    ) {
        SatValBackground( // sat, val에 따라 변하는 부분과 변하지 않는 부분을 분리하여 recompose 최적화
            hue = hue,
            modifier = Modifier.fillMaxSize(),
        )
        SatValCircle(
            satVal = satVal,
            onSatValChanged = onSatValChanged,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun SatValBackground(
    hue: Float,
    modifier: Modifier = Modifier,
) {
    Canvas( // TODO: hue가 바뀌면 전부 다시 그려야 함. 최적화?
        modifier = modifier,
    ) {
        val bitmap =
            Bitmap.createBitmap(
                size.width.toInt(),
                size.height.toInt(),
                Bitmap.Config.ARGB_8888,
            )
        val canvas = Canvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val rgb = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP,
        ) // 하얀색부터 HSV(hue, 1, 1)까지를 좌상단에서 우상단까지 gradient
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x10000000, Shader.TileMode.CLAMP,
        ) // 하얀색부터 검정색까지를 좌상단에서 좌하단까지 gradient
        canvas.drawRoundRect(
            satValPanel,
            12.dp.toPx(),
            12.dp.toPx(),
            Paint().apply {
                shader = ComposeShader(valShader, satShader, PorterDuff.Mode.MULTIPLY) // 두 shader를 곱함
            },
        )
        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, null, satValPanel.toRect(), null)
        }
    }
}

@Composable
fun SatValCircle(
    satVal: Pair<Float, Float>,
    onSatValChanged: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { input, _ ->
                    onSatValChanged(
                        1f / size.width * input.position.x.coerceIn(0f..size.width.toFloat()), // x좌표 0~size.width를 0~1로 변환
                        1f - 1f / size.height * input.position.y.coerceIn(0f..size.height.toFloat()), // y좌표 0~size.height를 1~0으로 변환
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    onSatValChanged(
                        1f / size.width * it.x.coerceIn(0f..size.width.toFloat()), // x좌표 0~size.width를 0~1로 변환
                        1f - 1f / size.height * it.y.coerceIn(0f..size.height.toFloat()), // y좌표 0~size.height를 1~0으로 변환
                    )
                }
            },
    ) {
        drawCircle(
            color = SNUTTColors.White,
            radius = 8.dp.toPx(),
            center = Offset(satVal.first * size.width, (1f - satVal.second) * size.height), // 좌표 -> 색 변환의 역산
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
    modalState.set(
        onDismiss = {
            modalState.hide()
        },
        onConfirm = {
            onColorPicked(currentColor)
            modalState.hide()
        },
        title = context.getString(R.string.color_picker_dialog_title),
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
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
