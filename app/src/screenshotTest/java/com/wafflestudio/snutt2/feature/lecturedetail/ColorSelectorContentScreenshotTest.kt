package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun ColorItem_Selected() {
    ColorItem(
        foreground = Color.White,
        background = Color(0xFFE54459),
        title = "SNUTT 1",
        isSelected = true,
        onClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun ColorItem_Unselected() {
    ColorItem(
        foreground = Color.White,
        background = Color(0xFF1BD0C8),
        title = "SNUTT 6",
        isSelected = false,
        onClick = {},
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun PickerColorSection_Default() {
    PickerColorSection(
        fgColor = Color.White,
        bgColor = Color(0xFF5965B2),
        onFgPickerClick = {},
        onBgPickerClick = {},
    )
}
