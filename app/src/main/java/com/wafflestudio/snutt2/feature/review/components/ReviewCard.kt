package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewCard(
    title: String,
    professor: String,
    rating: Float,
    content: String,
    semester: String,
    likeCount: Int,
    liked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column {
            Text(
                text = title,
                style = SNUTTTypography.h4.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
            Text(
                text = professor,
                style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.TextAlternative),
                maxLines = 1,
            )
        }
        Column {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ReviewStarRating(rating = rating, onRatingChange = null, starSize = 16.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = content,
                        style = SNUTTTypography.body1.copy(lineHeight = 20.sp, color = SNUTTColors.TextPlain),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = semester,
                        style = SNUTTTypography.body2.copy(fontSize = 13.sp, color = SNUTTColors.Gray30),
                    )
                }
            }
            ReviewLikeButton(
                count = likeCount,
                selected = liked,
                onClick = onLikeClick,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun ReviewCard_Overview() {
    SnuttPreviewCenteredSurface {
        ReviewCard(
            title = "편집디자인",
            professor = "교수명",
            rating = 3.5f,
            content = "강의평 내용을 입력하세요. 강의평 내용을 입력하세요. 강의평 내용을 입력하세요.",
            semester = "25년 2학기",
            likeCount = 12,
            liked = false,
            onLikeClick = {},
        )
    }
}
