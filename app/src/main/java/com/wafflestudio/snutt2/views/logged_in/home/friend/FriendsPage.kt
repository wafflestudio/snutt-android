package com.wafflestudio.snutt2.views.logged_in.home.friend

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.PeopleIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.RootActivity

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FriendsPage() {
    val reactRootView = (LocalContext.current as RootActivity).friendBundleManager.reactRootView

    reactRootView.value?.let { view ->
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { view },
        )
    } ?: FriendsPagePlaceholder()
}

@Composable
fun FriendsPagePlaceholder() {
    Column(Modifier.background(SNUTTColors.White900)) {
        TopBar(
            title = {
                Text(
                    text = "친구 시간표",
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_reviews_coming_soon),
                    contentDescription = "",
                    modifier = Modifier.size(75.dp),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "친구기능 개발 중...",
                        color = SNUTTColors.Black500,
                        style = SNUTTTypography.h3,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Coming Soon!",
                        color = SNUTTColors.Black500,
                        style = SNUTTTypography.body1,
                    )
                }
            }
        }
    }
}
