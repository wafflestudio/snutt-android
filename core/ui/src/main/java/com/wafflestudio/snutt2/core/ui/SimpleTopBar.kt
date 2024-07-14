package com.wafflestudio.snutt2.core.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.core.designsystem.component.ArrowBackIcon
import com.wafflestudio.snutt2.core.designsystem.theme.Black900
import com.wafflestudio.snutt2.core.designsystem.theme.SNUTTTypography
import com.wafflestudio.snutt2.core.designsystem.util.clicks

@Composable
fun SimpleTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onClickNavigateBack: () -> Unit,
) {
    TopBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = SNUTTTypography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            ArrowBackIcon(
                modifier = Modifier.clicks(1000L) { onClickNavigateBack() },
                colorFilter = ColorFilter.tint(Black900),
            )
        },
    )
}
