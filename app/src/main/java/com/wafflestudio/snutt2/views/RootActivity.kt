package com.wafflestudio.snutt2.views

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.wafflestudio.snutt2.ComposeSlideNavigator
import com.wafflestudio.snutt2.NavHostWithSlideAnimation
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.composable
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailCustomPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_out.SignInPage
import com.wafflestudio.snutt2.views.logged_out.SignUpPage
import com.wafflestudio.snutt2.views.logged_out.TutorialPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : BaseActivity() {
    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    @Inject
    lateinit var popupState: PopupState

    @Inject
    lateinit var apiOnError: ApiOnError

    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        runBlocking {
            token = userViewModel.getAccessToken()  // FIXME: .....
        }
        setContentView(R.layout.activity_root)

        val composeRoot = findViewById<ComposeView>(R.id.compose_root)

        composeRoot.setContent {
            SNUTTTheme {
                setUpUI()
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
        // TODO: 필요한 컨디션 체크하기
        return true
    }

    @Composable
    fun setUpUI() {
        val navController = rememberNavController(ComposeSlideNavigator())
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
            if (token.isEmpty()) NavigationDestination.Onboard
            else NavigationDestination.Home

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalApiOnProgress provides apiOnProgress,
            LocalApiOnError provides apiOnError,
        ) {
            NavHostWithSlideAnimation(
                navController = navController,
                startDestination = startDestination
            ) {

                onboardGraph(navController)

                composable(NavigationDestination.Home) { HomePage() }

                composable(NavigationDestination.Notification) { NotificationPage() }

                composable(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                composable(NavigationDestination.LectureDetail) { LectureDetailPage() }

                composable(NavigationDestination.LectureDetailCustom) { LectureDetailCustomPage() }

                composable(NavigationDestination.LectureColorSelector) {
                    LectureColorSelectorPage()
                }

                settingComposable()
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph(navController: NavController) {
        navigation(
            startDestination = NavigationDestination.Tutorial,
            route = NavigationDestination.Onboard
        ) {
            composable(NavigationDestination.Tutorial) {
                TutorialPage()
            }
            composable(NavigationDestination.SignIn) {
                SignInPage()
            }
            composable(NavigationDestination.SignUp) {
                SignUpPage()
            }
        }
    }

    private fun NavGraphBuilder.settingComposable() {
        composable(NavigationDestination.AppReport) { AppReportPage() }
        composable(NavigationDestination.ServiceInfo) { ServiceInfoPage() }
        composable(NavigationDestination.TeamInfo) { TeamInfoPage() }
        composable(NavigationDestination.TimeTableConfig) { TimetableConfigPage() }
        composable(NavigationDestination.UserConfig) { UserConfigPage() }
        composable(NavigationDestination.PersonalInformationPolicy) { PersonalInformationPolicyPage() }
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

suspend fun launchSuspendApi(apiOnProgress: ApiOnProgress, apiOnError: ApiOnError, api: suspend() -> Unit) {
    try {
        apiOnProgress.showProgress()
        api.invoke()
    } catch (e: Exception) {
        apiOnError(e)
    } finally {
        apiOnProgress.hideProgress()
    }

}
