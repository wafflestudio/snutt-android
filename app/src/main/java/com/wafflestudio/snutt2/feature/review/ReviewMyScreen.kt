package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.feature.review.components.ReviewDetailedCard
import com.wafflestudio.snutt2.ui.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toShortYearFormattedString

@Composable
fun ReviewMyRoute(onNavigateBack: () -> Unit, bottomBar: @Composable () -> Unit) {
    ReviewMyScreen(
        groups = emptyMap(),
        onBackClick = onNavigateBack,
        onLikeClick = {},
        onMoreClick = {},
        bottomBar = bottomBar,
    )
}

@Composable
fun ReviewMyScreen(
    groups: Map<CourseBook, List<LectureReview>>,
    onBackClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(SNUTTColors.White900)) {
        CenteredTopBar(
            height = 47.dp,
            title = { Text(stringResource(R.string.review_my_reviews), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900)) },
            navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clicks { onBackClick() }) {
                    SnuttIcon(R.drawable.ic_arrow_back, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    Text(stringResource(R.string.reviews_app_bar_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900))
                }
            },
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            groups.forEach { (courseBook, reviews) ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(courseBook.toShortYearFormattedString(LocalContext.current), style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative))
                        Text(
                            stringResource(R.string.review_count_format, reviews.size),
                            style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative),
                        )
                    }
                }
                items(reviews) { review ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(review.courseTitle, style = SNUTTTypography.h4.copy(fontSize = 15.sp))
                            Text(
                                review.instructor,
                                style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative),
                            )
                        }
                        ReviewDetailedCard(
                            review = review,
                            onLikeClick = { onLikeClick(review.id.toString()) },
                            onMoreClick = { onMoreClick(review.id.toString()) },
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                            showSemester = false,
                        )
                        Divider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = SNUTTColors.LineLight)
                    }
                }
            }
        }
        bottomBar()
    }
}

@SnuttPreview
@Composable
private fun ReviewMyScreen_Default() {
    SnuttPreviewSurface {
        ReviewMyScreen(
            groups = ReviewPreviewData.semesterGroups,
            onBackClick = {},
            onLikeClick = {},
            onMoreClick = {},
            bottomBar = {},
        )
    }
}
