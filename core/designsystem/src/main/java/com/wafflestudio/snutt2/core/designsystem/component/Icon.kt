package com.wafflestudio.snutt2.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.core.designsystem.icon.SNUTTIcons.ArrowBack

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = ArrowBack),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}