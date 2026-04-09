package com.wafflestudio.snutt2.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.lib.featureflag.FeatureFlag
import kotlin.reflect.typeOf
import com.wafflestudio.snutt2.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.test.TestRoute
import com.wafflestudio.snutt2.views.logged_in.home.HomePageRoute
import com.wafflestudio.snutt2.views.logged_in.home.bookmark.BookmarkRoute
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewBottomSheetRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.AppReportPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.ChangeNicknamePage
import com.wafflestudio.snutt2.views.logged_in.home.settings.LectureReminderRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.LicenseDetailPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.NetworkLogPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.OpenSourceLicensePage
import com.wafflestudio.snutt2.views.logged_in.home.settings.PersonalInformationPolicyPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.PushPreferencesRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.ServiceInfoPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.SocialLinkPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.TeamInfoPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.ThemeModeSelectPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.TimetableConfigRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserConfigRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history.DiaryHistoryRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write.DiaryWriteRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeConfigRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeDetailRoute
import com.wafflestudio.snutt2.feature.lecture_detail.current_table.AddCustomLectureRoute
import com.wafflestudio.snutt2.feature.lecture_detail.current_table.CurrentTableLectureDetailRoute
import com.wafflestudio.snutt2.feature.lecture_detail.current_table.LectureColorSelectorRoute
import com.wafflestudio.snutt2.feature.lecture_detail.deeplink.DeeplinkBookmarkLectureDetailRoute
import com.wafflestudio.snutt2.feature.lecture_detail.deeplink.DeeplinkTimetableLectureDetailRoute
import com.wafflestudio.snutt2.feature.notifications.NotificationRoute
import com.wafflestudio.snutt2.feature.table_lectures.TableLecturesRoute
import com.wafflestudio.snutt2.feature.thememarket.ThemeMarketRoute
import com.wafflestudio.snutt2.feature.vacancy_noti.VacancyRoute
import com.wafflestudio.snutt2.feature.login.EmailVerificationPage
import com.wafflestudio.snutt2.feature.login.FindIdPage
import com.wafflestudio.snutt2.feature.login.ImportantNoticeRoute
import com.wafflestudio.snutt2.feature.login.SignInPage
import com.wafflestudio.snutt2.feature.login.SignUpPage
import com.wafflestudio.snutt2.feature.login.TutorialPage
import com.wafflestudio.snutt2.feature.login.reset_password.ResetPasswordPage

internal fun NavGraphBuilder.buildRootNavGraph(
    navController: NavController,
    scheme: String,
) {
    onboardGraph(navController, scheme)

    composableRoot<NavigationDestination.Home> {
        HomePageRoute(
            onNavigateLectureDetailNew = { lectureId, tableId, isFromTimetable ->
                navController.navigate(
                    NavigationDestination.LectureDetail(lectureId = lectureId, tableId = tableId, isFromTimetable = isFromTimetable),
                ) {
                    launchSingleTop = true
                }
            },
            onNavigateThemeDetail = { navController.navigate(NavigationDestination.ThemeDetail()) },
            onNavigateLecturesOfTable = { navController.navigate(NavigationDestination.LecturesOfTable) },
            onNavigateVacancyNotification = { navController.navigate(NavigationDestination.VacancyNotification) },
            onNavigateBookmark = { navController.navigate(NavigationDestination.Bookmark) },
            onNavigateAddLecture = { navController.navigate(NavigationDestination.AddCustomLecture) },
            onNavigateOnboardAsOrigin = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            onNavigateUserConfig = { navController.navigate(NavigationDestination.UserConfig) },
            onNavigateNotification = { navController.navigate(NavigationDestination.Notification) },
            onNavigateThemeModeSelect = { navController.navigate(NavigationDestination.ThemeModeSelect) },
            onNavigateTimeTableConfig = { navController.navigate(NavigationDestination.TimeTableConfig) },
            onNavigateThemeConfig = { navController.navigate(NavigationDestination.ThemeConfig) },
            onNavigateThemeMarket = { navController.navigate(NavigationDestination.ThemeMarket) },
            onNavigatePushPreference = { navController.navigate(NavigationDestination.PushPreferences) },
            onNavigateLectureReminder = { navController.navigate(NavigationDestination.LectureReminder) },
            onNavigateDiaryWrite = {
                navController.navigate(
                    NavigationDestination.LectureDiaryWrite(
                        lectureId = "695affb59dfd1a77c7c20778",
                        courseTitle = "데이터사이언스를 위한 컴퓨팅 시스템",
                    ),
                )
            },
            onNavigateDiaryHistory = { navController.navigate(NavigationDestination.LectureDiaryHistory) },
            onNavigateTeamInfo = { navController.navigate(NavigationDestination.TeamInfo) },
            onNavigateAppReport = { navController.navigate(NavigationDestination.AppReport) },
            onNavigateOpenLicenses = { navController.navigate(NavigationDestination.OpenLicenses) },
            onNavigateServiceInfo = { navController.navigate(NavigationDestination.ServiceInfo) },
            onNavigatePersonalInformationPolicy = { navController.navigate(NavigationDestination.PersonalInformationPolicy) },
            onNavigateNetworkLog = { navController.navigate(NavigationDestination.NetworkLog) },
            onNavigateTest = { navController.navigate(NavigationDestination.Test) },
            onNavigateToReview = { lecture ->
                navController.navigate(
                    NavigationDestination.Review(
                        reviewId = lecture.reviewInfo.id,
                        lectureId = lecture.id,
                    ),
                )
            },
        )
    }

    composableAnimated<NavigationDestination.ImportantNotice>(scheme) {
        ImportantNoticeRoute(
            onNavigateAppReport = { navController.navigate(NavigationDestination.AppReport) },
        )
    }

    composableAnimated<NavigationDestination.Notification>(scheme) {
        NotificationRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTimetableLectureDetail = { lectureId, timetableId ->
                navController.navigate(NavigationDestination.DeeplinkTimetableLectureDetail(lectureId, timetableId))
            },
            onNavigateToBookmarkLectureDetail = { lectureId, year, semester ->
                navController.navigate(NavigationDestination.DeeplinkBookmarkLectureDetail(lectureId, year, semester))
            },
            onNavigateToFriends = {
                navController.navigateAsOrigin(NavigationDestination.Home(initialTab = "friends"))
            },
        )
    }

    composableAnimated<NavigationDestination.LecturesOfTable>(scheme) {
        TableLecturesRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateLectureDetail = { lectureId, tableId ->
                navController.navigate(NavigationDestination.LectureDetail(lectureId, tableId))
            },
        )
    }

    composableAnimated<NavigationDestination.LectureDetail>(scheme) { backStackEntry ->
        val route = backStackEntry.toRoute<NavigationDestination.LectureDetail>()
        val referrer = when {
            navController.previousBackStackEntry?.destination?.hasRoute(
                NavigationDestination.LecturesOfTable::class,
            ) == true
                -> DetailScreenReferrer.LectureList

            route.isFromTimetable -> DetailScreenReferrer.Timetable
            else -> null
        }

        CurrentTableLectureDetailRoute(
            referrer = referrer,
            colorSelectorSavedStateHandle = backStackEntry.savedStateHandle,
            onNavigateBack = { navController.popBackStack() },
            onNavigateColorSelector = { currentColor ->
                navController.navigate(NavigationDestination.LectureColorSelector(currentColor))
            },
            onNavigateLectureReminder = { navController.navigate(NavigationDestination.LectureReminder) },
            onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            onNavigateToReview = { reviewId, lectureId ->
                navController.navigate(
                    NavigationDestination.Review(
                        reviewId = reviewId,
                        lectureId = lectureId,
                        referrer = (referrer ?: DetailScreenReferrer.LectureDetail).encode(),
                    ),
                )
            },
        )
    }

    composableAnimated<NavigationDestination.Bookmark>(scheme) {
        BookmarkRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            onNavigateToReview = { lecture ->
                navController.navigate(
                    NavigationDestination.Review(
                        reviewId = lecture.reviewInfo.id,
                        lectureId = lecture.id,
                        referrer = DetailScreenReferrer.Bookmark.encode(),
                    ),
                )
            },
        )
    }

    composableAnimated<NavigationDestination.LectureColorSelector>(
        scheme = scheme,
        typeMap = mapOf(typeOf<LectureColor>() to LectureColorNavType),
    ) {
        LectureColorSelectorRoute(
            onNavigateBackWithResult = { selectedColor ->
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(NavigationDestination.LectureColorSelector.RESULT_KEY, selectedColor)
                navController.popBackStack()
            },
        )
    }

    composableAnimated<NavigationDestination.AddCustomLecture>(scheme) { backStackEntry ->
        AddCustomLectureRoute(
            colorSelectorSavedStateHandle = backStackEntry.savedStateHandle,
            onNavigateBack = { navController.popBackStack() },
            onNavigateColorSelector = { currentColor ->
                navController.navigate(NavigationDestination.LectureColorSelector(currentColor))
            },
            onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
        )
    }

    bottomSheet<NavigationDestination.ThemeDetail> {
        ThemeDetailRoute(
            onNavigateBack = { navController.popBackStack() },
        )
    }

    bottomSheet<NavigationDestination.Review> {
        ReviewBottomSheetRoute(
            onNavigateBack = { navController.popBackStack() },
        )
    }

    composableAnimated<NavigationDestination.DeeplinkTimetableLectureDetail>(scheme) {
        DeeplinkTimetableLectureDetailRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home()) },
            onNavigateToReview = { reviewId, lectureId ->
                navController.navigate(
                    NavigationDestination.Review(
                        reviewId = reviewId,
                        lectureId = lectureId,
                        referrer = DetailScreenReferrer.Notification.encode(),
                    ),
                )
            },
        )
    }

    composableAnimated<NavigationDestination.DeeplinkBookmarkLectureDetail>(scheme) {
        DeeplinkBookmarkLectureDetailRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToReview = { reviewId, lectureId ->
                navController.navigate(
                    NavigationDestination.Review(
                        reviewId = reviewId,
                        lectureId = lectureId,
                        referrer = DetailScreenReferrer.Bookmark.encode(),
                    ),
                )
            },
        )
    }

    settingComposables(navController, scheme)
}

private fun NavGraphBuilder.onboardGraph(navController: NavController, scheme: String) {
    navigation<NavigationDestination.Onboard>(
        startDestination = NavigationDestination.Tutorial,
    ) {
        composableRoot<NavigationDestination.Tutorial> {
            TutorialPage(
                onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home()) },
                onNavigateSignIn = { navController.navigate(NavigationDestination.SignIn) },
                onNavigateSignUp = { navController.navigate(NavigationDestination.SignUp) },
                onNavigateAppReport = { navController.navigate(NavigationDestination.AppReport) },
            )
        }
        composableAnimated<NavigationDestination.SignIn>(scheme) {
            SignInPage(
                onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home()) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateFindId = { navController.navigate(NavigationDestination.FindId) },
                onNavigateFindPassword = { navController.navigate(NavigationDestination.FindPassword) },
            )
        }
        composableAnimated<NavigationDestination.SignUp>(scheme) {
            SignUpPage(
                onNavigateEmailVerification = { navController.navigate(NavigationDestination.EmailVerification) },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composableAnimated<NavigationDestination.FindId>(scheme) {
            FindIdPage(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composableAnimated<NavigationDestination.FindPassword>(scheme) {
            ResetPasswordPage(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composableAnimated<NavigationDestination.EmailVerification>(scheme) {
            EmailVerificationPage(
                onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home()) },
            )
        }
    }
}

private fun NavGraphBuilder.settingComposables(
    navController: NavController,
    scheme: String,
) {
    composableAnimated<NavigationDestination.AppReport>(scheme) {
        AppReportPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.OpenLicenses>(scheme) {
        OpenSourceLicensePage(
            onNavigateBack = { navController.popBackStack() },
            onNavigateLicenseDetail = { licenseName ->
                navController.navigate(NavigationDestination.LicenseDetail(licenseName))
            },
        )
    }

    composableAnimated<NavigationDestination.LicenseDetail>(scheme) { backStackEntry ->
        val licenseName =
            backStackEntry.toRoute<NavigationDestination.LicenseDetail>().licenseName
        LicenseDetailPage(
            licenseName = licenseName,
            onNavigateBack = { navController.popBackStack() },
        )
    }

    composableAnimated<NavigationDestination.ServiceInfo>(scheme) {
        ServiceInfoPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.TeamInfo>(scheme) {
        TeamInfoPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.TimeTableConfig>(scheme) {
        TimetableConfigRoute(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.TimeTableConfig::class) == true) {
                    navController.popBackStack()
                }
            },
            onNavigateOnboard = {
                navController.navigateAsOrigin(NavigationDestination.Onboard)
            },
        )
    }
    composableAnimated<NavigationDestination.UserConfig>(scheme) {
        UserConfigRoute(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.UserConfig::class) == true) {
                    navController.popBackStack()
                }
            },
            onNavigateOnboard = {
                navController.navigateAsOrigin(NavigationDestination.Onboard)
            },
            onNavigateChangeNickname = {
                navController.navigate(NavigationDestination.ChangeNickname)
            },
            onNavigateSocialLink = {
                navController.navigate(NavigationDestination.SocialLink)
            },
        )
    }
    composableAnimated<NavigationDestination.ChangeNickname>(scheme) {
        ChangeNicknamePage(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.ChangeNickname::class) == true) {
                    navController.popBackStack()
                }
            },
        )
    }
    composableAnimated<NavigationDestination.SocialLink>(scheme) {
        SocialLinkPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.PersonalInformationPolicy>(scheme) {
        PersonalInformationPolicyPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.ThemeModeSelect>(scheme) {
        ThemeModeSelectPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }
    if (FeatureFlag.LECTURE_DIARY.isEnabled) {
        composableAnimated<NavigationDestination.LectureDiaryWrite>(scheme) { entry ->
            DiaryWriteRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.LectureDiaryWrite::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = {
                    navController.navigateAsOrigin(NavigationDestination.Onboard)
                },
                onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home()) },
                onNavigateReview = {
                    navController.navigateAsOrigin(NavigationDestination.Home(initialTab = "review"))
                },
                onNavigateNextDiaryWrite = { lectureId, courseTitle ->
                    navController.navigate(
                        NavigationDestination.LectureDiaryWrite(lectureId, courseTitle),
                    ) {
                        popUpTo<NavigationDestination.LectureDiaryWrite> {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composableAnimated<NavigationDestination.LectureDiaryHistory>(scheme) { entry ->
            DiaryHistoryRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.LectureDiaryHistory::class) == true) {
                        navController.popBackStack()
                    }
                },
            )
        }
    }
    composableAnimated<NavigationDestination.VacancyNotification>(scheme) {
        VacancyRoute(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.VacancyNotification::class) == true) {
                    navController.popBackStack()
                }
            },
            onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
        )
    }
    composableAnimated<NavigationDestination.PushPreferences>(scheme) {
        PushPreferencesRoute(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.PushPreferences::class) == true) {
                    navController.popBackStack()
                }
            },
            onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
        )
    }
    composableAnimated<NavigationDestination.LectureReminder>(scheme) {
        LectureReminderRoute(
            onNavigateBack = {
                if (navController.currentDestination?.hasRoute(NavigationDestination.LectureReminder::class) == true) {
                    navController.popBackStack()
                }
            },
            onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
        )
    }
    composableAnimated<NavigationDestination.ThemeMarket>(scheme) {
        ThemeMarketRoute(
            onBackClick = { navController.popBackStack() },
        )
    }
    composableAnimated<NavigationDestination.ThemeConfig>(scheme) {
        ThemeConfigRoute(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDetail = { theme ->
                when (theme) {
                    is CustomTheme -> navController.navigate(
                        NavigationDestination.ThemeDetail(themeId = theme.id),
                    )

                    is BuiltInTheme -> navController.navigate(
                        NavigationDestination.ThemeDetail(theme = theme.code),
                    )
                }
            },
            onClickAddTheme = {
                navController.navigate(NavigationDestination.ThemeDetail())
            },
        )
    }
    if (BuildConfig.DEBUG) composableAnimated<NavigationDestination.NetworkLog>(scheme) {
        NetworkLogPage(
            onNavigateBack = { navController.popBackStack() },
        )
    }

    if (BuildConfig.DEBUG) {
        composableAnimated<NavigationDestination.Test>(scheme) {
            TestRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.Test::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            )
        }
    }
}

internal inline fun <reified T : NavigationDestination> NavGraphBuilder.composableAnimated(
    scheme: String,
    typeMap: Map<kotlin.reflect.KType, NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = listOfNotNull(
            getDeepLinkPath<T>()?.let { deepLinkPath ->
                // uri pattern 생성 규칙: https://developer.android.com/reference/kotlin/androidx/navigation/NavDeepLink.Builder?_gl=1*uaoct7*_up*MQ..*_ga*MjExMzE2MzgxOS4xNzQzMjQ2NzMw*_ga_6HH9YJMN9M*MTc0MzI0NjczMC4xLjAuMTc0MzI0NjczMC4wLjAuMzkxMTIxMTgx#setUriPattern(kotlin.String,kotlin.collections.Map)
                // NavigationDestination에 선언된 argument들을 조합해서 위 규칙에 따라 uri pattern 을 생성함.
                // 푸시 알림의 url_scheme을 uri pattern에 맞게 쏘면 해당 화면으로 잘 랜딩됨.
                navDeepLink<T>(basePath = "$scheme$deepLinkPath", typeMap = typeMap)
            },
        ),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
            )
        },
        exitTransition = { fadeOut(targetAlpha = 0.0f) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) },
        popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
        content = content,
    )
}

internal inline fun <reified T : NavigationDestination> NavGraphBuilder.composableRoot(
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut(targetAlpha = 0.0f) },
        popExitTransition = { fadeOut() },
        popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
        content = content,
    )
}

fun <T : NavigationDestination> NavController.navigateAsOrigin(route: T) {
    navigate(route) {
        while (popBackStack()) {
            /* pop back until end */
        }
        launchSingleTop = true
        restoreState = true
    }
}
