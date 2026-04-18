package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.domain.model.LectureSession

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun DayTimePickerSheetContent_Default() {
    DayTimePickerSheetContent(
        session = LectureSession.Default,
        onDismiss = {},
        onConfirm = {},
    )
}
