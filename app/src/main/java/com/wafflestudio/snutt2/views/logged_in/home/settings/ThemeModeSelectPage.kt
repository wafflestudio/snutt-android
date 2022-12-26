package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
            .background(SNUTTColors.Gray100)
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_select_color_mode_title),
        ) {
            navController.popBackStack()
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            SettingItem(
                title = stringResource(R.string.settings_select_color_mode_auto),
                content = {
                    if (themeMode == ThemeMode.AUTO) {
                        CheckedIcon(colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    }
                },
                onClick = {
                    scope.launch {
                        userViewModel.setThemeMode(ThemeMode.AUTO)
                    }
                }
            )
            Spacer(modifier = Modifier.height(3.dp))
            SettingItem(
                title = stringResource(R.string.settings_select_color_mode_dark),
                content = {
                    if (themeMode == ThemeMode.DARK) {
                        CheckedIcon(colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    }
                },
                onClick = {
                    scope.launch {
                        userViewModel.setThemeMode(ThemeMode.DARK)
                    }
                }
            )
            Spacer(modifier = Modifier.height(3.dp))
            SettingItem(
                title = stringResource(R.string.settings_select_color_mode_light),
                content = {
                    if (themeMode == ThemeMode.LIGHT) {
                        CheckedIcon(colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    }
                },
                onClick = {
                    scope.launch {
                        userViewModel.setThemeMode(ThemeMode.LIGHT)
                    }
                }
            )
        }
    }
}
