package com.wafflestudio.snutt2.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.core.designsystem.theme.White900

@Composable
fun BackgroundColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .background(White900)
            .fillMaxSize(),
    ) {
        content()
    }
}