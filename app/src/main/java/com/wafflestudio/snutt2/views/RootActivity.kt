package com.wafflestudio.snutt2.views

import NavigationDestination
import android.Manifest
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.google.firebase.FirebaseApp
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.deeplink.InstallInAppDeeplinkExecutor
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.navigation.getDeepLinkPath
import com.wafflestudio.snutt2.react_native.ReactNativeBundleManager
import com.wafflestudio.snutt2.test.TestRoute
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
import com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write.DiaryWriteRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeConfigRoute
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeDetailRoute
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureColorSelectorPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink.TimetableLectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationRoute
import com.wafflestudio.snutt2.views.logged_in.table_lectures.TableLecturesRoute
import com.wafflestudio.snutt2.views.logged_in.thememarket.ThemeMarketRoute
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyPage
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import com.wafflestudio.snutt2.views.logged_out.*
import com.wafflestudio.snutt2.views.logged_out.reset_password.ResetPasswordPage
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

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    private var isInitialRefreshFinished = false

    private val composeRoot by lazy { findViewById<ComposeView>(R.id.compose_root) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        enableEdgeToEdge()
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

    private fun setUpContents(startDestination: NavigationDestination) {
        composeRoot.setContent {
            val themeMode by userViewModel.themeMode.collectAsState()
            CompositionLocalProvider(LocalThemeState provides themeMode) {
                SNUTTTheme {
                    // safeDrawingPadding(): targerSDK 35 대응
                    // SDK 35에서 꽉 찬 화면이 default가 되면서, statusBar와 navigationBar에 맞게 padding을 줘야 한다.
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                    ) {
                        setUpUI(startDestination)
                    }
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

    @Composable
    fun setUpUI(startDestination: NavigationDestination) {
        val navBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
        val bottomSheetNavigator = remember {
            BottomSheetNavigator(
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
            LocalAnalyticsLogger provides analyticsLogger,
        ) {
            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    remoteConfig.noticeConfig.collect {
                        if (it.visible) {
                            navController.navigateAsOrigin(NavigationDestination.ImportantNotice)
                        }
                    }
                }

                // ApiOnError에서 WRONG_USER_TOKEN 시 로그아웃 하고 Onboard로 내비게이션하기 위한 코드.
                // ApiOnError에서 UI 단 접근이 불가능하기 때문에, token.isEmpty()를 트리거로 하여 RootActivity에서 내비게이션한다.
                // 다만 앱 켰을 때 Onboard로 두 번 연속 내비게이션하지 않기 위해 hasRoute를 검사한다.
                // FIXME: 궁극적으로는 ApiOnError를 제거해야 한다.
                lifecycleScope.launch {
                    userViewModel.accessToken.collect { token ->
                        if (token.isEmpty() && navController.currentDestination?.hasRoute(
                                NavigationDestination.Tutorial::class,
                            ) == false
                        ) {
                            navController.navigateAsOrigin(
                                NavigationDestination.Onboard,
                            )
                        }
                    }
                }
            }

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

                    composableRoot<NavigationDestination.Home> { HomePage() }

                    composableAnimated<NavigationDestination.ImportantNotice> { ImportantNoticePage() }

                    composableAnimated<NavigationDestination.Notification> { NotificationRoute() }

                    composableAnimated<NavigationDestination.LecturesOfTable> { TableLecturesRoute() }

                    composableAnimated<NavigationDestination.LectureDetail> {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val referrer = when {
                            navController.previousBackStackEntry?.destination?.hasRoute(
                                NavigationDestination.LecturesOfTable::class,
                            ) == true
                            -> DetailScreenReferrer.LectureList

                            homePageController.homePageState.value == HomeItem.Timetable -> DetailScreenReferrer.Timetable
                            else -> null
                        }
                        val lectureDetailViewModel =
                            hiltViewModel<LectureDetailViewModel>(parentEntry)
                        val searchViewModel = hiltViewModel<SearchViewModel>(parentEntry)
                        val vacancyViewModel = hiltViewModel<VacancyViewModel>(parentEntry)
                        LectureDetailPage(
                            referrer = referrer,
                            vm = lectureDetailViewModel,
                            searchViewModel = searchViewModel,
                            vacancyViewModel = vacancyViewModel,
                        )
                    }

                    composableAnimated<NavigationDestination.TimetableLecture> { backStackEntry ->
                        val homeBackStackEntry = remember(backStackEntry) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val tableId =
                            backStackEntry.toRoute<NavigationDestination.TimetableLecture>().tableId
                        val lectureDetailViewModel =
                            hiltViewModel<LectureDetailViewModel>(homeBackStackEntry)
                        val tableListViewModel =
                            hiltViewModel<TableListViewModel>(homeBackStackEntry)
                        TimetableLectureDetailPage(
                            tableId,
                            lectureDetailViewModel,
                            tableListViewModel,
                        )
                    }

                    composableAnimated<NavigationDestination.LectureColorSelector> {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(NavigationDestination.Home)
                        }
                        val lectureDetailViewModel =
                            hiltViewModel<LectureDetailViewModel>(parentEntry)
                        LectureColorSelectorPage(lectureDetailViewModel)
                    }

                    bottomSheet<NavigationDestination.ThemeDetail> {
                        ThemeDetailRoute(
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }

                    settingComposables(navController, homePageController)
                }
            }
        }
    }

    private fun NavGraphBuilder.onboardGraph() {
        navigation<NavigationDestination.Onboard>(
            startDestination = NavigationDestination.Tutorial,
        ) {
            composableRoot<NavigationDestination.Tutorial> {
                TutorialPage()
            }
            composableAnimated<NavigationDestination.SignIn> {
                SignInPage()
            }
            composableAnimated<NavigationDestination.SignUp> {
                SignUpPage()
            }

            composableAnimated<NavigationDestination.FindId> {
                FindIdPage()
            }

            composableAnimated<NavigationDestination.FindPassword> {
                ResetPasswordPage()
            }

            composableAnimated<NavigationDestination.EmailVerification> {
                EmailVerificationPage()
            }
        }
    }

    private inline fun <reified T : NavigationDestination> NavGraphBuilder.composableAnimated(
        noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
    ) {
        composable<T>(
            deepLinks = listOfNotNull(
                getDeepLinkPath<T>()?.let { deepLinkPath ->
                    // uri pattern 생성 규칙: https://developer.android.com/reference/kotlin/androidx/navigation/NavDeepLink.Builder?_gl=1*uaoct7*_up*MQ..*_ga*MjExMzE2MzgxOS4xNzQzMjQ2NzMw*_ga_6HH9YJMN9M*MTc0MzI0NjczMC4xLjAuMTc0MzI0NjczMC4wLjAuMzkxMTIxMTgx#setUriPattern(kotlin.String,kotlin.collections.Map)
                    // NavigationDestination에 선언된 argument들을 조합해서 위 규칙에 따라 uri pattern 을 생성함.
                    // 푸시 알림의 url_scheme을 uri pattern에 맞게 쏘면 해당 화면으로 잘 랜딩됨.
                    navDeepLink<T>(basePath = "${applicationContext.getString(R.string.scheme)}$deepLinkPath")
                },
            ),
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

    private inline fun <reified T : NavigationDestination> NavGraphBuilder.composableRoot(
        noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
    ) {
        composable<T>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut(targetAlpha = 0.0f) },
            popExitTransition = { fadeOut() },
            popEnterTransition = { fadeIn(initialAlpha = 0.0f) },
            content = content,
        )
    }

    private fun NavGraphBuilder.settingComposables(
        navController: NavController,
        homePageController: HomePageController,
    ) {
        composableAnimated<NavigationDestination.AppReport> { AppReportPage() }
        composableAnimated<NavigationDestination.OpenLicenses> { OpenSourceLicensePage() }

        composableAnimated<NavigationDestination.LicenseDetail> { backStackEntry ->
            val licenseName =
                backStackEntry.toRoute<NavigationDestination.LicenseDetail>().licenseName
            LicenseDetailPage(licenseName)
        }

        composableAnimated<NavigationDestination.ServiceInfo> { ServiceInfoPage() }
        composableAnimated<NavigationDestination.TeamInfo> { TeamInfoPage() }
        composableAnimated<NavigationDestination.TimeTableConfig> {
            TimetableConfigRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.TimeTableConfig::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = {
                    navController.navigateAsOrigin(NavigationDestination.Onboard)
                },
            )
        }
        composableAnimated<NavigationDestination.UserConfig> {
            UserConfigRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.UserConfig::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = {
                    navController.navigateAsOrigin(NavigationDestination.Onboard)
                },
                onNavigateChangeNickname = {
                    navController.navigate(NavigationDestination.ChangeNickname)
                },
                onNavigateSocialLink = {
                    navController.navigate(NavigationDestination.SocialLink)
                },
            )
        }
        composableAnimated<NavigationDestination.ChangeNickname> { ChangeNicknamePage() }
        composableAnimated<NavigationDestination.SocialLink> { SocialLinkPage() }
        composableAnimated<NavigationDestination.PersonalInformationPolicy> { PersonalInformationPolicyPage() }
        composableAnimated<NavigationDestination.ThemeModeSelect> { ColorModeSelectPage() }
        if (BuildConfig.DEBUG) {
            composableAnimated<NavigationDestination.LectureDiaryWrite> { entry ->
                DiaryWriteRoute(
                    onNavigateBack = {
                        if (navController.currentDestination?.hasRoute(NavigationDestination.LectureDiaryWrite::class) == true) {
                            navController.popBackStack()
                        }
                    },
                    onNavigateOnboard = {
                        navController.navigateAsOrigin(NavigationDestination.Onboard)
                    },
                    onNavigateHome = { navController.navigateAsOrigin(NavigationDestination.Home) },
                    onNavigateReview = {},
                )
            }
        }
        composableAnimated<NavigationDestination.VacancyNotification> {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(NavigationDestination.Home)
            }
            val vacancyViewModel = hiltViewModel<VacancyViewModel>(parentEntry)
            VacancyPage(vacancyViewModel)
        }
        composableAnimated<NavigationDestination.PushPreferences> {
            PushPreferencesRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.PushPreferences::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            )
        }
        composableAnimated<NavigationDestination.LectureReminder> {
            LectureReminderRoute(
                onNavigateBack = {
                    if (navController.currentDestination?.hasRoute(NavigationDestination.LectureReminder::class) == true) {
                        navController.popBackStack()
                    }
                },
                onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
            )
        }
        composableAnimated<NavigationDestination.ThemeMarket> {
            ThemeMarketRoute(
                onBackClick = { navController.popBackStack() },
            )
        }
        composableAnimated<NavigationDestination.ThemeConfig> {
            ThemeConfigRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { theme ->
                    when (theme) {
                        is CustomTheme -> navController.navigate(
                            NavigationDestination.ThemeDetail(themeId = theme.id),
                        )

                        is BuiltInTheme -> navController.navigate(
                            NavigationDestination.ThemeDetail(theme = theme.code),
                        )
                    }
                },
                onClickAddTheme = {
                    navController.navigate(NavigationDestination.ThemeDetail())
                },
            )
        }
        if (BuildConfig.DEBUG) composableAnimated<NavigationDestination.NetworkLog> { NetworkLogPage() }

        if (BuildConfig.DEBUG) {
            composableAnimated<NavigationDestination.Test> {
                TestRoute(
                    onNavigateBack = {
                        if (navController.currentDestination?.hasRoute(NavigationDestination.Test::class) == true) {
                            navController.popBackStack()
                        }
                    },
                    onNavigateOnboard = { navController.navigateAsOrigin(NavigationDestination.Onboard) },
                )
            }
        }
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

    // NOTE(@JuTaK): 푸시 알림에 담긴 딥링크 정보는 intent.extra 에 url_scheme 이라는 key 의 value 로 들어 있다.
    // 이를 Jetpack Navigation 이 딥링크로 인식하고 navigate 할 수 있도록 intent.data 로 넣어준다.
    private fun parseDeeplinkExtra() {
        intent.extras?.getString(URL_SCHEME)?.let {
            intent.data = Uri.parse(it)
        }
    }

    // NOTE(@JuTaK): intent 에 담긴 초기 정보로 최초 랜딩할 탭을 결정한다.
    private fun parseHomePageDeeplink(): HomeItem? {
        // 예시: snutt://friends
        val regex = Regex("^${applicationContext.getString(R.string.scheme)}(.+)$")
        when (regex.find(intent.data.toString())?.groupValues?.get(1)) {
            getDeepLinkPath<NavigationDestination.Friends>() -> return HomeItem.Friends
        }

        // 예시: kakao12345://kakaolink?type=add-friend-kakao
        val type = intent.data?.getQueryParameter("type")
        when (type) {
            "add-friend-kakao" -> {
                return HomeItem.Friends
            }
        }

        return null
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
                window.apply {
                    setBackgroundDrawableResource(if (isDarkMode) R.color.black_dark else R.color.white)
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

fun <T : NavigationDestination> NavController.navigateAsOrigin(route: T) {
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
