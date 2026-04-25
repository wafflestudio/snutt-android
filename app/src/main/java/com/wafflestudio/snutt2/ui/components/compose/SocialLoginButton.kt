package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
fun SocialLoginButton(
    painter: Painter,
    size: Dp = 44.dp,
    onClick: () -> Unit,
) {
    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                onClick()
            },
    )
}

@SnuttPreview
@Composable
private fun SocialLoginButton_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            SocialLoginButton(
                painter = painterResource(id = R.drawable.kakao_login),
                onClick = {},
            )
        }
    }
}
