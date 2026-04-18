package com.wafflestudio.snutt2.feature.table_lectures

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.ClockIcon
import com.wafflestudio.snutt2.ui.components.compose.LocationIcon
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.TagIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.getInstructorAndCreditText
import com.wafflestudio.snutt2.ui.util.formatter.getLectureTagText
import com.wafflestudio.snutt2.ui.util.formatter.getSimplifiedClassTimeForLecture
import com.wafflestudio.snutt2.ui.util.formatter.getSimplifiedLocation

@Composable
fun TableLecturesRoute(
    viewModel: TableLecturesViewModel = hiltViewModel(),
    onNavigateLectureDetail: (lectureId: String, tableId: String?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TableLecturesUiEvent.NavigateToLectureDetail -> {
                    onNavigateLectureDetail(event.lectureId, event.tableId)
                }
            }
        }
    }

    TableLecturesScreen(
        uiState = uiState,
        onClickLecture = viewModel::onNavigateLectureDetail,
        onBack = onNavigateBack,
    )
}

@Composable
private fun TableLecturesScreen(
    uiState: TableLecturesUiState,
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
                TableLectureItem(
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
fun TableLectureItem(
    modifier: Modifier,
    lecture: LocalLecture,
    onClickLecture: (lecture: LocalLecture) -> Unit,
) {
    val context = LocalContext.current
    val tagText = getLectureTagText(context, lecture)
    val classTimeText = getSimplifiedClassTimeForLecture(context, lecture)
    val locationText = getSimplifiedLocation(context, lecture)

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
                text = getInstructorAndCreditText(context, lecture),
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
fun TableLectureItemPreview() {
    TableLectureItem(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
        lecture = PreviewData.syllabusLecture,
    ) {}
}
