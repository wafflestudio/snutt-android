package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

data class ReviewFilterOption(
    val label: String,
    val selected: Boolean,
)

@Composable
fun ReviewFilterSheetContent(
    categories: List<String>,
    selectedCategory: String,
    options: List<ReviewFilterOption>,
    onCategorySelected: (String) -> Unit,
    onOptionToggle: (String) -> Unit,
    onApplyClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(541.dp)
            .background(SNUTTColors.White900),
    ) {
        Column(modifier = Modifier.weight(1f).padding(20.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                SnuttIcon(
                    R.drawable.ic_close,
                    modifier = Modifier.align(Alignment.CenterEnd).size(31.dp).clicks(onClick = onCloseClick),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    contentDescription = stringResource(R.string.review_filter_close),
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                Column(modifier = Modifier.width(59.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    categories.forEach { category ->
                        Text(
                            text = category,
                            style = SNUTTTypography.h2.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (category == selectedCategory) SNUTTColors.Black900 else SNUTTColors.TextAssistive,
                            ),
                            modifier = Modifier.clicks { onCategorySelected(category) },
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clicks { onOptionToggle(option.label) },
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(if (option.selected) SNUTTColors.LineDisabled else SNUTTColors.Gray400, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                            Text(option.label, style = SNUTTTypography.body1.copy(color = SNUTTColors.Black900))
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(92.dp).background(SNUTTColors.MainBlue).clicks(onClick = onApplyClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.review_filter_apply),
                style = SNUTTTypography.h3.copy(color = Color.White),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun ReviewFilterSheetContent_Default() {
    SnuttPreviewSurface {
        ReviewFilterSheetContent(
            categories = listOf("분류", "학과", "학년", "학점", "시간", "교양분류", "기타"),
            selectedCategory = "학과",
            options = listOf(
                ReviewFilterOption("AI융합교육과", false),
                ReviewFilterOption("건축학과", true),
                ReviewFilterOption("간호학과(간호학전공)", true),
                ReviewFilterOption("건설환경공학부", true),
            ),
            onCategorySelected = {},
            onOptionToggle = {},
            onApplyClick = {},
            onCloseClick = {},
        )
    }
}
