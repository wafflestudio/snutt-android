package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryListPage() {
    val semesters = listOf("24-1", "23-겨울", "23-2", "23-1", "23-겨울")
    val diaries = listOf("1", "2", "3")
    Box {
        Column(
            modifier = Modifier.background(SNUTTColors.White900),
        ) {
            TopBar(
                title = { Text("") },
                navigationIcon = {
                    ArrowBackIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                },
            )
            LazyRow(
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(semesters) { semester ->
                    Box(
                        modifier = Modifier
                            .background(SNUTTColors.SNUTTTheme, RoundedCornerShape(50))
                            .padding(horizontal = 24.dp)
                            .height(34.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(semester, color = SNUTTColors.White900, style = SNUTTTypography.subtitle1.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
            LazyColumn() {
                items(diaries) {
                    DiaryListDateItem()
                }
            }
        }
    }
}

@Composable
@Preview()
fun DiaryListPagePreview() {
    DiaryListPage()
}
