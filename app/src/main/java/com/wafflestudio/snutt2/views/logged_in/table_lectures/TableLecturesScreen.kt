package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ClockIcon
import com.wafflestudio.snutt2.components.compose.LocationIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.TagIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtilsNew
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import kotlinx.coroutines.flow.map

@Composable
fun TableLecturesRoute(
    viewModel: TimetableViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val lectures by viewModel.currentTable.map { table ->
        // FIXME: Table 도메인 모델 생성 후 수정
        table?.lectureList?.map {
            // FIXME: ViewModel 단에서 LectureDTO 걷어 내고 제거
            it.toLocalLecture()
        } ?: emptyList()
    }.collectAsStateWithLifecycle(emptyList())

    // FIXME: 임시 코드
    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>(backStackEntry)

    TableLecturesScreen(
        uiState = TableLecturesUIState(lectures),
        onClickLecture = { lecture ->
            // FIXME: 임시 코드
            lectureDetailViewModel.initializeEditingLectureDetail(
                LectureDto.fromLocalLecture(
                    lecture,
                ),
                ModeType.Normal, viewModel.currentTable.value,
            )
            navController.navigate(NavigationDestination.LectureDetail) {
                launchSingleTop = true
            }
        },
        onBack = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun TableLecturesScreen(
    uiState: TableLecturesUIState,
    onClickLecture: (LocalLecture) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxSize()
            .logImpression(AnalyticsScreen.LectureList),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.timetable_app_bar_title),
            onClickNavigateBack = onBack,
        )
        LazyColumn {
            items(uiState.lectures) { lecture ->
                TableLectureItemNew(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
                    lecture,
                    onClickLecture,
                )
                Row(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                    Divider(thickness = 1.dp, color = SNUTTColors.Black050)
                }
            }
        }
    }
}

@Composable
fun TableLectureItemNew(
    modifier: Modifier,
    lecture: LocalLecture,
    onClickLecture: (lecture: LocalLecture) -> Unit,
) {
    val tagText = SNUTTStringUtilsNew.getLectureTagText(lecture)
    val classTimeText = SNUTTStringUtilsNew.getSimplifiedClassTimeForLecture(lecture)
    val locationText = SNUTTStringUtilsNew.getSimplifiedLocation(lecture)

    Column(
        modifier = modifier.clicks { onClickLecture(lecture) },
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = lecture.courseTitle,
                style = SNUTTTypography.h4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = SNUTTStringUtilsNew.getInstructorAndCreditText(lecture),
                style = SNUTTTypography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TagIcon(modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = tagText,
                style = SNUTTTypography.body2,
                modifier = Modifier.alpha(0.8f),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            ClockIcon(modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = classTimeText,
                style = SNUTTTypography.body2,
                modifier = Modifier.alpha(0.8f),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            LocationIcon(modifier = Modifier.size(15.dp, 15.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = locationText,
                style = SNUTTTypography.body2,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TableLectureItemPreviewNew() {
    TableLectureItemNew(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
        lecture = PreviewData.syllabusLecture,
    ) {}
}
