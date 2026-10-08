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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.review.ReviewedCourse
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewEmptyCourseCard(
    course: ReviewedCourse,
    onCourseClick: () -> Unit,
    onWriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(75.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReviewLectureListItem(
                title = course.title,
                professor = course.instructor,
                rating = null,
                reviewCount = 0,
                onClick = onCourseClick,
                modifier = Modifier.weight(1f),
            )
            ReviewWriteButton(onClick = onWriteClick, cornerRadius = 4.dp)
        }
        ReviewEmptyReviewPrompt(course.title, onClick = onWriteClick)
    }
}

@Composable
fun ReviewEmptyReviewPrompt(
    courseTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(124.dp)
            .background(SNUTTColors.MainBlue.copy(alpha = 0.04f), RoundedCornerShape(4.dp))
            .clicks(onClick = onClick)
            .padding(top = 22.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(30.dp).background(SNUTTColors.MilkMint.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            SnuttIcon(R.drawable.ic_write, modifier = Modifier.size(16.dp), colorFilter = ColorFilter.tint(SNUTTColors.MainBlue))
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = courseTitle,
            style = SNUTTTypography.body1.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = SNUTTColors.MainBlue),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.review_first_review),
            style = SNUTTTypography.body1.copy(fontSize = 14.sp, color = SNUTTColors.Gray30),
            textAlign = TextAlign.Center,
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewEmptyCourseCard_Default() {
    SnuttPreviewCenteredSurface {
        ReviewEmptyCourseCard(
            course = ReviewPreviewData.course.copy(title = "죽음의 과학적 이해", instructor = "교수명", averageRating = null, reviewCount = 0, averageMetrics = null),
            onCourseClick = {},
            onWriteClick = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}
