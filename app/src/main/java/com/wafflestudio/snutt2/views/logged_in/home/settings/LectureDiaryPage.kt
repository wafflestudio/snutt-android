package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.*

@Composable
fun LectureDiaryPage() {
    val semesters = listOf("24-1", "23-겨울")
    Box {
        Column(
            modifier = Modifier.background(SNUTTColors.White900),
        ) {
            TopBar()
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            ) {
            }
        }
    }
}

@Composable
@Preview()
fun LectureDiaryPagePreview() {
    LectureDiaryPage()
}
