package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType

@Composable
fun LecturesOfTablePage() {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<TimetableViewModel>()

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>(backStackEntry)

    val currentTable: TableDto? by viewModel.currentTable.collectAsState()
    val lectureList = currentTable?.lectureList ?: emptyList()

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.timetable_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() },
        )
        LecturesOfTable(
            lectures = lectureList,
            onClickAdd = {
                lectureDetailViewModel.initializeEditingLectureDetail(LectureDto.Default, ModeType.Editing(true))
                navController.navigate(NavigationDestination.LectureDetail)
            },
            onClickLecture = { lecture ->
                lectureDetailViewModel.initializeEditingLectureDetail(lecture, ModeType.Normal)
                navController.navigate(NavigationDestination.LectureDetail) {
                    launchSingleTop = true
                }
            },
        )
    }
}

@Composable
fun LecturesOfTable(
    lectures: List<LectureDto>,
    onClickAdd: () -> Unit,
    onClickLecture: (lecture: LectureDto) -> Unit,
) {
    LazyColumn {
        items(lectures) { lectureDto ->
            TableLectureItem(
                lecture = lectureDto,
                onClickLecture = onClickLecture,
            )
            Row(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                Divider(thickness = 1.dp, color = SNUTTColors.Black050)
            }
        }
        item {
            TableLectureAdd(onClickAdd = onClickAdd)
        }
    }
}

@Composable
private fun TableLectureItem(
    lecture: LectureDto,
    onClickLecture: (lecture: LectureDto) -> Unit,
) {
    val tagText = SNUTTStringUtils.getLectureTagText(lecture)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTimeForLecture(lecture)
    val locationText = SNUTTStringUtils.getSimplifiedLocation(lecture)

    Column(
        modifier = Modifier
            .clicks { onClickLecture(lecture) }
            .padding(horizontal = 20.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = lecture.course_title,
                style = SNUTTTypography.h4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = SNUTTStringUtils.getInstructorAndCreditText(lecture),
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

@Composable
private fun TableLectureAdd(onClickAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .clicks { onClickAdd.invoke() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Row {
            Text(
                text = stringResource(R.string.lecture_list_add_button),
                style = SNUTTTypography.body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            RightArrowIcon(modifier = Modifier.size(22.dp, 22.dp))
        }
    }
    Spacer(Modifier.height(20.dp))
}

@Preview(showBackground = true)
@Composable
fun LecturesOfTablePagePreview() {
    LecturesOfTablePage()
}
