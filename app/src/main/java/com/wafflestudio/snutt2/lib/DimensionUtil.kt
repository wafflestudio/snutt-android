package com.wafflestudio.snutt2.lib

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit

@Composable
fun TextUnit.toDp() = with(LocalDensity.current) {
    this@toDp.toDp()
}
