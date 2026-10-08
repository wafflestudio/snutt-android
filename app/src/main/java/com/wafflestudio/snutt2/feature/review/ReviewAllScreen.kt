package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.feature.review.components.ReviewCard
import com.wafflestudio.snutt2.feature.review.components.ReviewFilterTag
import com.wafflestudio.snutt2.feature.review.components.ReviewSearchField
import com.wafflestudio.snutt2.feature.review.components.ReviewSortOrder
import com.wafflestudio.snutt2.feature.review.components.ReviewSortSelector
import com.wafflestudio.snutt2.ui.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toShortYearFormattedString

@Composable
fun ReviewAllRoute(
    onNavigateSearch: () -> Unit,
    onNavigateDetail: (String) -> Unit,
) {
    ReviewAllScreen(
        reviews = emptyList(),
        filters = emptyList(),
        sortOrder = ReviewSortOrder.RECOMMENDED,
        onSearchClick = onNavigateSearch,
        onFilterClick = {},
        onReviewClick = onNavigateDetail,
        onRemoveFilter = {},
        onSortSelected = {},
        onLikeClick = {},
    )
}

@Composable
fun ReviewAllScreen(
    reviews: List<LectureReview>,
    filters: List<String>,
    sortOrder: ReviewSortOrder,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onReviewClick: (String) -> Unit,
    onRemoveFilter: (String) -> Unit,
    onSortSelected: (ReviewSortOrder) -> Unit,
    onLikeClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(SNUTTColors.White900)) {
        CenteredTopBar(
            height = 47.dp,
            title = {
                Text(stringResource(R.string.reviews_app_bar_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900))
            },
        )
        LazyColumn {
            item {
                Spacer(Modifier.height(12.dp))
                ReviewSearchField(
                    query = "",
                    isSearching = false,
                    onQueryChange = {},
                    onSearchClick = onSearchClick,
                    onBackClick = {},
                    onClearClick = {},
                    onFilterClick = onFilterClick,
                    onSearchSubmit = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filters) { filter -> ReviewFilterTag(filter, onRemove = { onRemoveFilter(filter) }) }
                }
                Spacer(Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = SNUTTColors.LineLight)
                Spacer(Modifier.height(16.dp))
                ReviewSortSelector(sortOrder, onSortSelected, modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))
            }
            items(reviews) { review ->
                ReviewCard(
                    title = review.courseTitle,
                    professor = review.instructor,
                    rating = review.rating.toFloat(),
                    content = review.content,
                    semester = review.courseBook.toShortYearFormattedString(LocalContext.current),
                    likeCount = review.likeCount.toInt(),
                    liked = review.isLiked,
                    onLikeClick = { onLikeClick(review.id.toString()) },
                    modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp).clicks { onReviewClick(review.courseId.toString()) },
                )
                Spacer(Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = SNUTTColors.LineLight)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@SnuttPreview
@Composable
private fun ReviewAllScreen_Default() {
    SnuttPreviewSurface {
        ReviewAllScreen(
            reviews = ReviewPreviewData.reviews + ReviewPreviewData.reviews,
            filters = listOf("1학년", "디자인과", "3학점", "2학점"),
            sortOrder = ReviewSortOrder.RECOMMENDED,
            onSearchClick = {},
            onFilterClick = {},
            onReviewClick = {},
            onRemoveFilter = {},
            onSortSelected = {},
            onLikeClick = {},
        )
    }
}
