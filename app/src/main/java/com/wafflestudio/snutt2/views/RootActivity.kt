package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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
import com.wafflestudio.snutt2.views.NavigationDestination.appReport
import com.wafflestudio.snutt2.views.NavigationDestination.home
import com.wafflestudio.snutt2.views.NavigationDestination.lectureColorSelector
import com.wafflestudio.snutt2.views.NavigationDestination.lectureDetail
import com.wafflestudio.snutt2.views.NavigationDestination.lecturesOfTable
import com.wafflestudio.snutt2.views.NavigationDestination.notification
import com.wafflestudio.snutt2.views.NavigationDestination.onboard
import com.wafflestudio.snutt2.views.NavigationDestination.serviceInfo
import com.wafflestudio.snutt2.views.NavigationDestination.signIn
import com.wafflestudio.snutt2.views.NavigationDestination.signUp
import com.wafflestudio.snutt2.views.NavigationDestination.teamInfo
import com.wafflestudio.snutt2.views.NavigationDestination.timetableConfig
import com.wafflestudio.snutt2.views.NavigationDestination.tutorial
import com.wafflestudio.snutt2.views.NavigationDestination.userConfig
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_out.SignInPage
import com.wafflestudio.snutt2.views.logged_out.SignUpPage
import com.wafflestudio.snutt2.views.logged_out.TutorialPage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val NavControllerContext = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

@AndroidEntryPoint
class RootActivity : BaseActivity() {

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    @Inject
    lateinit var popupState: PopupState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        findViewById<ComposeView>(R.id.compose_root).setContent {
            setUpUI()
        }
    }

    @Composable
    fun setUpUI() {
        val navController = rememberNavController()

        val startDestination =
            if (snuttStorage.accessToken.get().isEmpty()) onboard else home

        CompositionLocalProvider(NavControllerContext provides navController) {

            NavHost(navController = navController, startDestination = startDestination) {

                onboardGraph(navController)

                composable(home) { HomePage() }

                composable(notification) { NotificationPage() }

                composable(lecturesOfTable) { LecturesOfTablePage() }

                composable(lectureDetail) {
                    val id = it.arguments?.getString("lecture_id")
                    LectureDetailPage(id = id)
                }

                composable(lectureColorSelector) {
                    val id = it.arguments?.getString("lecture_id")
                    LectureColorSelectorPage(id = id)
                }

                settingComposable()
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph(navController: NavController) {
        navigation(startDestination = tutorial, route = onboard) {
            composable(tutorial) {
                TutorialPage()
            }
            composable(signIn) {
                SignInPage()
            }
            composable(signUp) {
                SignUpPage()
            }
        }
    }

    private fun NavGraphBuilder.settingComposable() {
        composable(appReport) { AppReportPage() }
        composable(serviceInfo) { ServiceInfoPage() }
        composable(teamInfo) { TeamInfoPage() }
        composable(timetableConfig) { TimetableConfigPage() }
        composable(userConfig) { UserConfigPage() }
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
