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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.android.ReviewUrlController
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.PagerConstants.TimeTablePage
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

@OptIn(ExperimentalPagerApi::class)
val HomePageController = compositionLocalOf<PagerState> {
    return@compositionLocalOf PagerState(TimeTablePage)
}

val ReviewUrlController = compositionLocalOf<ReviewUrlController> {
    throw RuntimeException("")
}

@AndroidEntryPoint
class RootActivity : BaseActivity() {

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    @Inject
    lateinit var popupState: PopupState

    @Inject
    lateinit var reviewUrlController: ReviewUrlController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        findViewById<ComposeView>(R.id.compose_root).setContent {
            setUpUI()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun setUpUI() {
        val navController = rememberNavController()

        val startDestination =
            if (snuttStorage.accessToken.get()
                    .isEmpty()
            ) NavigationDestination.Onboard else NavigationDestination.Home

        CompositionLocalProvider(
            NavControllerContext provides navController,
            HomePageController provides rememberPagerState()
        ) {

            NavHost(navController = navController, startDestination = startDestination) {

                onboardGraph(navController)

                composable(NavigationDestination.Home) { HomePage() }

                composable(NavigationDestination.Notification) { NotificationPage() }

                composable(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                composable(NavigationDestination.LectureDetail) {
                    /*  FIXME
                    *   if(lecture==null) {  } 과 같이 분기를 나누면, LectureDetail 페이지에서 뒤로가기를 할 때
                    *   이 블록이 다시 실행되면 이때는 lecture == null 이라 빈 페이지가 보인다.
                    *   argument 를 전달할 다른 방법을 찾던지, LectureDetailPage() 에서 null 처리를 그냥 하던지 선택 필요.
                    */
                    val lecture: LectureDto? =
                        navController.previousBackStackEntry?.savedStateHandle?.get<LectureDto>("lecture_dto")
                    CompositionLocalProvider(ReviewUrlController provides reviewUrlController) {
                        LectureDetailPage(lecture)
                    }
                }

                composable(NavigationDestination.LectureColorSelector) {
                    val id = it.arguments?.getString("lecture_id")
                    LectureColorSelectorPage(id = id)
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
