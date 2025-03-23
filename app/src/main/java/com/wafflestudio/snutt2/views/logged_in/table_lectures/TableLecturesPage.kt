package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ClockIcon
import com.wafflestudio.snutt2.components.compose.LocationIcon
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TagIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.PreviewData
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtilsNew
import com.wafflestudio.snutt2.ui.SNUTTTypography


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

@Composable
private fun TableLectureAddNew(modifier: Modifier, onClickAdd: () -> Unit) {
    Column(
        modifier = modifier.clicks { onClickAdd.invoke() },
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
fun TableLectureItemPreviewNew() {
    TableLectureItemNew(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
        lecture = PreviewData.syllabusLecture,
    ) {}
}

@Preview(showBackground = true)
@Composable
fun TableLectureAddPreviewNew() {
    TableLectureAddNew(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
    ) {}
}
