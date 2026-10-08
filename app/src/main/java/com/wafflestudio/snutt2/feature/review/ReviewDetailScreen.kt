package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.ReviewedCourse
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.feature.review.components.ReviewDetailedCard
import com.wafflestudio.snutt2.feature.review.components.ReviewEmptyReviewPrompt
import com.wafflestudio.snutt2.feature.review.components.ReviewSortOrder
import com.wafflestudio.snutt2.feature.review.components.ReviewSortSelector
import com.wafflestudio.snutt2.feature.review.components.ReviewStarRating
import com.wafflestudio.snutt2.feature.review.components.ReviewWriteButton
import com.wafflestudio.snutt2.ui.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewDetailRoute(
    onNavigateBack: () -> Unit,
    onNavigateWrite: () -> Unit,
) {
    ReviewDetailScreen(
        course = null,
        reviews = emptyList(),
        sortOrder = ReviewSortOrder.RECOMMENDED,
        onBackClick = onNavigateBack,
        onWriteClick = onNavigateWrite,
        onSortSelected = {},
        onLikeClick = {},
        onMoreClick = {},
        highlightedReviewId = null,
        modifier = Modifier.fillMaxHeight(0.95f),
    )
}

@Composable
fun ReviewDetailScreen(
    course: ReviewedCourse?,
    reviews: List<LectureReview>,
    sortOrder: ReviewSortOrder,
    onBackClick: () -> Unit,
    onWriteClick: () -> Unit,
    onSortSelected: (ReviewSortOrder) -> Unit,
    onLikeClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    highlightedReviewId: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(SNUTTColors.White900)) {
        CenteredTopBar(
            height = 47.dp,
            title = { Text(stringResource(R.string.review_detail_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900)) },
            navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clicks { onBackClick() }) {
                    SnuttIcon(R.drawable.ic_arrow_back, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    Text(stringResource(R.string.reviews_app_bar_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900))
                }
            },
        )
        if (course != null) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(32.dp))
                Text(course.title, style = SNUTTTypography.h2.copy(fontSize = 18.sp), maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(course.instructor, style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative))
                Spacer(Modifier.height(4.dp))
                Text(
                    listOfNotNull(
                        course.department?.takeIf { it.isNotBlank() },
                        course.category?.takeIf { it.isNotBlank() },
                        course.credit?.let { stringResource(R.string.review_credits, it) },
                    ).joinToString(" · "),
                    style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative),
                )
                Spacer(Modifier.height(28.dp))
                Divider(thickness = 0.5.dp, color = SNUTTColors.LineLight)
                Spacer(Modifier.height(28.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = course.averageRating?.let { "%.1f".format(it) } ?: "--",
                                style = SNUTTTypography.h2.copy(fontSize = 20.sp),
                            )
                            Text(
                                text = "(${course.reviewCount})",
                                style = SNUTTTypography.body2.copy(color = SNUTTColors.TextAlternative),
                            )
                        }
                        ReviewStarRating(rating = course.averageRating?.toFloat() ?: 0f, onRatingChange = null, starSize = 18.dp, starSpacing = 1.dp)
                    }
                    course.averageMetrics?.let { metrics ->
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                ReviewSummaryMetric(stringResource(R.string.review_metric_grade), metrics.gradeSatisfaction)
                                ReviewSummaryMetric(stringResource(R.string.review_metric_usefulness), metrics.gains)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                ReviewSummaryMetric(stringResource(R.string.review_metric_teaching), metrics.teachingSkill)
                                ReviewSummaryMetric(stringResource(R.string.review_metric_ease), metrics.lifeBalance)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            Divider(thickness = 7.dp, color = SNUTTColors.Gray400)
        }
        ReviewSortSelector(
            selectedOrder = sortOrder,
            onOrderSelected = onSortSelected,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn {
                if (reviews.isEmpty() && course != null) {
                    item {
                        ReviewEmptyReviewPrompt(
                            courseTitle = course.title,
                            onClick = onWriteClick,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }
                items(reviews) { review ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (review.id.toString() == highlightedReviewId) SNUTTColors.MainBlue.copy(alpha = 0.04f) else SNUTTColors.White900,
                            ),
                    ) {
                        ReviewDetailedCard(
                            review = review,
                            onLikeClick = { onLikeClick(review.id.toString()) },
                            onMoreClick = { onMoreClick(review.id.toString()) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        )
                    }
                    Divider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = SNUTTColors.LineLight)
                }
            }
        }
        ReviewWriteButton(onClick = onWriteClick, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ReviewSummaryMetric(label: String, value: Double?) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        SnuttIcon(R.drawable.ic_star_filled, modifier = Modifier.size(14.dp), colorFilter = ColorFilter.tint(SNUTTColors.Yellow))
        Text(value?.let { "%.1f".format(it) } ?: "-", style = SNUTTTypography.body1.copy(fontWeight = FontWeight.SemiBold))
        Text(label, style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative))
    }
}

@SnuttPreview
@Composable
private fun ReviewDetailScreen_Default() {
    SnuttPreviewSurface {
        ReviewDetailScreen(
            course = ReviewPreviewData.course,
            reviews = ReviewPreviewData.reviews,
            sortOrder = ReviewSortOrder.RECOMMENDED,
            onBackClick = {},
            onWriteClick = {},
            onSortSelected = {},
            onLikeClick = {},
            onMoreClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewDetailScreen_Submitted() {
    SnuttPreviewSurface {
        ReviewDetailScreen(
            course = ReviewPreviewData.course,
            reviews = ReviewPreviewData.reviews,
            sortOrder = ReviewSortOrder.RECOMMENDED,
            onBackClick = {},
            onWriteClick = {},
            onSortSelected = {},
            onLikeClick = {},
            onMoreClick = {},
            highlightedReviewId = ReviewPreviewData.reviews.first().id.toString(),
        )
    }
}
