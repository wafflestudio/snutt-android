package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.ReviewedCourse
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.feature.review.components.ReviewCard
import com.wafflestudio.snutt2.feature.review.components.ReviewDetailedCard
import com.wafflestudio.snutt2.feature.review.components.ReviewEmptyCourseCard
import com.wafflestudio.snutt2.feature.review.components.ReviewFilterTag
import com.wafflestudio.snutt2.feature.review.components.ReviewLectureListItem
import com.wafflestudio.snutt2.feature.review.components.ReviewSearchField
import com.wafflestudio.snutt2.feature.review.components.ReviewSortOrder
import com.wafflestudio.snutt2.feature.review.components.ReviewSortSelector
import com.wafflestudio.snutt2.feature.review.components.ReviewWriteButton
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
fun ReviewHomeScreen(
    reviews: List<LectureReview>,
    courses: List<ReviewedCourse>,
    reviewsByCourse: Map<Long, List<LectureReview>>,
    filters: List<String>,
    semesterTitle: String,
    sortOrder: ReviewSortOrder,
    bottomBar: @Composable () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAllClick: () -> Unit,
    onMyClick: () -> Unit,
    onReviewClick: (String) -> Unit,
    onWriteClick: (String) -> Unit,
    onRemoveFilter: (String) -> Unit,
    onSortSelected: (ReviewSortOrder) -> Unit,
    onLikeClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(SNUTTColors.White900)) {
        CenteredTopBar(
            height = 47.dp,
            title = {
                Text(stringResource(R.string.reviews_app_bar_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900))
            },
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Spacer(Modifier.height(16.dp))
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                ) {
                    items(filters) { filter -> ReviewFilterTag(filter, onRemove = { onRemoveFilter(filter) }) }
                }
                Spacer(Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(horizontal = 20.dp), color = SNUTTColors.LineLight, thickness = 0.5.dp)
                Spacer(Modifier.height(12.dp))
                ReviewSortSelector(sortOrder, onOrderSelected = onSortSelected, modifier = Modifier.padding(horizontal = 20.dp))
            }
            items(reviews) { review ->
                Spacer(Modifier.height(12.dp))
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
                Divider(modifier = Modifier.padding(horizontal = 20.dp), color = SNUTTColors.LineLight, thickness = 0.5.dp)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clicks(onClick = onAllClick).padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.review_view_all),
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.Gray30),
                    )
                    SnuttIcon(
                        R.drawable.ic_arrow_down,
                        modifier = Modifier.size(18.dp).rotate(-90f),
                        colorFilter = ColorFilter.tint(SNUTTColors.Gray30),
                    )
                }
                Divider(color = SNUTTColors.Gray400, thickness = 6.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(39.dp)
                        .background(SNUTTColors.MilkMint.copy(alpha = 31f / 255f))
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(semesterTitle, style = SNUTTTypography.body1.copy(color = SNUTTColors.DarkMintText))
                    Row(
                        modifier = Modifier.clicks(onClick = onMyClick),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.review_my_reviews),
                            style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative),
                        )
                        SnuttIcon(
                            R.drawable.ic_arrow_down,
                            modifier = Modifier.size(18.dp).rotate(-90f),
                            colorFilter = ColorFilter.tint(SNUTTColors.TextAlternative),
                        )
                    }
                }
            }
            items(courses) { course ->
                val courseReviews = reviewsByCourse[course.courseId].orEmpty()
                if (courseReviews.isEmpty()) {
                    ReviewEmptyCourseCard(
                        course = course,
                        onCourseClick = { onReviewClick(course.courseId.toString()) },
                        onWriteClick = { onWriteClick(course.courseId.toString()) },
                        modifier = Modifier.padding(horizontal = 20.dp).padding(top = 8.dp, bottom = 20.dp),
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(91.dp).padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ReviewLectureListItem(
                            title = course.title,
                            professor = course.instructor,
                            rating = course.averageRating,
                            reviewCount = course.reviewCount.toInt(),
                            onClick = { onReviewClick(course.courseId.toString()) },
                            modifier = Modifier.weight(1f),
                        )
                        ReviewWriteButton(onClick = { onWriteClick(course.courseId.toString()) }, cornerRadius = 4.dp)
                    }
                    courseReviews.forEach { review ->
                        ReviewDetailedCard(
                            review = review,
                            onLikeClick = { onLikeClick(review.id.toString()) },
                            onMoreClick = { onMoreClick(review.id.toString()) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        )
                    }
                }
                Divider(modifier = Modifier.padding(horizontal = 20.dp), color = SNUTTColors.LineLight, thickness = 0.5.dp)
            }
        }
        bottomBar()
    }
}

@SnuttPreview
@Composable
private fun ReviewHomeScreen_Default() {
    SnuttPreviewSurface {
        ReviewHomeScreen(
            reviews = ReviewPreviewData.reviews,
            courses = ReviewPreviewData.searchResults,
            reviewsByCourse = mapOf(
                1L to listOf(ReviewPreviewData.reviews[0]),
                2L to listOf(ReviewPreviewData.reviews[1]),
            ),
            filters = listOf("1학년", "디자인과", "3학점", "2학점"),
            semesterTitle = "25년 2학기 수강한 강의",
            sortOrder = ReviewSortOrder.RECOMMENDED,
            bottomBar = {},
            onSearchClick = {},
            onFilterClick = {},
            onAllClick = {},
            onMyClick = {},
            onReviewClick = {},
            onWriteClick = {},
            onRemoveFilter = {},
            onSortSelected = {},
            onLikeClick = {},
            onMoreClick = {},
        )
    }
}
