package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LectureDetailPage(id: String?) {
    Text(text = "LectureDetailPage $id")
}

@Preview
@Composable
fun LectureDetailPagePreview() {
    LectureDetailPage("temp")
}
