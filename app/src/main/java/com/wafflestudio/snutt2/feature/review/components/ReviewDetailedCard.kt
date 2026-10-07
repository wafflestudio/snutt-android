package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toShortYearFormattedString

@Composable
fun ReviewDetailedCard(
    review: LectureReview,
    onLikeClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSemester: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showSemester) {
            Text(
                text = review.courseBook.toShortYearFormattedString(LocalContext.current),
                style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.Gray30),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReviewStarRating(rating = review.rating.toFloat(), onRatingChange = null, starSize = 16.dp)
            Image(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = null,
                colorFilter = ColorFilter.tint(SNUTTColors.TextAssistive),
                modifier = Modifier.size(20.dp).clicks(onClick = onMoreClick),
            )
        }
        ReviewMetricRow(
            grade = review.metrics.gradeSatisfaction?.toFloat(),
            usefulness = review.metrics.gains?.toFloat(),
            teaching = review.metrics.teachingSkill?.toFloat(),
            ease = review.metrics.lifeBalance?.toFloat(),
        )
        Text(
            text = review.content,
            style = SNUTTTypography.body1.copy(lineHeight = 20.sp, color = SNUTTColors.TextPlain),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        ReviewLikeButton(
            count = review.likeCount.toInt(),
            selected = review.isLiked,
            onClick = onLikeClick,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewDetailedCard_Default() {
    SnuttPreviewCenteredSurface {
        ReviewDetailedCard(
            review = ReviewPreviewData.reviews.first(),
            onLikeClick = {},
            onMoreClick = {},
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}
