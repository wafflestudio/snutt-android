package com.wafflestudio.snutt2.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.getSimplifiedClassTimeForLecture
import com.wafflestudio.snutt2.ui.util.formatter.getSimplifiedLocation

@Composable
fun SearchLectureListItem(
    lecture: SearchedLecture,
    lectureState: LectureState,
    onClick: () -> Unit,
    onClickDetail: () -> Unit,
    onClickReview: () -> Unit,
    onClickBookmark: () -> Unit,
    onClickVacancy: () -> Unit,
    onClickAddOrRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(if (lectureState.selected) SNUTTColors.Dim2 else SNUTTColors.Transparent)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .clicks { onClick() },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 제목 + 교수/학점
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lecture.courseTitle,
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.h4,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.search_result_item_instructor_credit_text, lecture.instructor, lecture.credit),
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // 태그 + 평점
            Row(verticalAlignment = Alignment.CenterVertically) {
                SnuttIcon(
                    R.drawable.ic_tag,
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = listOf(lecture.category, lecture.department, lecture.academicYear)
                        .filter { it.isNotBlank() }
                        .ifEmpty { listOf("(없음)") }
                        .joinToString(", "),
                    modifier = Modifier.weight(1f),
                    color = SNUTTColors.AllWhite,
                    fontWeight = FontWeight.Light,
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                ReviewRating(reviewInfo = lecture.reviewInfo)
            }
            // 시간
            LectureInfoRow(
                icon = {
                    SnuttIcon(
                        R.drawable.ic_clock,
                        modifier = Modifier.size(15.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                },
                text = getSimplifiedClassTimeForLecture(LocalContext.current, lecture),
            )
            // 장소
            LectureInfoRow(
                icon = {
                    SnuttIcon(
                        R.drawable.ic_location,
                        modifier = Modifier.size(15.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                },
                text = getSimplifiedLocation(LocalContext.current, lecture),
            )
            // 비고
            LectureInfoRow(
                icon = {
                    SnuttIcon(
                        R.drawable.ic_remark,
                        modifier = Modifier.size(15.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                },
                text = lecture.remark.ifEmpty { "없음" },
            )
        }

        AnimatedVisibility(visible = lectureState.selected) {
            LectureActionBar(
                contained = lectureState.contained,
                isBookmarked = lectureState.isBookmarked,
                isVacancyRegistered = lectureState.isVacancyRegistered,
                onClickDetail = onClickDetail,
                onClickReview = onClickReview,
                onClickBookmark = onClickBookmark,
                onClickVacancy = onClickVacancy,
                onClickAddOrRemove = onClickAddOrRemove,
            )
        }

        Divider(color = SNUTTColors.White400)
    }
}

@Composable
private fun ReviewRating(reviewInfo: LectureReviewInfo) {
    val ratingText = reviewInfo.rating?.let {
        if (reviewInfo.rating > 0) {
            "${(reviewInfo.rating * 10).toInt() / 10.0}"
        } else {
            "--"
        }
    } ?: "--"
    val displayText = "$ratingText (${reviewInfo.reviewCount})"

    Row(verticalAlignment = Alignment.CenterVertically) {
        SnuttIcon(
            R.drawable.ic_star_outline,
            modifier = Modifier
                .size(12.dp)
                .offset(y = 1.dp),
            colorFilter = ColorFilter.tint(SNUTTColors.White),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = displayText,
            color = SNUTTColors.White,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun LectureInfoRow(
    icon: @Composable () -> Unit,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = SNUTTTypography.body2.copy(
                color = SNUTTColors.AllWhite,
                fontWeight = FontWeight.Light,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LectureActionBar(
    contained: Boolean,
    isBookmarked: Boolean,
    isVacancyRegistered: Boolean,
    onClickDetail: () -> Unit,
    onClickReview: () -> Unit,
    onClickBookmark: () -> Unit,
    onClickVacancy: () -> Unit,
    onClickAddOrRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LectureActionButton(
            title = stringResource(R.string.search_result_item_detail_button),
            modifier = Modifier.weight(1f),
            onClick = onClickDetail,
        ) {
            SnuttIcon(
                R.drawable.ic_detail,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
            )
        }
        Spacer(modifier = Modifier.weight(0.3f))
        LectureActionButton(
            title = stringResource(R.string.search_result_item_review_button),
            modifier = Modifier.weight(1f),
            onClick = onClickReview,
        ) {
            SnuttIcon(
                R.drawable.ic_review_thick,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
            )
        }
        Spacer(modifier = Modifier.weight(0.3f))
        LectureActionButton(
            title = stringResource(R.string.search_result_item_bookmark_button),
            modifier = Modifier.weight(1f),
            onClick = onClickBookmark,
        ) {
            SnuttIcon(
                if (isBookmarked) R.drawable.ic_bookmark_selected else R.drawable.ic_bookmark_unselected,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
            )
        }
        Spacer(modifier = Modifier.weight(0.3f))
        LectureActionButton(
            title = stringResource(R.string.search_result_item_vacancy_button),
            modifier = Modifier.weight(1f),
            onClick = onClickVacancy,
        ) {
            SnuttIcon(
                if (isVacancyRegistered) R.drawable.ic_ringing_alarm_selected else R.drawable.ic_ringing_alarm_unselected,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
            )
        }
        Spacer(modifier = Modifier.weight(0.3f))
        LectureActionButton(
            title = if (contained) {
                stringResource(R.string.search_result_item_remove_button)
            } else {
                stringResource(R.string.search_result_item_add_button)
            },
            modifier = Modifier.weight(1f),
            onClick = onClickAddOrRemove,
        ) {
            if (contained) {
                SnuttIcon(
                    R.drawable.ic_remove_circle,
                    modifier = Modifier.size(23.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
            } else {
                SnuttIcon(
                    R.drawable.ic_add_circle,
                    modifier = Modifier.size(23.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
            }
        }
    }
}

@Composable
private fun LectureActionButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.clicks { onClick() },
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
        Text(
            text = title,
            style = SNUTTTypography.body2.copy(
                color = SNUTTColors.AllWhite,
                fontSize = 10.sp,
            ),
        )
    }
}

// region Preview

@SnuttPreview
@Composable
private fun SearchLectureListItem_Collapsed() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.background(SNUTTColors.Dim2)) {
            SearchLectureListItem(
                lecture = PreviewData.sampleLectures.first(),
                lectureState = LectureState(
                    selected = false,
                    contained = false,
                    isBookmarked = false,
                    isVacancyRegistered = false,
                ),
                onClick = {},
                onClickDetail = {},
                onClickReview = {},
                onClickBookmark = {},
                onClickVacancy = {},
                onClickAddOrRemove = {},
            )
        }
    }
}

@SnuttPreview
@Composable
private fun SearchLectureListItem_Expanded() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.background(SNUTTColors.Dim2)) {
            SearchLectureListItem(
                lecture = PreviewData.sampleLectures.first(),
                lectureState = LectureState(
                    selected = true,
                    contained = false,
                    isBookmarked = false,
                    isVacancyRegistered = false,
                ),
                onClick = {},
                onClickDetail = {},
                onClickReview = {},
                onClickBookmark = {},
                onClickVacancy = {},
                onClickAddOrRemove = {},
            )
        }
    }
}

@SnuttPreview
@Composable
private fun SearchLectureListItem_ExpandedContained() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.background(SNUTTColors.Dim2)) {
            SearchLectureListItem(
                lecture = PreviewData.sampleLectures.first(),
                lectureState = LectureState(
                    selected = true,
                    contained = true,
                    isBookmarked = true,
                    isVacancyRegistered = true,
                ),
                onClick = {},
                onClickDetail = {},
                onClickReview = {},
                onClickBookmark = {},
                onClickVacancy = {},
                onClickAddOrRemove = {},
            )
        }
    }
}

// endregion
