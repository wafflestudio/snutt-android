package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.config.FeatureFlag
import com.wafflestudio.snutt2.domain.model.ThemeMode
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

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
                SnuttIcon(
                    R.drawable.ic_horizontal_more_unselected,
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
                    SnuttIcon(
                        R.drawable.ic_person,
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
                        text = stringResource(
                            when (uiState.themeMode) {
                                ThemeMode.DARK -> R.string.theme_mode_dark
                                ThemeMode.LIGHT -> R.string.theme_mode_light
                                ThemeMode.AUTO -> R.string.theme_mode_auto
                            },
                        ),
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

@SnuttPreview
@Composable
private fun SettingsScreen_Default() {
    SnuttPreviewSurface {
        SettingsScreen(
            uiState = SettingsUiState("양주현", ThemeMode.DARK, false, listOf("빈자리 알림")),
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
}
