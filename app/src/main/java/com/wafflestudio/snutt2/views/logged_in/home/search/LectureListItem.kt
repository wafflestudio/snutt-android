package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddCircleIcon
import com.wafflestudio.snutt2.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.components.compose.ClockIcon
import com.wafflestudio.snutt2.components.compose.DetailIcon
import com.wafflestudio.snutt2.components.compose.LocationIcon
import com.wafflestudio.snutt2.components.compose.RemarkIcon
import com.wafflestudio.snutt2.components.compose.RemoveCircleIcon
import com.wafflestudio.snutt2.components.compose.RingingAlarmIcon
import com.wafflestudio.snutt2.components.compose.StarIcon
import com.wafflestudio.snutt2.components.compose.TagIcon
import com.wafflestudio.snutt2.components.compose.ThickReviewIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlin.text.ifEmpty

@Composable
fun LazyItemScope.ExpandableLectureListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureState>,
    onToggleLectureSelection: (LectureDto) -> Unit,
    onClickLectureDetail: (LectureDto) -> Unit,
    onClickReview: (LectureDto) -> Unit,
    onClickBookmark: (LectureDto, Boolean) -> Unit,
    onClickVacancy: (LectureDto, Boolean) -> Unit,
    onToggleLectureContained: (LectureDto, Boolean) -> Unit,
) {
    val lecture = lectureDataWithState.item
    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained
    val isBookmarked = lectureDataWithState.state.isBookmarked
    val isVacancyRegistered = lectureDataWithState.state.isVacancyRegistered

    val lectureTitle = lecture.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithState.item.instructor,
        lectureDataWithState.item.credit,
    )
    val remarkText = lectureDataWithState.item.remark
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDataWithState.item)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTimeForLecture(lectureDataWithState.item)
    val backgroundColor = if (selected) SNUTTColors.Dim2 else SNUTTColors.Transparent

    Column(
        modifier = Modifier
            .animateItem(
                placementSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .clicks {
                    onToggleLectureSelection(lecture)
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.h4,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructorCreditText,
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = tagText,
                    modifier = Modifier.weight(1f),
                    color = SNUTTColors.AllWhite,
                    fontWeight = FontWeight.Light,
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StarIcon(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(y = 1.dp),
                        filled = false,
                        colorFilter = ColorFilter.tint(SNUTTColors.White),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = lectureDataWithState.item.review?.displayText ?: "-- (0)", // TODO: dto대신 model 사용하고, review를 non-nullable로 만들기
                        color = SNUTTColors.White,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        style = SNUTTTypography.body2,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = classTimeText,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light,
                    ),
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LocationIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = SNUTTStringUtils.getSimplifiedLocation(lectureDataWithState.item),
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light,
                    ),
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemarkIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = remarkText.ifEmpty { "없음" },
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        AnimatedVisibility(visible = lectureDataWithState.state.selected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_detail_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClickLectureDetail(lecture)
                    },
                ) {
                    DetailIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_review_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClickReview(lecture)
                    },
                ) {
                    ThickReviewIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_bookmark_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClickBookmark(lecture, isBookmarked)
                    },
                ) {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(23.dp),
                        marked = isBookmarked,
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_vacancy_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClickVacancy(lecture, isVacancyRegistered)
                    },
                ) {
                    RingingAlarmIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        marked = isVacancyRegistered,
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(R.string.search_result_item_add_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onToggleLectureContained(lecture, contained)
                    },
                ) {
                    if (contained) {
                        RemoveCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        )
                    } else {
                        AddCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        )
                    }
                }
            }
        }
        Divider(color = SNUTTColors.White400)
    }
}

@Composable
fun LectureListItemButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clicks {
                onClick()
            },
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
