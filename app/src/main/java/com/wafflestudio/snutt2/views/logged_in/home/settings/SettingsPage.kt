package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.HorizontalMoreIcon
import com.wafflestudio.snutt2.components.compose.PersonIcon
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.featureflag.FeatureFlag
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUserConfig: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateThemeMarket: () -> Unit,
    onNavigatePushPreference: () -> Unit,
    onNavigateLectureDiary: () -> Unit,
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
        onClickUserConfig = onNavigateUserConfig,
        onClickThemeModeSelect = onNavigateThemeModeSelect,
        onClickTimeTableConfig = onNavigateTimeTableConfig,
        onClickThemeConfig = onNavigateThemeConfig,
        onClickVacancyNotification = onNavigateVacancyNotification,
        onClickThemeMarket = onNavigateThemeMarket,
        onClickPushPreference = onNavigatePushPreference,
        onClickLectureDiary = onNavigateLectureDiary,
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
    onClickUserConfig: () -> Unit,
    onClickThemeModeSelect: () -> Unit,
    onClickTimeTableConfig: () -> Unit,
    onClickThemeConfig: () -> Unit,
    onClickVacancyNotification: () -> Unit,
    onClickThemeMarket: () -> Unit,
    onClickPushPreference: () -> Unit,
    onClickLectureDiary: () -> Unit,
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
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_select_color_mode_title),
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickThemeModeSelect,
                ) {
                    Text(
                        text = uiState.themeModeName,
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
            Margin(height = 10.dp)
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
                SettingItem(
                    title = stringResource(R.string.settings_item_push_preferences),
                    hasNextPage = true,
                    onClick = onClickPushPreference,
                )
                if (FeatureFlag.LECTURE_DIARY.isEnabled) {
                    SettingItem(
                        title = stringResource(R.string.settings_item_lecture_diary),
                        settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                        hasNextPage = true,
                        onClick = onClickLectureDiary,
                    )
                }
            }
            Margin(height = 10.dp)
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
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_app_report_title),
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                onClick = onClickAppReport,
            )
            Margin(height = 10.dp)
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
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_logout_title),
                titleColor = SNUTTColors.Red,
                settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                onClick = onClickLogout,
            )

            if (BuildConfig.DEBUG) {
                Margin(height = 10.dp)
                SettingItem(
                    title = "네트워크 로그",
                    settingPageNewBadgeTitles = uiState.settingPageNewBadgeTitles,
                    onClick = onClickNetworkLog,
                )
            }

            if (BuildConfig.DEBUG) {
                Margin(height = 10.dp)
                SettingItem(
                    title = "리팩토링 테스트",
                    onClick = onClickTest,
                )
            }
            Margin(height = 10.dp)
        }
    }

    if (uiState.showLogoutDialog) {
        CustomDialog(
            onDismiss = onDismissLogout,
            onConfirm = onConfirmLogout,
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
    settingPageNewBadgeTitles: List<String> = emptyList(),
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
        uiState = SettingsUiState("양주현", "다크", false, listOf("빈자리 알림")), onClickUserConfig = {}, onClickThemeModeSelect = {}, onClickTimeTableConfig = {}, onClickThemeConfig = {}, onClickVacancyNotification = {}, onClickThemeMarket = {}, onClickPushPreference = {}, onClickLectureDiary = {}, onClickTeamInfo = {}, onClickAppReport = {}, onClickOpenLicenses = {}, onClickServiceInfo = {}, onClickPersonalInformationPolicy = {}, onClickNetworkLog = {}, onClickTest = {}, onClickLogout = {}, onConfirmLogout = {}, onDismissLogout = {},
    )
}
