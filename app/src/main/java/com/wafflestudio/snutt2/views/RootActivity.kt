package com.wafflestudio.snutt2.views

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailCustomPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_out.SignInPage
import com.wafflestudio.snutt2.views.logged_out.SignUpPage
import com.wafflestudio.snutt2.views.logged_out.TutorialPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class RootActivity : BaseActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private val timetableViewModel: TimetableViewModel by viewModels()

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    @Inject
    lateinit var popupState: PopupState

    @Inject
    lateinit var apiOnError: ApiOnError

    private var isInitialRefreshFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val composeRoot = findViewById<ComposeView>(R.id.compose_root)

        lifecycleScope.launch {
            val token = userViewModel.accessToken.filterNotNull().first()
            if (token.isNotEmpty()) {
                homeViewModel.refreshData()
            }
            isInitialRefreshFinished = true
        }

        composeRoot.setContent {
            val accessToken: String? by userViewModel.accessToken.collectAsState()

            SNUTTTheme {
                val token = accessToken
                if (token != null) {
                    setUpUI(isLoggedOut = token.isEmpty())
                }
            }
        }

        setUpSplashScreen(composeRoot)
    }

    private fun setUpSplashScreen(rootView: View) {
        splashScreen.setOnExitAnimationListener { view ->
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).run {
                interpolator = AnticipateInterpolator()
                duration = 200L
                doOnEnd { view.remove() }
                start()
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
    fun setUpUI(isLoggedOut: Boolean) {
        val navController = rememberAnimatedNavController()
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

        val startDestination =
            if (isLoggedOut) NavigationDestination.Onboard
            else NavigationDestination.Home

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalApiOnProgress provides apiOnProgress,
            LocalApiOnError provides apiOnError,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                onboardGraph(navController)

                composable2(NavigationDestination.Home) { HomePage() }

                composable2(NavigationDestination.Notification) { NotificationPage() }

                composable2(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                composable2(NavigationDestination.LectureDetail) { LectureDetailPage() }

                composable2(NavigationDestination.LectureDetailCustom) { LectureDetailCustomPage() }

                composable2(NavigationDestination.LectureColorSelector) {
                    LectureColorSelectorPage()
                }

                settingcomposable2()
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph(navController: NavController) {
        navigation(
            startDestination = NavigationDestination.Tutorial,
            route = NavigationDestination.Onboard
        ) {
            composable2(NavigationDestination.Tutorial) {
                TutorialPage()
            }
            composable2(NavigationDestination.SignIn) {
                SignInPage()
            }
            composable2(NavigationDestination.SignUp) {
                SignUpPage()
            }
        }
    }

    private fun NavGraphBuilder.composable2(
        route: String,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) },
            exitTransition = { fadeOut(targetAlpha = 0.0f) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) },
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
    }
}

fun NavController.navigateAsOrigin(route: String) {
    navigate(route) {
        popUpTo(this@navigateAsOrigin.graph.findStartDestination().id) {
            saveState = true
            inclusive = true
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
