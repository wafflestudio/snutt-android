package com.wafflestudio.snutt2.views

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.LoadingIndicator
import com.wafflestudio.snutt2.components.compose.ShowModal
import com.wafflestudio.snutt2.components.compose.bottomSheet
import com.wafflestudio.snutt2.components.compose.rememberModalState
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.views.logged_in.bookmark.BookmarkPage
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.AppReportPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.ColorModeSelectPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.PersonalInformationPolicyPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.ServiceInfoPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.TeamInfoPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.TimetableConfigPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserConfigPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_out.FindIdPage
import com.wafflestudio.snutt2.views.logged_out.FindPasswordPage
import com.wafflestudio.snutt2.views.logged_out.SignInPage
import com.wafflestudio.snutt2.views.logged_out.SignUpPage
import com.wafflestudio.snutt2.views.logged_out.TutorialPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var popupState: PopupState

    @Inject
    lateinit var apiOnError: ApiOnError

    private var isInitialRefreshFinished = false

    private val composeRoot by lazy { findViewById<ComposeView>(R.id.compose_root) }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        lifecycleScope.launch {
            homeViewModel.refreshData()
            isInitialRefreshFinished = true
        }
        val token = userViewModel.accessToken.value
        setUpContents(
            if (token.isEmpty()) NavigationDestination.Onboard
            else NavigationDestination.Home
        )
        setUpSplashScreen(composeRoot)
        startUpdatingPushToken()
    }

    private fun setUpContents(startDestination: String) {
        composeRoot.setContent {
            val themeMode by userViewModel.themeMode.collectAsState()
            CompositionLocalProvider(LocalThemeState provides themeMode) {
                SNUTTTheme {
                    setUpUI(startDestination)
                }
            }
        }
    }

    private fun setUpSplashScreen(rootView: View) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { view ->
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).run {
                    interpolator = AnticipateInterpolator()
                    duration = 200L
                    doOnEnd { view.remove() }
                    start()
                }
            }
        }

        rootView.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isInitialConditionsSatisfied()) {
                        rootView.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    fun isInitialConditionsSatisfied(): Boolean {
        return isInitialRefreshFinished
    }

    @Composable
    fun setUpUI(startDestination: String) {
        val navController = rememberAnimatedNavController()
        val homePageController = remember { HomePageController() }
        val compactMode by userViewModel.compactMode.collectAsState()

        val bottomSheet = bottomSheet()
        val dialogState = rememberModalState()
        ShowModal(state = dialogState)

        val apiOnProgress = remember {
            object : ApiOnProgress {
                override var progressShowing: Boolean = false

                override fun showProgress(title: String?) {
                    if (title != null) {
                        progressShowing = true
                        dialogState.set(onDismiss = {}, title = title) {
                        LoadingIndicator()
                    }.show()
                    }
                }

                override fun hideProgress() {
                    if (progressShowing) {
                        dialogState.hide()
                        progressShowing = false
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalApiOnProgress provides apiOnProgress,
            LocalApiOnError provides apiOnError,
            LocalHomePageController provides homePageController,
            LocalPopupState provides popupState,
            LocalModalState provides dialogState,
            LocalCompactState provides compactMode,
            LocalBottomSheetState provides bottomSheet,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                onboardGraph()

                composableRoot(
                    NavigationDestination.Home,
                    deepLinks = listOf(navDeepLink { uriPattern = "https://snutt-dev.wafflestudio.com?mobileCtaLink=snutt://share?timetableId={id}" })
                ) {navBackStackEntry ->
                    LaunchedEffect(Unit) {
                        navBackStackEntry.arguments?.getString("id")?.let { timetableId ->
                            navBackStackEntry.arguments?.clear()
                            homePageController.update(HomeItem.Share(timetableId))
                        }
                    }
                    HomePage()
                }

                composable2(NavigationDestination.Notification) { NotificationPage() }

                composable2(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                composable2(NavigationDestination.LectureDetail) {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry(NavigationDestination.Home)
                    }
                    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>(parentEntry)
                    LectureDetailPage(lectureDetailViewModel)
                }

                composable2(NavigationDestination.LectureColorSelector) {
                    LectureColorSelectorPage()
                }

                composable2(NavigationDestination.Bookmark) {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry(NavigationDestination.Home)
                    }
                    val searchViewModel = hiltViewModel<SearchViewModel>(parentEntry)
                    BookmarkPage(searchViewModel)
                }

                settingcomposable2()
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph() {
        navigation(
            startDestination = NavigationDestination.Tutorial,
            route = NavigationDestination.Onboard
        ) {
            composableRoot(NavigationDestination.Tutorial) {
                TutorialPage()
            }
            composable2(NavigationDestination.SignIn) {
                SignInPage()
            }
            composable2(NavigationDestination.SignUp) {
                SignUpPage()
            }

            composable2(NavigationDestination.FindId) {
                FindIdPage()
            }

            composable2(NavigationDestination.FindPassword) {
                FindPasswordPage()
            }
        }
    }

    private fun NavGraphBuilder.composable2(
        route: String,
        deepLinks: List<NavDeepLink> = emptyList(),
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route,
            deepLinks = deepLinks,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    )
                )
            },
            exitTransition = { fadeOut(targetAlpha = 0.0f) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) },
            popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
            content = content
        )
    }

    private fun NavGraphBuilder.composableRoot(
        route: String,
        deepLinks: List<NavDeepLink> = emptyList(),
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route,
            deepLinks = deepLinks,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut(targetAlpha = 0.0f) },
            popExitTransition = { fadeOut() },
            popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
            content = content
        )
    }

    private fun NavGraphBuilder.settingcomposable2() {
        composable2(NavigationDestination.AppReport) { AppReportPage() }
        composable2(NavigationDestination.ServiceInfo) { ServiceInfoPage() }
        composable2(NavigationDestination.TeamInfo) { TeamInfoPage() }
        composable2(NavigationDestination.TimeTableConfig) { TimetableConfigPage() }
        composable2(NavigationDestination.UserConfig) { UserConfigPage() }
        composable2(NavigationDestination.PersonalInformationPolicy) { PersonalInformationPolicyPage() }
        composable2(NavigationDestination.ThemeModeSelect) { ColorModeSelectPage() }
    }

    private fun startUpdatingPushToken() {
        lifecycleScope.launch {
            userViewModel.accessToken.collect {
                if (it.isNotEmpty()) {
                    kotlin.runCatching {
                        userViewModel.registerPushToken()
                    } // do nothing on error.
                }
            }
        }
    }
}

fun NavController.navigateAsOrigin(route: String) {
    navigate(route) {
        while (popBackStack()) {
            /* pop back until end */
        }
        launchSingleTop = true
        restoreState = true
    }
}

suspend fun launchSuspendApi(
    apiOnProgress: ApiOnProgress,
    apiOnError: ApiOnError,
    onError: () -> Unit = {},
    loadingIndicatorTitle: String? = null,
    api: suspend () -> Unit
) {
    try {
        loadingIndicatorTitle?.let { apiOnProgress.showProgress(it) }
        api.invoke()
    } catch (e: Exception) {
        apiOnError(e)
        onError()
    } finally {
        if (loadingIndicatorTitle != null) apiOnProgress.hideProgress()
    }
}
