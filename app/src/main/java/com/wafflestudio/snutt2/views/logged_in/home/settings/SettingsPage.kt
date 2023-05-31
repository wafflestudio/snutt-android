package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                SettingIcon(
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
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            SettingItem(title = stringResource(R.string.user_settings_app_bar_title)) {
                navController.navigate(
                    NavigationDestination.UserConfig
                )
            }
            SettingItem(title = stringResource(R.string.timetable_settings_app_bar_title)) {
                navController.navigate(
                    NavigationDestination.TimeTableConfig
                )
            }
            SettingItem(title = stringResource(R.string.settings_select_color_mode_title)) {
                navController.navigate(
                    NavigationDestination.ThemeModeSelect
                )
            }
        }
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_version_info),
            modifier = Modifier.background(SNUTTColors.White900),
            content = {
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500)
                )
            }
        )
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            SettingItem(title = stringResource(R.string.settings_team_info)) {
                navController.navigate(
                    NavigationDestination.TeamInfo
                )
            }
            SettingItem(title = stringResource(R.string.settings_app_report_title)) {
                navController.navigate(
                    NavigationDestination.AppReport
                )
            }
        }
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            SettingItem(title = stringResource(R.string.settings_licenses_title)) {
                showLicenseDialog(context)
            }
            SettingItem(title = stringResource(R.string.settings_service_info)) {
                navController.navigate(
                    NavigationDestination.ServiceInfo
                )
            }
            SettingItem(title = stringResource(R.string.settings_personal_information_policy)) {
                navController.navigate(
                    NavigationDestination.PersonalInformationPolicy
                )
            }
        }
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_logout_title),
            modifier = Modifier.background(SNUTTColors.White900)
        ) {
            logoutDialogState = true
        }
        if (BuildConfig.DEBUG) {
            Margin(height = 10.dp)
            SettingItem(
                title = "네트워크 로그",
                modifier = Modifier.background(SNUTTColors.White900)
            ) {
                navController.navigate(NavigationDestination.NetworkLog)
            }
        }
        Margin(height = 10.dp)
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
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = title, style = SNUTTTypography.body1)
        Spacer(modifier = Modifier.weight(1f))
        content()
        Spacer(modifier = Modifier.width(20.dp))
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
