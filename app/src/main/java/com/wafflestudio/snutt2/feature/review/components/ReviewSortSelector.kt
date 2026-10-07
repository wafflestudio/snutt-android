package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

enum class ReviewSortOrder {
    RECOMMENDED,
    LATEST,
}

@Composable
fun ReviewSortSelector(
    selectedOrder: ReviewSortOrder,
    onOrderSelected: (ReviewSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ReviewSortOption(stringResource(R.string.review_sort_recommended), selectedOrder == ReviewSortOrder.RECOMMENDED) {
            onOrderSelected(ReviewSortOrder.RECOMMENDED)
        }
        ReviewSortOption(stringResource(R.string.review_sort_latest), selectedOrder == ReviewSortOrder.LATEST) {
            onOrderSelected(ReviewSortOrder.LATEST)
        }
    }
}

@Composable
private fun ReviewSortOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val selectedColor = SNUTTColors.TextPlain
    val unselectedColor = SNUTTColors.TextAssistive
    Row(
        modifier = Modifier.clicks(onClick = onClick).padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "●",
            style = SNUTTTypography.body2.copy(
                fontSize = 4.sp,
                lineHeight = 4.sp,
                color = if (selected) selectedColor else unselectedColor,
            ),
            modifier = Modifier.width(4.dp),
        )
        Text(
            text = label,
            style = SNUTTTypography.body1.copy(color = if (selected) selectedColor else unselectedColor),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewSortSelector_Recommended() {
    SnuttPreviewCenteredSurface {
        ReviewSortSelector(ReviewSortOrder.RECOMMENDED, {})
    }
}
