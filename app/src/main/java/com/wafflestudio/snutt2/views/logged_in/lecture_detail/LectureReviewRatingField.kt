package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.StarIcon
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
internal fun LectureReviewRatingField(
    reviewInfo: LectureReviewInfo?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        LectureDetailItem(
            title = stringResource(R.string.lecture_detail_review_rating),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                StarIcon(
                    filled = true,
                    modifier = Modifier
                        .size(18.dp)
                        .offset(y = 1.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = SNUTTColors.Black900)) {
                            append(reviewInfo?.rating?.let { "%.1f".format(it) } ?: "--")
                            append(" ")
                        }
                        withStyle(SpanStyle(color = SNUTTColors.Gray2)) {
                            append(
                                stringResource(
                                    R.string.lecture_detail_review_count,
                                    reviewInfo?.reviewCount ?: 0,
                                ),
                            )
                        }
                    },
                    style = MaterialTheme.typography.body1.copy(fontSize = 15.sp),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "reviewInfo 있음")
@Composable
private fun WithReviewInfoPreview() {
    LectureReviewRatingField(
        reviewInfo = PreviewData.sampleReviewInfo,
    )
}

@Preview(showBackground = true, widthDp = 360, name = "reviewInfo 없음")
@Composable
private fun WithoutReviewInfoPreview() {
    LectureReviewRatingField(
        reviewInfo = null,
    )
}

@Preview(showBackground = true, widthDp = 360, name = "강의퍙 없음")
@Composable
private fun WithZeroReviewInfoPreview() {
    LectureReviewRatingField(
        reviewInfo = LectureReviewInfo(
            id = "",
            rating = null,
            reviewCount = 0,
        ),
    )
}
