package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.flowOf

private val spoqaHanSans = FontFamily(
    Font(R.font.spoqa_han_sans_regular, FontWeight.Medium),
    Font(R.font.spoqa_han_sans_bold, FontWeight.Bold),
)

private val subHeadingFontStyle = TextStyle(fontSize = 14.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Bold)
private val detailFontStyle = TextStyle(fontSize = 12.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Medium)
private val detail2FontStyle = TextStyle(fontSize = 14.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Medium)

sealed class Data {
    class Lecture(val lecture: LectureDto) : Data()

    object Add : Data()
}

@Composable
fun TableLecturesList(lectures: List<LectureDto>, onClickAdd: () -> Unit, onClickLecture: (lecture: LectureDto) -> Unit) {

    val lectureItems: LazyPagingItems<Data> =
        flowOf(
            PagingData.from(
                lectures.map<LectureDto, Data> { Data.Lecture(it) }
                    .toMutableList()
                    .apply { add(Data.Add) }
                    .toList()
            )
        ).collectAsLazyPagingItems()

    LazyColumn {
        items(lectureItems) { lectureData ->
            TableLectureItem(lectureData, onClickAdd, onClickLecture)
            if (lectureData is Data.Lecture) {
                Row(Modifier.padding(horizontal = 20.dp)) {
                    Divider(color = colorResource(R.color.table_lecture_divider), thickness = 1.dp)
                }
            }
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
                    if (it.isEmpty()) "(없음)" else it.joinToString(", ")
                }
            var classTimeText = SNUTTStringUtils.getSimplifiedClassTime(data.lecture)
            if (classTimeText.isEmpty()) classTimeText = "(없음)"
            var locationText = SNUTTStringUtils.getSimplifiedLocation(data.lecture)
            if (locationText.isEmpty()) locationText = "(없음)"

            Column(
                modifier = Modifier
                    .clickable(
                        onClick = { onClickLecture.invoke(data.lecture) },
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(text = data.lecture.course_title, style = subHeadingFontStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = data.lecture.instructor + " / " + data.lecture.credit + "학점", style = detailFontStyle)
                    }
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_tag), contentDescription = "tag icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = tagText, style = detailFontStyle)
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_clock), contentDescription = "time icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = classTimeText, style = detailFontStyle)
                }
                Spacer(Modifier.height(7.dp))
                Row {
                    Image(painter = painterResource(id = R.drawable.ic_location), contentDescription = "location icon", modifier = Modifier.size(15.dp, 15.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(text = locationText, style = detailFontStyle)
                }
            }
        }
        is Data.Add -> {
            Column(
                modifier = Modifier
                    .clickable(
                        onClick = { onClickAdd.invoke() },
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.lecture_list_add_button), style = detail2FontStyle)
                    }
                    Image(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "add arrow", modifier = Modifier.size(22.dp, 22.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        else -> {}
    }
}
