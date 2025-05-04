package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.ArrowUpIcon
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryListItem() {
    var isSelected by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("2024.3.20", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                            Text("금", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                        }
                        Box(modifier = Modifier.background(color = SNUTTColors.Red.copy(alpha = 0.06F), shape = RoundedCornerShape(4.dp)).padding(vertical = 4.dp, horizontal = 8.dp)) {
                            Text(stringResource(R.string.diary_day_bad), style = SNUTTTypography.body1.copy(fontSize = 11.sp, color = SNUTTColors.Red))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("시각디자인기초, 배구")
                                }
                                append(stringResource(R.string.diary_wrote_text))
                            },
                            style = SNUTTTypography.body1, color = SNUTTColors.EditTextLabel,
                        )
                        if (isSelected) ArrowUpIcon(modifier = Modifier.height(20.dp).clickable { isSelected = !isSelected }) else ArrowDownIcon(modifier = Modifier.height(20.dp).clickable { isSelected = !isSelected })
                    }
                }
                AnimatedVisibility(visible = isSelected) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DiaryContentListItem()
                        DiaryContentListItem()
                    }
                }
            }
        }

    Divider(
        modifier = Modifier.height(0.5f.dp),
        color = SNUTTColors.Black250,
    )
}

@Composable
fun DiaryContentListItem() {
    Box(modifier = Modifier.background(color = SNUTTColors.LectureDiaryGray, shape = RoundedCornerShape(4.dp))) {
        Column(
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("시각디자인기초", style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel))
                Box(
                    modifier = Modifier.border(0.8.dp, color = SNUTTColors.EditTextUnderline, shape = RoundedCornerShape(17.dp))
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                ) {
                    Text(
                        "수정하기", modifier = Modifier,
                        style = SNUTTTypography.button.copy(fontSize = 13.sp),
                    )
                }
            }
            Row(modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("수강신청", style = SNUTTTypography.subtitle2)
                        Text("널널해요", style = SNUTTTypography.body1)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("드랍여부", style = SNUTTTypography.subtitle2)
                        Text("모르겠어요", style = SNUTTTypography.body1)
                    }
                }
                Divider(modifier = Modifier.height(25.dp).width(1.4.dp).align(Alignment.CenterVertically))
                Column() {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("수업 첫인상", style = SNUTTTypography.subtitle2)
                        Text("두려워요", style = SNUTTTypography.body1)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DiaryListItemPreview() {
    DiaryListItem()
}
