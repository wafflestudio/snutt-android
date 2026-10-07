package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewLectureListItem(
    title: String,
    professor: String,
    rating: Double?,
    reviewCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp)
            .clicks(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = SNUTTTypography.h4.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SNUTTColors.Black900),
            maxLines = 1,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_star_filled),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (rating == null) SNUTTColors.TextAssistive else SNUTTColors.Yellow),
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = rating?.let { "%.1f".format(it) } ?: "(-)",
                    style = SNUTTTypography.body1.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (rating == null) SNUTTColors.TextMed else SNUTTColors.Black900,
                    ),
                )
                if (rating != null) {
                    Text(
                        text = "($reviewCount)",
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative),
                    )
                }
            }
            Text(text = "·", style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative))
            Text(text = professor, style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative))
        }
    }
}

@SnuttPreview
@Composable
private fun ReviewLectureListItem_Rated() {
    SnuttPreviewCenteredSurface {
        ReviewLectureListItem("인공지능 신뢰성", "박상철", 3.0, 3, {})
    }
}
