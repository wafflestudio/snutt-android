package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.VacancyListItem(
    lectureDataWithSelected: Selectable<LectureDto>,
    vacancyViewModel: VacancyViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val selected = lectureDataWithSelected.state
    val lectureTitle = lectureDataWithSelected.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithSelected.item.instructor,
        lectureDataWithSelected.item.credit
    )
    val quotaText = stringResource(
        R.string.vacancy_item_quota_text,
        lectureDataWithSelected.item.registrationCount,
        lectureDataWithSelected.item.quota,
    )
    val remarkText = lectureDataWithSelected.item.remark
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDataWithSelected.item)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lectureDataWithSelected.item)
    val backgroundColor = if (selected) SNUTTColors.Dim2 else SNUTTColors.Transparent
    val fontColor = if (selected) SNUTTColors.AllWhite else SNUTTColors.Black900

    Column(
        modifier = Modifier
            .animateItemPlacement(
                animationSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .clicks {
                    scope.launch {
                        vacancyViewModel.toggleLectureSelection(lectureDataWithSelected.item)
                    }
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = fontColor),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = fontColor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(fontColor),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = tagText,
                    style = SNUTTTypography.body2.copy(
                        fontColor,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Text(
                    text = quotaText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.Blue),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(fontColor),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = classTimeText,
                    style = SNUTTTypography.body2.copy(
                        color = fontColor,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LocationIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(fontColor),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = SNUTTStringUtils.getSimplifiedLocation(lectureDataWithSelected.item),
                    style = SNUTTTypography.body2.copy(
                        color = fontColor,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemarkIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(fontColor),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = remarkText.ifEmpty { "없음" },
                    style = SNUTTTypography.body2.copy(
                        color = fontColor,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        AnimatedVisibility(visible = lectureDataWithSelected.state) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.vacancy_item_detail_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { }
                )
                Spacer(modifier = Modifier.weight(0.2f))
                Text(
                    text = stringResource(R.string.vacancy_item_delete_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { }
                )
                Spacer(modifier = Modifier.weight(0.2f))
                Text(
                    text = stringResource(R.string.vacancy_item_goto_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { }
                )
            }
        }
    }
    Divider(color = SNUTTColors.Black250)
}
