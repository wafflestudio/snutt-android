package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.HorizontalMoreIcon
import com.wafflestudio.snutt2.components.compose.PersonIcon
import com.wafflestudio.snutt2.components.compose.RedDotWithNumber
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.featureflag.FeatureFlag
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
    uncheckedNotifications: Long,
    onNavigateUserConfig: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateThemeMarket: () -> Unit,
    onNavigatePushPreference: () -> Unit,
    onNavigateLectureReminder: () -> Unit,
    onNavigateDiaryWrite: () -> Unit,
    onNavigateDiaryHistory: () -> Unit,
    onNavigateTeamInfo: () -> Unit,
    onNavigateAppReport: () -> Unit,
    onNavigateOpenLicenses: () -> Unit,
    onNavigateServiceInfo: () -> Unit,
    onNavigatePersonalInformationPolicy: () -> Unit,
    onNavigateNetworkLog: () -> Unit,
    onNavigateTest: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
) {
    val uiState by viewModel.settingsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logoutFinishedUiEvent.collect {
            onNavigateOnboardAsOrigin()
        }
    }

    SettingsScreen(
        uiState = uiState,
        bottomBar = bottomBar,
        uncheckedNotifications = uncheckedNotifications,
        onClickUserConfig = onNavigateUserConfig,
        onClickNotification = onNavigateNotification,
        onClickThemeModeSelect = onNavigateThemeModeSelect,
        onClickTimeTableConfig = onNavigateTimeTableConfig,
        onClickThemeConfig = onNavigateThemeConfig,
        onClickVacancyNotification = onNavigateVacancyNotification,
        onClickThemeMarket = onNavigateThemeMarket,
        onClickPushPreference = onNavigatePushPreference,
        onClickLectureReminder = onNavigateLectureReminder,
        onClickDiaryWrite = onNavigateDiaryWrite,
        onClickDiaryHistory = onNavigateDiaryHistory,
        onClickTeamInfo = onNavigateTeamInfo,
        onClickAppReport = onNavigateAppReport,
        onClickOpenLicenses = onNavigateOpenLicenses,
        onClickServiceInfo = onNavigateServiceInfo,
        onClickPersonalInformationPolicy = onNavigatePersonalInformationPolicy,
        onClickNetworkLog = onNavigateNetworkLog,
        onClickTest = onNavigateTest,
        onClickLogout = viewModel::showLogoutDialog,
        onConfirmLogout = viewModel::performLogout,
        onDismissLogout = viewModel::hideLogoutDialog,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    bottomBar: @Composable () -> Unit = {},
    uncheckedNotifications: Long,
    onClickUserConfig: () -> Unit,
    onClickNotification: () -> Unit,
    onClickThemeModeSelect: () -> Unit,
    onClickTimeTableConfig: () -> Unit,
    onClickThemeConfig: () -> Unit,
    onClickVacancyNotification: () -> Unit,
    onClickThemeMarket: () -> Unit,
    onClickPushPreference: () -> Unit,
    onClickLectureReminder: () -> Unit,
    onClickDiaryWrite: () -> Unit,
    onClickDiaryHistory: () -> Unit,
    onClickTeamInfo: () -> Unit,
    onClickAppReport: () -> Unit,
    onClickOpenLicenses: () -> Unit,
    onClickServiceInfo: () -> Unit,
    onClickPersonalInformationPolicy: () -> Unit,
    onClickNetworkLog: () -> Unit,
    onClickTest: () -> Unit,
    onClickLogout: () -> Unit,
    onConfirmLogout: () -> Unit,
    onDismissLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground)
            .logImpression(AnalyticsScreen.SettingsHome),
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
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
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
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                onClick = onClickUserConfig,
            ) {
                Text(
                    text = uiState.userName,
                    style = SNUTTTypography.body1.copy(
                        color = SNUTTColors.Black500,
                    ),
                )
            }
            SettingItem(
                title = stringResource(R.string.settings_notification_title),
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                redDotIconNumber = uncheckedNotifications,
                onClick = onClickNotification,
            )
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickThemeModeSelect,
                ) {
                    Text(
                        text = stringResource(uiState.themeMode.labelResId),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.timetable_settings_app_bar_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickTimeTableConfig,
                )
                SettingItem(
                    title = stringResource(R.string.settings_timetable_theme_config_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickThemeConfig,
                )
            }
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_item_vacancy),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    hasNextPage = true,
                    onClick = onClickVacancyNotification,
                )
                if (FeatureFlag.THEME_MARKET.isEnabled) {
                    SettingItem(
                        title = stringResource(R.string.settings_item_theme_market),
                        settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                        hasNextPage = true,
                        onClick = onClickThemeMarket,
                    )
                }
                if (FeatureFlag.PUSH_PREFERENCES.isEnabled) {
                    SettingItem(
                        title = stringResource(R.string.settings_item_push_preferences),
                        hasNextPage = true,
                        onClick = onClickPushPreference,
                    )
                }
                if (FeatureFlag.LECTURE_REMINDER.isEnabled) {
                    SettingItem(
                        title = stringResource(R.string.settings_lecture_reminder_title),
                        hasNextPage = true,
                        onClick = onClickLectureReminder,
                    )
                }
                if (FeatureFlag.LECTURE_DIARY.isEnabled) {
                    SettingItem(
                        title = stringResource(R.string.settings_item_lecture_diary),
                        settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                        hasNextPage = true,
                        onClick = onClickDiaryHistory,
                    )
                }
                if (BuildConfig.DEBUG) {
                    SettingItem(
                        title = stringResource(R.string.settings_item_write_lecture_diary),
                        settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                        hasNextPage = true,
                        onClick = onClickDiaryWrite,
                    )
                }
            }
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_version_info),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    hasNextPage = false,
                ) {
                    Text(
                        text = BuildConfig.VERSION_NAME,
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.settings_team_info),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickTeamInfo,
                )
            }
            SettingItem(
                title = stringResource(R.string.settings_app_report_title),
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                onClick = onClickAppReport,
            )
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_licenses_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickOpenLicenses,
                )
                SettingItem(
                    title = stringResource(R.string.settings_service_info),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickServiceInfo,
                )
                SettingItem(
                    title = stringResource(R.string.settings_personal_information_policy),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickPersonalInformationPolicy,
                )
            }
            SettingItem(
                title = stringResource(R.string.settings_logout_title),
                titleColor = SNUTTColors.Red,
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                onClick = onClickLogout,
            )

            if (BuildConfig.DEBUG) {
                SettingItem(
                    title = stringResource(R.string.debug_network_log_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickNetworkLog,
                )
            }

            if (BuildConfig.DEBUG) {
                SettingItem(
                    title = "리팩토링 테스트",
                    onClick = onClickTest,
                )
            }
        }
        bottomBar()
    }

    if (uiState.showLogoutDialog) {
        CustomDialog(
            onDismiss = onDismissLogout,
            onConfirm = onConfirmLogout,
            title = stringResource(R.string.settings_logout_title),
            positiveButtonText = stringResource(R.string.settings_logout_title),
        ) {
            Text(
                text = stringResource(R.string.settings_logout_message),
                style = SNUTTTypography.body2,
            )
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
    settingPageNewBadgeTitles: List<String> = emptyList(),
    redDotIconNumber: Long? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
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
        if (redDotIconNumber != null && redDotIconNumber > 0) {
            RedDotWithNumber(Modifier.padding(start = 8.dp), redDotIconNumber)
        }
        if (settingPageNewBadgeTitles.contains(title)) {
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

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    SettingsScreen(
        uiState = SettingsUiState("양주현", com.wafflestudio.snutt2.ui.ThemeMode.DARK, false, listOf("빈자리 알림")),
        uncheckedNotifications = 0L,
        onClickUserConfig = {},
        onClickNotification = {},
        onClickThemeModeSelect = {},
        onClickTimeTableConfig = {},
        onClickThemeConfig = {},
        onClickVacancyNotification = {},
        onClickThemeMarket = {},
        onClickPushPreference = {},
        onClickLectureReminder = {},
        onClickDiaryWrite = {},
        onClickDiaryHistory = {},
        onClickTeamInfo = {},
        onClickAppReport = {},
        onClickOpenLicenses = {},
        onClickServiceInfo = {},
        onClickPersonalInformationPolicy = {},
        onClickNetworkLog = {},
        onClickTest = {},
        onClickLogout = {},
        onConfirmLogout = {},
        onDismissLogout = {},
    )
}
