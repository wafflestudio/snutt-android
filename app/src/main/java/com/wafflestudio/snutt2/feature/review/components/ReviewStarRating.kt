package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import kotlin.math.floor

@Composable
fun ReviewStarRating(
    rating: Float,
    onRatingChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    starSize: Dp = 28.dp,
    starSpacing: Dp = 0.dp,
) {
    Row(
        modifier = modifier.then(
            if (onRatingChange != null) {
                Modifier.pointerInput(onRatingChange, starSize, starSpacing) {
                    awaitEachGesture {
                        val down = awaitFirstDown()

                        fun updateRating(positionX: Float) {
                            val starIndex = floor(positionX / (starSize + starSpacing).toPx()).toInt().coerceIn(0, 4)
                            val positionInStar = positionX - starIndex * (starSize + starSpacing).toPx()
                            val halfStars = (starIndex * 2 + if (positionInStar < starSize.toPx() / 2) 1 else 2).coerceIn(1, 10)
                            onRatingChange(halfStars / 2f)
                        }

                        updateRating(down.position.x)
                        do {
                            val event = awaitPointerEvent()
                            event.changes.firstOrNull()?.let { updateRating(it.position.x) }
                        } while (event.changes.any { it.pressed })
                    }
                }
            } else {
                Modifier
            },
        ),
        horizontalArrangement = Arrangement.spacedBy(starSpacing),
    ) {
        repeat(5) { index ->
            when {
                rating >= index + 1f -> Image(
                    painter = painterResource(R.drawable.ic_star_filled),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(SNUTTColors.Yellow),
                    modifier = Modifier.size(starSize),
                )

                rating >= index + 0.5f -> Box(modifier = Modifier.size(starSize)) {
                    Image(
                        painter = painterResource(R.drawable.ic_star_filled),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(SNUTTColors.TextAssistive),
                        modifier = Modifier.size(starSize),
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_star_half),
                        contentDescription = null,
                        modifier = Modifier.size(starSize),
                    )
                }

                else -> Image(
                    painter = painterResource(R.drawable.ic_star_filled),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(SNUTTColors.TextAssistive),
                    modifier = Modifier.size(starSize),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun ReviewStarRating_HalfStar() {
    SnuttPreviewCenteredSurface {
        ReviewStarRating(rating = 3.5f, onRatingChange = {})
    }
}
