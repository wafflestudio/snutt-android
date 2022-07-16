package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
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
            if (snuttStorage.accessToken.get().isNotEmpty()) "onboard" else "home"

        NavHost(navController = navController, startDestination = startDestination) {

            onboardGraph(navController)

            composable("home") { HomePage(navController = navController) }

            composable("notification") { NotificationPage() }

            composable("lectures_of_table") { LecturesOfTablePage() }

            composable("lectures/{lecture_id}") {
                val id = it.arguments?.getString("lecture_id")
                LectureDetailPage(id = id)
            }

            settingComposables()
        }
    }

    private fun NavGraphBuilder.onboardGraph(navController: NavController) {
        navigation(startDestination = "tutorial", route = "onboard") {
            composable("tutorial") {
                TutorialPage(
                    onClickSignIn = { navController.navigate("signIn") },
                    onClickSignUp = { navController.navigate("signUp") }
                )
            }
            composable("signIn") {
                SignInPage(
                    onClickSignIn = { _, _ -> navController.navigateAsOrigin("home") },
                    onClickFacebookSignIn = { navController.navigateAsOrigin("home") }
                )
            }
            composable("signUp") {
                SignUpPage(
                    onClickSignUp = { _, _, _, _ -> navController.navigateAsOrigin("home") },
                    onClickFacebookSignUp = { navController.navigateAsOrigin("home") }
                )
            }
        }
    }

    private fun NavGraphBuilder.settingComposables() {
        composable("appReport") { AppReportPage() }
        composable("serviceInfo") { ServiceInfoPage() }
        composable("teamInfo") { TeamInfoPage() }
        composable("timetableConfig") { TimetableConfigPage() }
        composable("userConfig") { UserConfigPage() }
    }

    private fun NavController.navigateAsOrigin(route: String) {
        navigate(route) {
            popUpTo(this@navigateAsOrigin.graph.findStartDestination().id) {
                saveState = true
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

