package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridItemScopeImpl.animateItemPlacement
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun VacancyPage() {
    val navController = LocalNavController.current

    Column(modifier = Modifier.background(SNUTTColors.White900)) {
        SimpleTopBar(
            title = stringResource(R.string.vacancy_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VacancyListItem(
    lectureDataWithSelected: Selectable<LectureDto>
) {
    val scope = rememberCoroutineScope()
    val selected = lectureDataWithSelected.state
    val lectureTitle = lectureDataWithSelected.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithSelected.item.instructor,
        lectureDataWithSelected.item.credit
    )
    val remarkText = lectureDataWithSelected.item.remark
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDataWithSelected.item)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lectureDataWithSelected.item)
    val backgroundColor = if (selected) SNUTTColors.Dim2 else SNUTTColors.Transparent

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
                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                    }
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
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
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
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
                        fontWeight = FontWeight.Light
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
                    text = SNUTTStringUtils.getSimplifiedLocation(lectureDataWithSelected.item),
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
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
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    Divider(color = SNUTTColors.White400)
}
