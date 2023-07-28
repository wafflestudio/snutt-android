package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CheckedIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun ColorModeSelectPage() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()
    val themeMode by userViewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.MoreTabBackground)
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_select_color_mode_title),
        ) {
            navController.popBackStack()
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_auto),
                    hasNextPage = false,
                    onClick = {
                        scope.launch {
                            userViewModel.setThemeMode(ThemeMode.AUTO)
                        }
                    }
                ) {
                    if (themeMode == ThemeMode.AUTO) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500)
                        )
                    }
                }
                SettingDivider()
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_dark),
                    hasNextPage = false,
                    onClick = {
                        scope.launch {
                            userViewModel.setThemeMode(ThemeMode.DARK)
                        }
                    }
                ) {
                    if (themeMode == ThemeMode.DARK) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500)
                        )
                    }
                }
                SettingDivider()
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_light),
                    hasNextPage = false,
                    onClick = {
                        scope.launch {
                            userViewModel.setThemeMode(ThemeMode.LIGHT)
                        }
                    }
                ) {
                    if (themeMode == ThemeMode.LIGHT) {
                        CheckedIcon(
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black500)
                        )
                    }
                }
            }
        }
    }
}
