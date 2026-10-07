package com.wafflestudio.snutt2.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", showSystemUi = true)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", showSystemUi = true)
annotation class SnuttPreview

@Composable
fun SnuttPreviewSurface(content: @Composable () -> Unit) {
    SNUTTTheme {
        Surface(content = content)
    }
}

@Composable
fun SnuttPreviewCenteredSurface(content: @Composable () -> Unit) {
    SnuttPreviewSurface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}
