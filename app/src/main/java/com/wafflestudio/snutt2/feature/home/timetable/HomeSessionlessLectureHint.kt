package com.wafflestudio.snutt2.feature.home.timetable

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowLeftBold
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography


@Composable
fun HomeSessionlessLectureHint(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val yOffsetTop by infiniteTransition.animateValue(
        initialValue = (-3).dp,
        targetValue = 2.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = EaseIn,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        typeConverter = Dp.VectorConverter,
    )
    val yOffsetBottom by infiniteTransition.animateValue(
        initialValue = (-3).dp,
        targetValue = 2.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = EaseIn,
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(30),
        ),
        typeConverter = Dp.VectorConverter,
    )
    val borderColor = SNUTTColors.SNUTTTheme
    Column(
        modifier = Modifier.offset(y = (40).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val blurRadius = 8.dp.toPx()

                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            isAntiAlias = true
                            style = Paint.Style.STROKE
                            color = borderColor.toArgb()
                            this.strokeWidth = strokeWidth
                            maskFilter = BlurMaskFilter(
                                blurRadius,
                                BlurMaskFilter.Blur.NORMAL,
                            )
                        }

                        val halfStroke = strokeWidth / 2
                        val rect = RectF(
                            halfStroke,
                            halfStroke,
                            size.width - halfStroke,
                            size.height - halfStroke,
                        )

                        canvas.nativeCanvas.drawRoundRect(
                            rect,
                            7.2.dp.toPx(),
                            7.2.dp.toPx(),
                            paint,
                        )
                    }
                }
                .background(
                    color = SNUTTColors.DropdownMenuBackground,
                    shape = RoundedCornerShape(7.2.dp),
                )
                .border(
                    width = 1.dp,
                    color = SNUTTColors.SNUTTTheme,
                    shape = RoundedCornerShape(7.2.dp),
                ),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 7.5.dp, horizontal = 16.dp),
                text = stringResource(R.string.timetable_sessionless_lecture_list_hint),
                style = SNUTTTypography.body1,
                color = SNUTTColors.SNUTTDarkMintBlue,
            )
        }
        ArrowLeftBold(
            modifier = Modifier
                .rotate(-90F)
                .offset { IntOffset((5.dp + yOffsetTop).roundToPx(), 0) },
            colorFilter = ColorFilter.tint(SNUTTColors.Hint1),
        )
        ArrowLeftBold(
            modifier = Modifier
                .rotate(-90F)
                .offset { IntOffset((40.dp + yOffsetBottom).roundToPx(), 0) },
            colorFilter = ColorFilter.tint(SNUTTColors.Hint2),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeSessionlessLectureHintPreview() {
    HomeSessionlessLectureHint()
}
