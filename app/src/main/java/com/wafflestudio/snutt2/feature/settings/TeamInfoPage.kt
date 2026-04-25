package com.wafflestudio.snutt2.feature.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.ThemeMode

@Composable
fun TeamInfoPage(
    viewModel: SettingsWebPageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TeamInfoScreen(
        themeMode = uiState.themeMode,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun TeamInfoScreen(
    themeMode: ThemeMode,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val url = stringResource(R.string.api_server) + stringResource(R.string.member)
    val apiKey = stringResource(R.string.api_key)
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .logImpression(AnalyticsScreen.SettingsDevelopers),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_team_info),
            onClickNavigateBack = onNavigateBack,
        )
        AndroidView(
            factory = {
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    loadUrl(
                        url,
                        hashMapOf(
                            "x-access-apikey" to apiKey,
                            "dark" to if (isDark) "dark" else "light",
                        ),
                    )
                }
            },
        )
    }
}

@SnuttPreview
@Composable
private fun TeamInfoScreen_Default() {
    SnuttPreviewSurface {
        TeamInfoScreen(
            themeMode = ThemeMode.AUTO,
            onNavigateBack = {},
        )
    }
}
