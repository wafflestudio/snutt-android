package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.VacancyListItem(
    lectureDataWithVacancy: DataWithState<LectureDto, Boolean>,
    editing: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    val hasVacancy = lectureDataWithVacancy.state
    val lectureTitle = lectureDataWithVacancy.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithVacancy.item.instructor,
        lectureDataWithVacancy.item.credit
    )
    val quotaText = stringResource(
        R.string.vacancy_item_quota_text,
        lectureDataWithVacancy.item.registrationCount,
        lectureDataWithVacancy.item.quota,
    )
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDataWithVacancy.item)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lectureDataWithVacancy.item)
    val backgroundColor = if (hasVacancy) SNUTTColors.Red.copy(alpha = 0.1f) else SNUTTColors.Transparent

    Row(
        modifier = Modifier
            .animateItemPlacement(
                animationSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(editing) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 20.dp)
            )
        }
        Column {
            Column(
                modifier = Modifier
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lectureTitle,
                        style = SNUTTTypography.h4,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (hasVacancy) VacancySticker(
                        modifier = Modifier
                            .padding(start = 5.dp)
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = instructorCreditText,
                        style = SNUTTTypography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Right
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TagIcon(
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tagText,
                        style = SNUTTTypography.body2,
                        modifier = Modifier
                            .alpha(0.8f)
                            .weight(1f)
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
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = classTimeText,
                        style = SNUTTTypography.body2,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LocationIcon(
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = SNUTTStringUtils.getSimplifiedLocation(lectureDataWithVacancy.item),
                        style = SNUTTTypography.body2,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
            }
            Divider(color = SNUTTColors.Black250)
        }
    }
}

@Composable
fun VacancySticker(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .border(
                width = 1.dp,
                color = SNUTTColors.Red,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(horizontal = 3.dp),
        text = stringResource(R.string.vacancy_item_vacancy_sticker),
        style = SNUTTTypography.body2
            .copy(
                color = SNUTTColors.Red,
                fontSize = 10.sp
            )
    )
}

@Preview
@Composable
fun VacancyStickerPreview() {
    VacancySticker()
}
