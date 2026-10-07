package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewMetricRow(
    grade: Float,
    usefulness: Float,
    teaching: Float,
    ease: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(39.dp)
            .background(Color(0x0a1bd0c8), RoundedCornerShape(4.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReviewMetric("성적", grade)
        ReviewMetricDivider()
        ReviewMetric("유익함", usefulness)
        ReviewMetricDivider()
        ReviewMetric("강의력", teaching)
        ReviewMetricDivider()
        ReviewMetric("널널함", ease)
    }
}

@Composable
private fun ReviewMetric(label: String, value: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative))
        Text(
            text = "%.1f".format(value),
            style = SNUTTTypography.body1.copy(fontWeight = FontWeight.Medium, color = SNUTTColors.DarkMintText),
        )
    }
}

@Composable
private fun ReviewMetricDivider() {
    val dividerColor = SNUTTColors.TextAssistive.copy(alpha = 0.4f)
    Canvas(modifier = Modifier.width(0.8.dp).height(14.dp)) {
        drawLine(
            color = dividerColor,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 0.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewMetricRow_Default() {
    SnuttPreviewCenteredSurface {
        ReviewMetricRow(grade = 2f, usefulness = 5f, teaching = 4.5f, ease = 3.5f)
    }
}
