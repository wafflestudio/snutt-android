package com.wafflestudio.snutt2.views.logged_in.home.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.components.compose.PeopleIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun FriendsPage() {
    Column(Modifier.background(SNUTTColors.White900)) {
        TopBar(
            title = {
                Text(
                    text = "친구 시간표",
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                PeopleIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "COMMING SOON",
                letterSpacing = 4.sp,
                style = SNUTTTypography.body1.copy(
                    fontSize = 20.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
