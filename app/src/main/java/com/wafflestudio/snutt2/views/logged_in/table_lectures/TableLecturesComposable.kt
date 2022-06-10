package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getInstructorAndCredit
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.ui.common.clickableWithoutRippleEffect
import com.wafflestudio.snutt2.ui.SnuttTypography

sealed class Data {
    class Lecture(val lecture: LectureDto) : Data()

    object Add : Data()
}

@Composable
fun TableLecturesList(
    lectures: List<LectureDto>,
    onClickAdd: () -> Unit,
    onClickLecture: (lecture: LectureDto) -> Unit
) {
    LazyColumn {
        items(lectures) { lectureDto ->
            TableLectureItem(data = Data.Lecture(lectureDto), onClickAdd = onClickAdd, onClickLecture = onClickLecture)
            Row(Modifier.padding(horizontal = 20.dp)) {
                Divider(color = colorResource(R.color.table_lecture_divider), thickness = 1.dp)
            }
        }
        item {
            TableLectureItem(data = Data.Add, onClickAdd = onClickAdd, onClickLecture = onClickLecture)
        }
    }
}

@Composable
fun TableLectureItem(data: Data?, onClickAdd: () -> Unit, onClickLecture: (lecture: LectureDto) -> Unit) {

    when (data) {
        is Data.Lecture -> {
            val tagText: String = listOf(
                data.lecture.category,
                data.lecture.department,
                data.lecture.academic_year
            )
                .filter { it.isNullOrBlank().not() }
                .let {
                    if (it.isEmpty()) stringResource(id = R.string.table_lectures_empty_string) else it.joinToString(", ")
                }
            val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(data.lecture)
            val locationText = SNUTTStringUtils.getSimplifiedLocation(data.lecture)

            Column(
                modifier = Modifier
                    .clickableWithoutRippleEffect {
                        onClickLecture(data.lecture)
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(text = data.lecture.course_title, style = SnuttTypography.subtitle1, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = getInstructorAndCredit(data.lecture.instructor, data.lecture.credit), style = SnuttTypography.body1)
                    }
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_tag), contentDescription = "tag icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = tagText, style = SnuttTypography.body1)
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_clock), contentDescription = "time icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = classTimeText, style = SnuttTypography.body1)
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_location), contentDescription = "location icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = locationText, style = SnuttTypography.body1)
                }
            }
        }
        is Data.Add -> {
            Column(
                modifier = Modifier
                    .clickableWithoutRippleEffect {
                        onClickAdd.invoke()
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.lecture_list_add_button), style = SnuttTypography.subtitle2)
                    }
                    Image(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "add arrow", modifier = Modifier.size(22.dp, 22.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        else -> {}
    }
}
