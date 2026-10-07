package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.EvaluatedCourse
import com.wafflestudio.snutt2.feature.review.components.ReviewFilterTag
import com.wafflestudio.snutt2.feature.review.components.ReviewLectureListItem
import com.wafflestudio.snutt2.feature.review.components.ReviewSearchField
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewSearchRoute(
    onNavigateBack: () -> Unit,
    onNavigateDetail: (String) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    ReviewSearchScreen(
        query = "",
        results = emptyList(),
        filters = emptyList(),
        onQueryChange = {},
        onBackClick = onNavigateBack,
        onFilterClick = {},
        onClearClick = {},
        onSearchSubmit = {},
        onResultClick = onNavigateDetail,
        onRemoveFilter = {},
        bottomBar = bottomBar,
    )
}

@Composable
fun ReviewSearchScreen(
    query: String,
    results: List<EvaluatedCourse>,
    filters: List<String>,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchSubmit: () -> Unit,
    onResultClick: (String) -> Unit,
    onRemoveFilter: (String) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(SNUTTColors.White900)) {
        Spacer(Modifier.height(12.dp))
        ReviewSearchField(
            query = query,
            isSearching = true,
            onQueryChange = onQueryChange,
            onSearchClick = {},
            onBackClick = onBackClick,
            onClearClick = onClearClick,
            onFilterClick = onFilterClick,
            onSearchSubmit = onSearchSubmit,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filters) { filter -> ReviewFilterTag(filter, onRemove = { onRemoveFilter(filter) }) }
        }
        Spacer(Modifier.height(16.dp))
        if (results.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Image(
                        painter = painterResource(R.drawable.img_search_big),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(SNUTTColors.Gray10),
                        modifier = Modifier.size(95.dp),
                    )
                    Text(
                        text = stringResource(if (query.isEmpty()) R.string.review_search_empty else R.string.review_search_no_results),
                        style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.TextAssistive),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 20.dp)) {
                item { Divider(thickness = 0.5.dp, color = SNUTTColors.LineLight) }
                items(results) { course ->
                    ReviewLectureListItem(
                        title = course.lecture.courseTitle,
                        professor = course.lecture.instructor,
                        rating = course.averageRating,
                        reviewCount = course.reviewCount.toInt(),
                        onClick = { onResultClick(course.courseId.toString()) },
                    )
                    Divider(thickness = 0.5.dp, color = SNUTTColors.LineLight)
                }
            }
        }
        bottomBar()
    }
}

@SnuttPreview
@Composable
private fun ReviewSearchScreen_Results() {
    SnuttPreviewSurface {
        ReviewSearchScreen(
            query = "인공지능",
            results = ReviewPreviewData.searchResults,
            filters = listOf("1학년", "디자인과", "3학점"),
            onQueryChange = {},
            onBackClick = {},
            onFilterClick = {},
            onClearClick = {},
            onSearchSubmit = {},
            onResultClick = {},
            onRemoveFilter = {},
            bottomBar = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewSearchScreen_Empty() {
    SnuttPreviewSurface {
        ReviewSearchScreen(
            query = "",
            results = emptyList(),
            filters = listOf("1학년", "디자인과", "3학점"),
            onQueryChange = {},
            onBackClick = {},
            onFilterClick = {},
            onClearClick = {},
            onSearchSubmit = {},
            onResultClick = {},
            onRemoveFilter = {},
            bottomBar = {},
        )
    }
}
