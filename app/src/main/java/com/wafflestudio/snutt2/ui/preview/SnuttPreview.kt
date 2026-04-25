package com.wafflestudio.snutt2.ui.preview

import android.content.res.Configuration
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko")
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko")
annotation class SnuttPreview

@Composable
fun SnuttPreviewSurface(content: @Composable () -> Unit) {
    SNUTTTheme {
        Surface(content = content)
    }
}
