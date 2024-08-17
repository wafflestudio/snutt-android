package com.wafflestudio.snutt2.views

import android.Manifest
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.firebase.FirebaseApp
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.deeplink.InstallInAppDeeplinkExecutor
import com.wafflestudio.snutt2.layouts.bottomsheetnavigation.ModalBottomSheetLayout
import com.wafflestudio.snutt2.layouts.bottomsheetnavigation.bottomSheet
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.react_native.ReactNativeBundleManager
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.HomePage
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.*
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeConfigPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeDetailPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeListViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink.TimetableLectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationPage
import com.wafflestudio.snutt2.views.logged_in.table_lectures.LecturesOfTablePage
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyPage
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
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

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var friendBundleManager: ReactNativeBundleManager

    private var isInitialRefreshFinished = false

    private val composeRoot by lazy { findViewById<ComposeView>(R.id.compose_root) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(null)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_root)
        parseDeeplinkExtra()

        val token = userViewModel.accessToken.value
        lifecycleScope.launch {
            if (token.isNotEmpty()) {
                homeViewModel.refreshData()
            }
            isInitialRefreshFinished = true
        }
        setUpContents(
            if (token.isEmpty()) {
                NavigationDestination.Onboard
            } else {
                NavigationDestination.Home
            },
        )
        setUpSplashScreen(composeRoot)
        setWindowAppearance()
        checkNotificationPermission()
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
            },
        )
    }

    fun isInitialConditionsSatisfied(): Boolean {
        return isInitialRefreshFinished
    }

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun setUpUI(startDestination: String) {
        val navBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
        val bottomSheetNavigator = remember {
            com.wafflestudio.snutt2.layouts.bottomsheetnavigation.BottomSheetNavigator(
                navBottomSheetState,
            )
        }
        val navController = rememberNavController(bottomSheetNavigator)
        val initialHomeTab = remember {
            parseHomePageDeeplink() ?: HomeItem.Timetable
        }
        val homePageController = remember {
            HomePageController(initialHomeTab)
        }
        val compactMode by userViewModel.compactMode.collectAsState()

        val bottomSheet = BottomSheet()
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
            LocalRemoteConfig provides remoteConfig,
            LocalNavBottomSheetState provides navBottomSheetState,
        ) {
            InstallInAppDeeplinkExecutor()
            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    onboardGraph()

                    composableRoot(NavigationDestination.Home) { HomePage() }

                    composable2(NavigationDestination.Notification) { NotificationPage() }

                    composable2(NavigationDestination.LecturesOfTable) { LecturesOfTablePage() }

                    composable2(NavigationDestination.LectureDetail) {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val lectureDetailViewModel =
                            hiltViewModel<LectureDetailViewModel>(parentEntry)
                        val searchViewModel = hiltViewModel<SearchViewModel>(parentEntry)
                        val vacancyViewModel = hiltViewModel<VacancyViewModel>(parentEntry)
                        LectureDetailPage(
                            vm = lectureDetailViewModel,
                            searchViewModel = searchViewModel,
                            vacancyViewModel = vacancyViewModel,
                        )
                    }

                    composable2(
                        route = "${NavigationDestination.TimetableLecture}?tableId={tableId}",
                        arguments = listOf(
                            navArgument("tableId") {
                                type = NavType.StringType
                                nullable = true
                            },
                        ),
                    ) { backStackEntry ->
                        val homeBackStackEntry = remember(backStackEntry) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>(homeBackStackEntry)
                        val tableListViewModel = hiltViewModel<TableListViewModel>(homeBackStackEntry)
                        TimetableLectureDetailPage(lectureDetailViewModel, tableListViewModel)
                    }

                    composable2(NavigationDestination.LectureColorSelector) {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val lectureDetailViewModel =
                            hiltViewModel<LectureDetailViewModel>(parentEntry)
                        LectureColorSelectorPage(lectureDetailViewModel)
                    }

                    bottomSheet(
                        "${NavigationDestination.ThemeDetail}?themeId={themeId}&theme={theme}",
                        arguments = listOf(
                            navArgument("themeId") {
                                type = NavType.StringType
                                defaultValue = ""
                            },
                            navArgument("theme") {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        ),
                    ) { backStackEntry ->
                        val themeDetailViewModel =
                            hiltViewModel<ThemeDetailViewModel>(backStackEntry)
                        ThemeDetailPage(
                            themeDetailViewModel = themeDetailViewModel,
                        )
                    }

                    settingcomposable2(navController)
                }
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph() {
        navigation(
            startDestination = NavigationDestination.Tutorial,
            route = NavigationDestination.Onboard,
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

            composable2(NavigationDestination.EmailVerification) {
                EmailVerificationPage()
            }
        }
    }

    private fun NavGraphBuilder.composable2(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = listOf(
            navDeepLink {
                uriPattern = "${applicationContext.getString(R.string.scheme)}$route"
            },
        ),
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
    ) {
        composable(
            route,
            arguments = arguments,
            deepLinks = deepLinks,
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

    private fun NavGraphBuilder.composableRoot(
        route: String,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
    ) {
        composable(
            route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut(targetAlpha = 0.0f) },
            popExitTransition = { fadeOut() },
            popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
            content = content,
        )
    }

    private fun NavGraphBuilder.settingcomposable2(navController: NavController) {
        composable2(NavigationDestination.AppReport) { AppReportPage() }
        composable2(NavigationDestination.OpenLicenses) { OpenSourceLicensePage() }

        composable2(
            route = "${NavigationDestination.LicenseDetail}?licenseName={licenseName}",
            arguments = listOf(
                navArgument("licenseName") {
                    type = NavType.StringType
                    nullable = true
                },
            ),
        ) { _ ->
            LicenseDetailPage()
        }

        composable2(NavigationDestination.ServiceInfo) { ServiceInfoPage() }
        composable2(NavigationDestination.TeamInfo) { TeamInfoPage() }
        composable2(NavigationDestination.TimeTableConfig) { TimetableConfigPage() }
        composable2(NavigationDestination.UserConfig) { UserConfigPage() }
        composable2(NavigationDestination.ChangeNickname) { ChangeNicknamePage() }
        composable2(NavigationDestination.SocialLink) { SocialLinkPage() }
        composable2(NavigationDestination.PersonalInformationPolicy) { PersonalInformationPolicyPage() }
        composable2(NavigationDestination.ThemeModeSelect) { ColorModeSelectPage() }
        composable2(NavigationDestination.VacancyNotification) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(NavigationDestination.Home)
            }
            val vacancyViewModel = hiltViewModel<VacancyViewModel>(parentEntry)
            VacancyPage(vacancyViewModel)
        }
        composable2(NavigationDestination.ThemeConfig) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(NavigationDestination.Home)
            }
            val themeListViewModel = hiltViewModel<ThemeListViewModel>(parentEntry)
            ThemeConfigPage(
                themeListViewModel = themeListViewModel,
            )
        }
        if (BuildConfig.DEBUG) composable2(NavigationDestination.NetworkLog) { NetworkLogPage() }
    }

    // 안드 13 대응
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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

    private fun parseDeeplinkExtra() {
        intent.extras?.getString(URL_SCHEME)?.let {
            intent.data = Uri.parse(it)
        }
    }

    private fun parseHomePageDeeplink(): HomeItem? {
        val regex = Regex("^${applicationContext.getString(R.string.scheme)}(.+)$")
        return when (regex.find(intent.data.toString())?.groupValues?.get(1)) {
            NavigationDestination.Friends -> HomeItem.Friends
            else -> null
        }
    }

    private fun setWindowAppearance() {
        lifecycleScope.launch {
            userViewModel.themeMode.collect { themeMode ->
                /* <다크모드에서 내비게이션 시 흰색 깜빡이는 이슈 해결>
                 * 내비게이션 시 액티비티 배경색인 흰색(styles.xml에서 android:windowBackground 로 지정된 색)이 잠깐 노출된다.
                 * 원래는 values-night/styles.xml를 통해 다크모드의 색을 지정하지만, 우리는 시스템의 테마와 앱의 테마를
                 * 다르게 설정할 수 있기 때문에 여기서 직접 설정해 준다.
                 */
                val isDarkMode = isDarkMode(this@RootActivity, themeMode)
                val primaryColor = ContextCompat.getColor(this@RootActivity, if (isDarkMode) R.color.black_dark else R.color.white)
                window.apply {
                    setBackgroundDrawableResource(if (isDarkMode) R.color.black_dark else R.color.white)
                    statusBarColor = primaryColor
                    navigationBarColor = primaryColor
                }
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isDarkMode.not()
                    isAppearanceLightNavigationBars = isDarkMode.not()
                }
            }
        }
    }

    companion object {
        const val URL_SCHEME = "url_scheme"
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
    onError: suspend () -> Unit = {},
    loadingIndicatorTitle: String? = null,
    api: suspend () -> Unit,
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
