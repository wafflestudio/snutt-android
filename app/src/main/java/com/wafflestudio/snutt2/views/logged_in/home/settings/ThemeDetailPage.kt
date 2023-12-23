package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable

@Composable
fun ThemeDetailPage() {
    var themeName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            title = "테마명",
        ) {
            EditText(
                value = themeName,
                onValueChange = { themeName = it },
            )
        }
        SettingColumn(
            title = "색 조합",
        ) {
            SettingItem(
                title = "색상1",
            ) {
            }
        }
        SettingItem(
            title = "기본 테마로 지정",
        ) {
            PoorSwitch(false)
        }
        SettingColumn(
            title = "미리보기",
        ) {
            TimeTable(selectedLecture = null, touchEnabled = false)
        }
    }
}
