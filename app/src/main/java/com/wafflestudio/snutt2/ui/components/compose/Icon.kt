package com.wafflestudio.snutt2.ui.components.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

@Composable
fun SnuttIcon(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = id),
        contentDescription = contentDescription,
        colorFilter = colorFilter,
    )
}
