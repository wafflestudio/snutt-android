package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
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
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : BaseActivity() {

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    @Inject
    lateinit var popupState: PopupState

    @Inject
    lateinit var apiOnError: ApiOnError

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        findViewById<ComposeView>(R.id.compose_root)
            .setContent {
                setUpUI()
            }
    }

    @Composable
    fun setUpUI() {
        val navController = rememberNavController()

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
            if (snuttStorage.accessToken.get().isEmpty()) NavigationDestination.Onboard
            else NavigationDestination.Home

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalApiOnProgress provides apiOnProgress,
            LocalApiOnError provides apiOnError,
        ) {

            NavHost(navController = navController, startDestination = startDestination) {

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
