package com.wafflestudio.snutt2.views.logged_in.thememarket

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.android.webview.ThemeMarketWebViewContainer
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ThemeMarketRoute(
    onBackClick: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    ThemeMarketScreen(
        accessToken = userViewModel.accessToken,
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun ThemeMarketScreen(
    accessToken: StateFlow<String>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isDarkMode = isDarkMode()
    val webViewContainer = remember {
        ThemeMarketWebViewContainer(
            context = context,
            accessToken = accessToken,
            isDarkMode = isDarkMode,
        )
    }

    BackHandler {
        if (webViewContainer.webView.canGoBack()) {
            webViewContainer.webView.goBack()
        } else {
            onBackClick()
        }
    }

    LaunchedEffect(Unit) {
        webViewContainer.openPage()
    }

    Column(
        modifier = modifier,
    ) {
        SimpleTopBar(
            title = stringResource(R.string.theme_market_app_bar_title),
            onClickNavigateBack = onBackClick,
        )
        ThemeMarketWebView(
            themeMarketWebViewContainer = webViewContainer,
        )
    }
}
