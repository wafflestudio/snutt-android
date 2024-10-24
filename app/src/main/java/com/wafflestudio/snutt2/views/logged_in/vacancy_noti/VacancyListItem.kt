package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.VacancyListItem(
    lectureDto: LectureDto,
    editing: Boolean = false,
    checked: Boolean = false,
    onClick: () -> Unit = {},
) {
    val hasVacancy = lectureDto.wasFull && lectureDto.registrationCount < lectureDto.quota
    val lectureTitle = lectureDto.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDto.instructor,
        lectureDto.credit,
    )
    val quotaText = stringResource(
        R.string.vacancy_item_quota_text,
        lectureDto.registrationCount,
        lectureDto.quota,
    )
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDto)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTimeForLecture(lectureDto)
    val backgroundColor = if (hasVacancy) SNUTTColors.VacancyRedBg else SNUTTColors.White900

    Row(
        modifier = Modifier
            .animateItemPlacement(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(editing) {
            RoundCheckbox(
                checked = checked,
                onCheckedChange = { onClick() },
                modifier = Modifier.padding(end = 20.dp),
            )
        }
        Column {
            Column(
                modifier = Modifier
                    .padding(vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            text = lectureTitle,
                            style = SNUTTTypography.h4,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (hasVacancy) {
                            VacancyBadge(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp),
                            )
                        }
                    }
                    Text(
                        text = instructorCreditText,
                        style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Right,
                    )
                }
                Row( // 태그와 quota의 알 수 없는 수직 위치 때문에 쓴 꼼수
                    modifier = Modifier
                        .padding(bottom = 6.dp, top = 2.dp)
                        .height(20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TagIcon(
                            modifier = Modifier
                                .padding(top = 2.dp) // 태그 텍스트의 알 수 없는 수직 위치 때문에 쓴 꼼수
                                .size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = tagText,
                            style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = quotaText,
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.VacancyBlue, fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ClockIcon(
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = classTimeText,
                        style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LocationIcon(
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = SNUTTStringUtils.getSimplifiedLocation(lectureDto),
                        style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Divider(
                modifier = Modifier.height(0.5f.dp),
                color = SNUTTColors.Black250,
            )
        }
    }
}

@Composable
fun VacancyBadge(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .border(
                width = 1.dp,
                color = SNUTTColors.VacancyRed,
                shape = RoundedCornerShape(2.dp),
            )
            .padding(horizontal = 3.dp, vertical = 1.dp),
        text = stringResource(R.string.vacancy_item_vacancy_sticker),
        style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal)
            .copy(
                color = SNUTTColors.VacancyRed,
                fontSize = 11.sp,
            ),
    )
}

@Preview
@Composable
fun VacancyStickerPreview() {
    VacancyBadge()
}
