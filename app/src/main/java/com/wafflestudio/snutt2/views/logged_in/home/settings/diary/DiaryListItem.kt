package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.RoundBorderButton
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlin.math.round

@Composable
fun DiaryListItem () {
    Box(modifier = Modifier.padding(horizontal = 20.dp)){
        Box(modifier = Modifier.padding(vertical = 20.dp)){
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)){
                        Text("2024.3.20", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                        Text("금", style = SNUTTTypography.h3.copy(fontSize = 15.sp))
                    }
                    Box(modifier = Modifier.background(color = SNUTTColors.LectureDiaryRedBg, shape = RoundedCornerShape(4.dp)).padding(vertical = 4.dp, horizontal = 8.dp)){
                        Text("별로에요", style = SNUTTTypography.body1.copy(fontSize = 11.sp, color = SNUTTColors.LectureDiaryRed))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                            append("시각디자인기초, 배구")
                        }
                        append("에 대한 강의일기를 남겼어요.")
                    }, style = SNUTTTypography.body1, color = SNUTTColors.EditTextLabel)
                    ArrowDownIcon(modifier = Modifier.height(20.dp))
                }
            }
        }
        Divider(
            modifier = Modifier.height(0.5f.dp),
            color = SNUTTColors.Black250,
        )
    }
}


@Composable
@Preview
fun DiaryListItemPreview(){
    DiaryListItem()
}
