package com.wafflestudio.snutt2.views.logged_in.home.friend

import android.app.Activity
import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BigPeopleIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FriendsPage() {
    val context = LocalContext.current
//    val bundleLoaded by reactNativeBundleManager.bundleLoadCompleteSignal.collectAsState(false)

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            val intent = Intent((context as Activity), RNModuleActivity::class.java)
            context.startActivity(intent)
        }
    }

//    if (bundleLoaded || reactNativeBundleManager.reactRootView == null) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = {
//                ComposeView(context).apply {
//                    setContent {
//                        FriendsPagePlaceholder()
//                    }
//                }
//            }
//        )
//    } else {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = {
//                reactNativeBundleManager.reactRootView ?: ComposeView(context).apply {
//                    setContent {
//                        FriendsPagePlaceholder()
//                    }
//                }
//            }
//        )
//    }
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
                BigPeopleIcon(
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
