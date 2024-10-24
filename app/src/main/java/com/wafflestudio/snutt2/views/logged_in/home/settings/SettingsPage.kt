package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val viewModel = hiltViewModel<UserViewModel>()
    var logoutDialogState by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsState()
    val user by userViewModel.userInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
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
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.user_settings_app_bar_title),
                modifier = Modifier.height(66.dp),
                leadingIcon = {
                    PersonIcon(
                        modifier = Modifier
                            .size(22.dp)
                            .padding(end = 5.dp),
                    )
                },
                onClick = {
                    navController.navigate(
                        NavigationDestination.UserConfig,
                    )
                },
            ) {
                Text(
                    text = user?.nickname.toString(),
                    style = SNUTTTypography.body1.copy(
                        color = SNUTTColors.Black500,
                    ),
                )
            }
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.ThemeModeSelect,
                        )
                    },
                ) {
                    Text(
                        text = themeMode.toString(),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.timetable_settings_app_bar_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.TimeTableConfig,
                        )
                    },
                )
                SettingItem(
                    title = stringResource(R.string.settings_timetable_theme_config_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.ThemeConfig,
                        )
                    },
                )
            }
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_item_vacancy),
                    hasNextPage = true,
                    onClick = {
                        navController.navigate(
                            NavigationDestination.VacancyNotification,
                        )
                    },
                )
            }
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_version_info),
                    hasNextPage = false,
                ) {
                    Text(
                        text = BuildConfig.VERSION_NAME,
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.settings_team_info),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.TeamInfo,
                        )
                    },
                )
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_app_report_title),
                onClick = {
                    navController.navigate(
                        NavigationDestination.AppReport,
                    )
                },
            )
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_licenses_title),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.OpenLicenses,
                        )
                    },
                )
                SettingItem(
                    title = stringResource(R.string.settings_service_info),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.ServiceInfo,
                        )
                    },
                )
                SettingItem(
                    title = stringResource(R.string.settings_personal_information_policy),
                    onClick = {
                        navController.navigate(
                            NavigationDestination.PersonalInformationPolicy,
                        )
                    },
                )
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_logout_title),
                titleColor = SNUTTColors.Red,
                onClick = {
                    logoutDialogState = true
                },
            )

            if (BuildConfig.DEBUG) {
                Margin(height = 10.dp)
                SettingItem(
                    title = "네트워크 로그",
                    onClick = {
                        navController.navigate(NavigationDestination.NetworkLog)
                    },
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
            positiveButtonText = stringResource(R.string.settings_logout_title),
        ) {
            Text(text = stringResource(R.string.settings_logout_message), style = SNUTTTypography.body2)
        }
    }
}

@Composable
fun SettingColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    titleStyle: TextStyle = SNUTTTypography.body2.copy(
        color = MaterialTheme.colors.onSurfaceVariant,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp, start = 20.dp)
                    .align(Alignment.Start),
                style = titleStyle,
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
        content()
    }
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colors.onSurface,
    leadingIcon: @Composable () -> Unit = {},
    hasNextPage: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    val newSettingItems by LocalRemoteConfig.current.settingPageNewBadgeTitles.collectAsState(emptyList())
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(MaterialTheme.colors.surface)
            .clicks { if (onClick != null) onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(
                color = titleColor,
            ),
        )
        if (newSettingItems.contains(title)) {
            NewBadge(Modifier.padding(start = 5.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        content()
        if (hasNextPage) {
            RightArrowIcon(
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500),
            )
        }
    }
}

@Composable
fun NewBadge(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 26.dp, height = 14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(SNUTTColors.SNUTTTheme),
    ) {
        Text(
            text = "NEW!",
            modifier = Modifier.align(Alignment.Center),
            style = SNUTTTypography.body2
                .copy(
                    color = SNUTTColors.AllWhite,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}
