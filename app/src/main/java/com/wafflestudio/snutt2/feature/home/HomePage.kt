package com.wafflestudio.snutt2.feature.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.feature.friend.FriendsRoute
import com.wafflestudio.snutt2.feature.home.drawer.TimeTableRoute
import com.wafflestudio.snutt2.feature.home.popups.Popup
import com.wafflestudio.snutt2.feature.reviews.ReviewRoute
import com.wafflestudio.snutt2.feature.search.SearchRoute
import com.wafflestudio.snutt2.feature.settings.SettingsRoute
import com.wafflestudio.snutt2.logging.compose.PopupLoggingEffect
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun HomePageRoute(
    viewModel: HomePageViewModel = hiltViewModel(),
    onNavigateLectureDetailNew: (lectureId: String, tableId: String?, isFromTimetable: Boolean) -> Unit,
    onNavigateThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateAddLecture: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onNavigateUserConfig: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
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
    onNavigateToReview: (SearchedLecture) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomePageUiEvent.NavigateToLectureDetail -> {
                    val isFromTimetable = viewModel.uiState.value.currentTab == HomeItem.Timetable
                    onNavigateLectureDetailNew(event.lectureId, event.tableId, isFromTimetable)
                }

                is HomePageUiEvent.OpenUrl -> {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                    }
                }
            }
        }
    }

    BackHandler(enabled = uiState.currentTab != HomeItem.Timetable) {
        viewModel.updateTab(HomeItem.Timetable)
    }

    HomePageNewScreen(
        uiState = uiState,
        onTabSelected = viewModel::updateTab,
        onNavigateLectureDetail = viewModel::onNavigateLectureDetail,
        onNavigateAddLecture = onNavigateAddLecture,
        onNavigateThemeDetail = onNavigateThemeDetail,
        onNavigateLecturesOfTable = onNavigateLecturesOfTable,
        onNavigateVacancyNotification = onNavigateVacancyNotification,
        onNavigateBookmark = onNavigateBookmark,
        onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
        onNavigateUserConfig = onNavigateUserConfig,
        onNavigateNotification = onNavigateNotification,
        onNavigateThemeModeSelect = onNavigateThemeModeSelect,
        onNavigateTimeTableConfig = onNavigateTimeTableConfig,
        onNavigateThemeConfig = onNavigateThemeConfig,
        onNavigateThemeMarket = onNavigateThemeMarket,
        onNavigatePushPreference = onNavigatePushPreference,
        onNavigateLectureReminder = onNavigateLectureReminder,
        onNavigateDiaryWrite = onNavigateDiaryWrite,
        onNavigateDiaryHistory = onNavigateDiaryHistory,
        onNavigateTeamInfo = onNavigateTeamInfo,
        onNavigateAppReport = onNavigateAppReport,
        onNavigateOpenLicenses = onNavigateOpenLicenses,
        onNavigateServiceInfo = onNavigateServiceInfo,
        onNavigatePersonalInformationPolicy = onNavigatePersonalInformationPolicy,
        onNavigateNetworkLog = onNavigateNetworkLog,
        onNavigateTest = onNavigateTest,
        onNavigateToReview = onNavigateToReview,
        onPopupClickFewDays = viewModel::closePopupWithHiddenDays,
        onPopupClickClose = viewModel::closePopup,
        onPopupClickImage = viewModel::onPopupImageClick,
    )
}

@Composable
private fun HomePageNewScreen(
    uiState: HomePageUiState,
    onTabSelected: (HomeItem) -> Unit,
    onNavigateLectureDetail: (LocalLecture) -> Unit,
    onNavigateAddLecture: () -> Unit,
    onNavigateThemeDetail: () -> Unit,
    onNavigateLecturesOfTable: () -> Unit,
    onNavigateVacancyNotification: () -> Unit,
    onNavigateBookmark: () -> Unit,
    onNavigateOnboardAsOrigin: () -> Unit,
    onNavigateUserConfig: () -> Unit,
    onNavigateNotification: () -> Unit,
    onNavigateThemeModeSelect: () -> Unit,
    onNavigateTimeTableConfig: () -> Unit,
    onNavigateThemeConfig: () -> Unit,
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
    onNavigateToReview: (SearchedLecture) -> Unit,
    onPopupClickFewDays: () -> Unit,
    onPopupClickClose: () -> Unit,
    onPopupClickImage: () -> Unit,
) {
    val bottomBar: @Composable () -> Unit = {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                SNUTTColors.Gray100,
                            ),
                        ),
                    ),
            )
            BottomNavigation(
                pageState = uiState.currentTab,
                uncheckedNotificationExist = uiState.uncheckedNotificationCount > 0,
                onUpdatePageState = onTabSelected,
            )
        }
    }

    when (uiState.currentTab) {
        HomeItem.Timetable -> {
            TimeTableRoute(
                bottomBar = bottomBar,
                onNavigateBottomSheetThemeDetail = onNavigateThemeDetail,
                onNavigateLecturesOfTable = onNavigateLecturesOfTable,
                onNavigateVacancyNotification = onNavigateVacancyNotification,
                onNavigateLectureDiaryHistory = onNavigateDiaryHistory,
                onNavigateLectureDetail = onNavigateLectureDetail,
                onNavigateBookmark = onNavigateBookmark,
                onNavigateSearch = { onTabSelected(HomeItem.Search) },
                onNavigateAddLecture = onNavigateAddLecture,
            )
        }

        HomeItem.Search -> SearchRoute(
            bottomBar = bottomBar,
            onNavigateVacancy = onNavigateVacancyNotification,
            onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
            onNavigateToReview = onNavigateToReview,
        )

        HomeItem.Review -> ReviewRoute(bottomBar = bottomBar)

        HomeItem.Friends -> FriendsRoute(bottomBar = bottomBar)

        HomeItem.Settings -> SettingsRoute(
            bottomBar = bottomBar,
            uncheckedNotifications = uiState.uncheckedNotificationCount,
            onNavigateUserConfig = onNavigateUserConfig,
            onNavigateNotification = onNavigateNotification,
            onNavigateThemeModeSelect = onNavigateThemeModeSelect,
            onNavigateTimeTableConfig = onNavigateTimeTableConfig,
            onNavigateThemeConfig = onNavigateThemeConfig,
            onNavigateVacancyNotification = onNavigateVacancyNotification,
            onNavigateThemeMarket = onNavigateThemeMarket,
            onNavigatePushPreference = onNavigatePushPreference,
            onNavigateLectureReminder = onNavigateLectureReminder,
            onNavigateDiaryWrite = onNavigateDiaryWrite,
            onNavigateDiaryHistory = onNavigateDiaryHistory,
            onNavigateTeamInfo = onNavigateTeamInfo,
            onNavigateAppReport = onNavigateAppReport,
            onNavigateOpenLicenses = onNavigateOpenLicenses,
            onNavigateServiceInfo = onNavigateServiceInfo,
            onNavigatePersonalInformationPolicy = onNavigatePersonalInformationPolicy,
            onNavigateNetworkLog = onNavigateNetworkLog,
            onNavigateTest = onNavigateTest,
            onNavigateOnboardAsOrigin = onNavigateOnboardAsOrigin,
        )
    }

    if (uiState.shouldShowPopup) {
        PopupLoggingEffect(uiState.popupImageUri)
        Popup(
            onClickFewDays = onPopupClickFewDays,
            onClickClose = onPopupClickClose,
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clicks { onPopupClickImage() },
                model = uiState.popupImageUri,
                contentDescription = "",
                error = painterResource(id = R.drawable.img_reviews_coming_soon),
            )
        }
    }
}
