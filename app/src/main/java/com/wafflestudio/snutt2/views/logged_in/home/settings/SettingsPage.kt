package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(
    uncheckedNotification: Boolean
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val viewModel = hiltViewModel<UserViewModel>()
    var logoutDialogState by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.Gray100)
    ) {
        TopBar(
            // FIXME: 설정 글자가 중간에서 살짝 아래에 위치
            title = {
                Text(
                    text = stringResource(R.string.timetable_app_bar_setting),
                    style = SNUTTTypography.h2,
                )
            },
            navigationIcon = {
                HorizontalMoreIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                IconWithAlertDot(uncheckedNotification) { centerAlignedModifier ->
                    NotificationIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { navController.navigate(NavigationDestination.Notification) },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.user_settings_app_bar_title),
                modifier = Modifier.height(66.dp),
                leadingIcon = {
                    PersonIcon(
                        modifier = Modifier
                            .size(22.dp)
                            .padding(end = 5.dp)
                    )
                },
                onClick = {
                    navController.navigate(
                        NavigationDestination.UserConfig
                    )
                }
            )
            Margin(height = 10.dp)
            SettingColumn(
                title = "디스플레이",
            ) {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.ThemeModeSelect
                        )
                    }
                ) {
                    Text(
                        text = when (themeMode) {
                            ThemeMode.DARK -> stringResource(R.string.settings_select_color_mode_dark)
                            ThemeMode.LIGHT -> stringResource(R.string.settings_select_color_mode_light)
                            ThemeMode.AUTO -> stringResource(R.string.settings_select_color_mode_auto)
                        },
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500)
                    )
                }
                Divider(modifier = Modifier.padding(horizontal = 20.dp))
                SettingItem(
                    title = stringResource(R.string.timetable_settings_app_bar_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.TimeTableConfig
                        )
                    }
                )
            }
            Margin(height = 10.dp)
            SettingColumn(
                title = "정보 및 제안"
            ) {
                SettingItem(
                    title = stringResource(R.string.settings_version_info),
                ) {
                    Text(
                        text = BuildConfig.VERSION_NAME,
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500)
                    )
                }
                Divider(modifier = Modifier.padding(horizontal = 20.dp))
                SettingItem(
                    title = stringResource(R.string.settings_team_info),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.TeamInfo
                        )
                    }
                )
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_app_report_title),
                onClick = {
                    navController.navigate(
                        NavigationDestination.AppReport
                    )
                }
            )

            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_licenses_title),
                    onClick = {
                        showLicenseDialog(context)
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 20.dp))
                SettingItem(
                    title = stringResource(R.string.settings_service_info),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.ServiceInfo
                        )
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 20.dp))
                SettingItem(
                    title = stringResource(R.string.settings_personal_information_policy),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.PersonalInformationPolicy
                        )
                    }
                )
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_logout_title),
                isCritical = true,
                onClick = {
                    logoutDialogState = true
                }
            )

            if (BuildConfig.DEBUG) {
                Margin(height = 10.dp)
                SettingItem(
                    title = "네트워크 로그",
                    onClick = {
                        navController.navigate(NavigationDestination.NetworkLog)
                    }
                )
            }
            Margin(height = 10.dp)
        }
    }

    if (logoutDialogState) {
        CustomDialog(
            onDismiss = { logoutDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        viewModel.performLogout()
                        logoutDialogState = false
                        navController.navigateAsOrigin(NavigationDestination.Onboard)
                    }
                }
            },
            title = stringResource(R.string.settings_logout_title),
            positiveButtonText = stringResource(R.string.settings_logout_title)
        ) {
            Text(text = stringResource(R.string.settings_logout_message), style = SNUTTTypography.body2)
        }
    }
}

@Composable
fun SettingColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 10.dp),
            style = SNUTTTypography.body2.copy(
                color = SNUTTColors.Gray200
            )
        )
        Spacer(modifier = Modifier.size(5.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SNUTTColors.White900)
        ) {
            content()
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = {},
    isCritical: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SNUTTColors.White900)
            .padding(horizontal = 20.dp)
            .clicks { if (onClick != null) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(
                color = if (isCritical) SNUTTColors.Red else SNUTTColors.Black900 // TODO: Red 색 조정
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        content()
        if (onClick != null) { // TODO: onClick != null이지만 화살표가 없는 item도 있다...수정필요
            RightArrowIcon(
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500)
            )
        }
    }
}

private fun showLicenseDialog(context: Context) {
    LicensesDialog.Builder(context).setNotices(R.raw.notices).setIncludeOwnLicense(true).build()
        .show()
}

@Preview
@Composable
fun SettingsPagePreview() {
    SettingsPage(false)
}
