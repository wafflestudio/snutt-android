package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.SettingsIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import de.psdev.licensesdialog.BuildConfig
import de.psdev.licensesdialog.LicensesDialog

@Composable
fun SettingsPage() {
    val navController = NavControllerContext.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingsViewModel>()
    val tableTrimParam =
        viewModel.trimParam.asObservable().subscribeAsState(initial = TableTrimParam.Default)

    var logoutDialogState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff2f2f2)) // TODO: Color
    ) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.timetable_app_bar_setting)) },
            navigationIcon = { SettingsIcon(modifier = Modifier.size(30.dp)) },
        )
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(Color.White)) {
            // TODO: 여기서도 공용으로 쓰니 compose component 폴더로 보내기
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
        }
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_version_info),
            modifier = Modifier.background(Color.White),
            content = {
                Text(text = BuildConfig.VERSION_NAME)
            }
        )
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(Color.White)) {
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
        Column(modifier = Modifier.background(Color.White)) {
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
            modifier = Modifier.background(Color.White)
        ) {
            logoutDialogState = true
        }
        Margin(height = 10.dp)
    }

    if (logoutDialogState) {
        CustomDialog(
            onDismiss = { logoutDialogState = false },
            onConfirm = {
//                viewModel.performLogout()
            },
            title = stringResource(R.string.settings_logout_title),
            positiveButtonText = stringResource(R.string.settings_logout_title)
        ) {
            Text(text = stringResource(R.string.settings_logout_message))
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
        Text(text = title)
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
    CompositionLocalProvider(
        NavControllerContext provides rememberNavController()
    ) {
        SettingsPage()
    }
}
