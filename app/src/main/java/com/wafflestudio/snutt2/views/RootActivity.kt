package com.wafflestudio.snutt2.views

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ShowModal
import com.wafflestudio.snutt2.components.compose.rememberModalState
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_out.*
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
        var isProgressVisible by remember { mutableStateOf(false) }

        val apiOnProgress = remember {
            object : ApiOnProgress {
                override fun showProgress() {
                    isProgressVisible = true
                }

                override fun hideProgress() {
                    isProgressVisible = false
                }
            }
        }

        val dialogState = rememberModalState()
        ShowModal(state = dialogState)

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalApiOnProgress provides apiOnProgress,
            LocalApiOnError provides apiOnError,
            LocalHomePageController provides homePageController,
            LocalPopupState provides popupState,
            LocalModalState provides dialogState,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                onboardGraph()

                composableRoot(NavigationDestination.Home) { HomePage() }

                composable2(NavigationDestination.Notification) { NotificationPage() }

                composable2(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                composable2(NavigationDestination.LectureDetail) { LectureDetailPage() }

                composable2(NavigationDestination.LectureColorSelector) {
                    LectureColorSelectorPage()
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
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route,
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
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route,
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
    api: suspend () -> Unit
) {
    try {
        apiOnProgress.showProgress()
        api.invoke()
    } catch (e: Exception) {
        apiOnError(e)
    } finally {
        apiOnProgress.hideProgress()
    }
}
