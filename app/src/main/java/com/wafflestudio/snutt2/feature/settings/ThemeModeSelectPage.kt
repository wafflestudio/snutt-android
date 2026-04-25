package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.CheckedIcon
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.ThemeMode

@Composable
fun ThemeModeSelectPage(
    viewModel: ColorModeSelectViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ColorModeSelectScreen(
        themeMode = uiState.themeMode,
        onSelectMode = { viewModel.setThemeMode(it) },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun ColorModeSelectScreen(
    themeMode: ThemeMode,
    onSelectMode: (ThemeMode) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground)
            .logImpression(AnalyticsScreen.SettingsColorScheme),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_select_color_mode_title),
        ) {
            onNavigateBack()
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(10.dp))
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_auto),
                    hasNextPage = false,
                    onClick = { onSelectMode(ThemeMode.AUTO) },
                ) {
                    if (themeMode == ThemeMode.AUTO) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
                        )
                    }
                }
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_dark),
                    hasNextPage = false,
                    onClick = { onSelectMode(ThemeMode.DARK) },
                ) {
                    if (themeMode == ThemeMode.DARK) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
                        )
                    }
                }
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_light),
                    hasNextPage = false,
                    onClick = { onSelectMode(ThemeMode.LIGHT) },
                ) {
                    if (themeMode == ThemeMode.LIGHT) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
                        )
                    }
                }
            }
        }
    }
}

@SnuttPreview
@Composable
private fun ColorModeSelectScreen_Default() {
    SnuttPreviewSurface {
        ColorModeSelectScreen(
            themeMode = ThemeMode.AUTO,
            onSelectMode = {},
            onNavigateBack = {},
        )
    }
}
