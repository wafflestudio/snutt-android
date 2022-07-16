package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
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

        NavHost(navController = navController, startDestination = "tutorial") {
            composable("tutorial") {
                TutorialPage(
                    onClickSignIn = { navController.navigate("sign_in") },
                    onClickSignUp = { navController.navigate("sign_up") }
                )
            }
            composable("sign_in") {
                SignInPage(
                    onClickSignIn = { _, _ -> navController.navigate("home") },
                    onClickFacebookSignIn = { navController.navigate("home") }
                )
            }
            composable("sign_up") {
                SignUpPage(
                    onClickSignUp = { _, _, _, _ -> navController.navigate("home") },
                    onClickFacebookSignUp = { navController.navigate("home") }
                )
            }

            composable("home") {
                HomePage()
            }

            composable("notification") {
                NotificationPage()
            }

            composable("lectures_of_table") {
                LecturesOfTablePage()
            }

            composable("lectures/{lecture_id}") {
                val id = it.arguments?.getString("lecture_id")
                LectureDetailPage(id = id)
            }
        }
    }
}
