package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.ui.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.ui.components.compose.RingingAlarmIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
internal fun LectureDetailTopBar(
    lecture: Lecture,
    editMode: Boolean,
    hideEditButton: Boolean = false,
    isBookmarked: Boolean,
    isVacancyRegistered: Boolean,
    onBackPressed: () -> Unit,
    onEditModeToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onVacancyToggle: () -> Unit,
) {
    TopBar(
        title = {
            Text(
                text = stringResource(R.string.lecture_detail_app_bar_title),
                style = SNUTTTypography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            ArrowBackIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks { onBackPressed() },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        },
        actions = {
            if (lecture is LectureSyllabusInfo && !editMode) {
                RingingAlarmIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onVacancyToggle() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    marked = isVacancyRegistered,
                )
                BookmarkIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onBookmarkToggle() },
                    marked = isBookmarked,
                )
            }

            if (hideEditButton.not() && lecture is LocalLecture) {
                Text(
                    text = if (editMode) {
                        stringResource(R.string.lecture_detail_top_bar_complete)
                    } else {
                        stringResource(R.string.lecture_detail_top_bar_edit)
                    },
                    style = SNUTTTypography.body1,
                    modifier = Modifier.clicks { onEditModeToggle() },
                )
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture (북마크/알림 켜짐)")
@Composable
private fun SyllabusLectureViewMarkedPreview() {
    LectureDetailTopBar(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        isBookmarked = true,
        isVacancyRegistered = true,
        onBackPressed = {},
        onEditModeToggle = {},
        onBookmarkToggle = {},
        onVacancyToggle = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture (북마크/알림 꺼짐)")
@Composable
private fun SyllabusLectureViewUnmarkedPreview() {
    LectureDetailTopBar(
        lecture = PreviewData.syllabusLecture,
        editMode = false,
        isBookmarked = false,
        isVacancyRegistered = false,
        onBackPressed = {},
        onEditModeToggle = {},
        onBookmarkToggle = {},
        onVacancyToggle = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SyllabusLecture - 편집 모드")
@Composable
private fun SyllabusLectureEditModePreview() {
    LectureDetailTopBar(
        lecture = PreviewData.syllabusLecture,
        editMode = true,
        isBookmarked = false,
        isVacancyRegistered = false,
        onBackPressed = {},
        onEditModeToggle = {},
        onBookmarkToggle = {},
        onVacancyToggle = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "CustomLecture")
@Composable
private fun CustomLectureViewPreview() {
    LectureDetailTopBar(
        lecture = PreviewData.customLecture,
        editMode = false,
        isBookmarked = false,
        isVacancyRegistered = false,
        onBackPressed = {},
        onEditModeToggle = {},
        onBookmarkToggle = {},
        onVacancyToggle = {},
    )
}

@Preview(showBackground = true, widthDp = 360, name = "SearchedLecture")
@Composable
private fun SearchedLectureViewPreview() {
    LectureDetailTopBar(
        lecture = PreviewData.searchedLecture,
        editMode = false,
        isBookmarked = true,
        isVacancyRegistered = true,
        onBackPressed = {},
        onEditModeToggle = {},
        onBookmarkToggle = {},
        onVacancyToggle = {},
    )
}
