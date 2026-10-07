package com.wafflestudio.snutt2.feature.review

import androidx.compose.runtime.Composable

@Composable
fun ReviewRoute(
    bottomBar: @Composable () -> Unit = {},
    onNavigateSearch: () -> Unit,
    onNavigateAll: () -> Unit,
    onNavigateMy: () -> Unit,
    onNavigateDetail: (String) -> Unit,
    onNavigateWrite: (String) -> Unit,
) {
    ReviewHomeScreen(
        reviews = emptyList(),
        courses = emptyList(),
        reviewsByCourse = emptyMap(),
        filters = emptyList(),
        semesterTitle = "",
        sortOrder = com.wafflestudio.snutt2.feature.review.components.ReviewSortOrder.RECOMMENDED,
        bottomBar = bottomBar,
        onSearchClick = onNavigateSearch,
        onFilterClick = {},
        onAllClick = onNavigateAll,
        onMyClick = onNavigateMy,
        onReviewClick = onNavigateDetail,
        onWriteClick = onNavigateWrite,
        onRemoveFilter = {},
        onSortSelected = {},
        onLikeClick = {},
        onMoreClick = {},
    )
}
