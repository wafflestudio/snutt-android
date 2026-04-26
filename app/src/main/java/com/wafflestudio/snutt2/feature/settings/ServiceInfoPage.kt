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
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.theme.ThemeMode

@Composable
fun ServiceInfoPage(
    viewModel: SettingsWebPageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServiceInfoScreen(
        themeMode = uiState.themeMode,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun ServiceInfoScreen(
    themeMode: ThemeMode,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val url = stringResource(R.string.api_server) + stringResource(R.string.terms)
    val apiKey = stringResource(R.string.api_key)
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.settings_service_info),
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
