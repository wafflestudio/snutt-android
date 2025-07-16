package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryListItem() {
    var isSelected by remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("2024.3.20", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                    Text("금", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "시각디자인기초, 배구",
                    style = SNUTTTypography.body1, color = SNUTTColors.EditTextLabel,
                )
                ArrowDownIcon(modifier = Modifier.height(20.dp).clickable { isSelected = !isSelected }.rotate(if (isSelected) 180f else 0f))
            }
        }
        AnimatedVisibility(visible = isSelected) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DiaryContentListItem()
                DiaryContentListItem()
            }
        }
    }

    Divider(
        modifier = Modifier.height(0.5.dp),
        color = SNUTTColors.Black250,
    )
}

@Composable
fun DiaryContentListItem() {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString("남기고 싶은 말"),
        style = SNUTTTypography.subtitle2,
    )
    val widthInDp = with(LocalDensity.current) { textLayoutResult.size.width.toDp() }
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = SNUTTColors.LectureDiaryGray,
                    shape = RoundedCornerShape(4.dp),
                ).padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("배구", style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel))
                TrashIcon(modifier = Modifier.size(28.dp, 28.dp), colorFilter = ColorFilter.tint(SNUTTColors.EditTextHint))
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row {
                    Text(modifier = Modifier.padding(end = 16.dp).width(widthInDp), text = "수강신청", style = SNUTTTypography.subtitle2)
                    Text("널널해요", style = SNUTTTypography.body1)
                }
                Row {
                    Text(modifier = Modifier.padding(end = 16.dp).width(widthInDp), text = "드랍여부", style = SNUTTTypography.subtitle2)
                    Text("모르겠어요", style = SNUTTTypography.body1)
                }
                Row {
                    Text(modifier = Modifier.padding(end = 16.dp).width(widthInDp), text = "수업 첫인상", style = SNUTTTypography.subtitle2)
                    Text("두려워요", style = SNUTTTypography.body1)
                }
                Row {
                    Text(modifier = Modifier.padding(end = 16.dp).width(widthInDp), text = "남기고 싶은 말", style = SNUTTTypography.subtitle2)
                    Text("오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8", style = SNUTTTypography.body1)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                "수정하기",
                modifier = Modifier.border(0.8.dp, color = SNUTTColors.EditTextUnderline, shape = RoundedCornerShape(17.dp))
                    .padding(vertical = 6.dp, horizontal = 16.dp),
                style = SNUTTTypography.button.copy(fontSize = 13.sp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DiaryListItemPreview() {
    DiaryListItem()
}
