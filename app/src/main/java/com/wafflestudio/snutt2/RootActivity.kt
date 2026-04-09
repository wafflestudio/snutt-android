package com.wafflestudio.snutt2

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.wafflestudio.snutt2.logging.compose.LocalAnalyticsLogger
import com.wafflestudio.snutt2.config.RemoteConfig
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import com.wafflestudio.snutt2.navigation.NavigationDestination
import com.wafflestudio.snutt2.navigation.buildRootNavGraph
import com.wafflestudio.snutt2.navigation.getDeepLinkPath
import com.wafflestudio.snutt2.navigation.navigateAsOrigin
import com.wafflestudio.snutt2.ui.theme.LocalThemeState
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.feature.home.HomeItem
import com.wafflestudio.snutt2.domain.RefreshInitialDataUseCase
import com.wafflestudio.snutt2.feature.settings.RootViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    @Inject
    lateinit var refreshInitialDataUseCase: RefreshInitialDataUseCase

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isLoading = true
        splashScreen.setKeepOnScreenCondition { isLoading }

        enableEdgeToEdge()
        super.onCreate(null)

        FirebaseApp.initializeApp(this)
        parseDeeplinkExtra()

        val token = rootViewModel.accessToken.value

        lifecycleScope.launch {
            if (token.isNotEmpty()) {
                refreshInitialDataUseCase()
            }
            isLoading = false
        }
        val initialDeeplinkTab = parseHomePageDeeplink()
        setUpContents(
            if (token.isEmpty()) {
                NavigationDestination.Onboard
            } else {
                NavigationDestination.Home(initialTab = initialDeeplinkTab?.toTabString())
            },
        )
        setWindowAppearance()
        checkNotificationPermission()
        startUpdatingPushToken()
    }

    private fun setUpContents(startDestination: NavigationDestination) {
        setContent {
            val themeMode by rootViewModel.themeMode.collectAsState()
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

        CompositionLocalProvider(
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
                    rootViewModel.accessToken.collect { token ->
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
                    buildRootNavGraph(
                        navController = navController,
                        scheme = applicationContext.getString(R.string.scheme),
                    )
                }
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
            rootViewModel.accessToken.filterNot { it.isEmpty() }.collect {
                rootViewModel.registerPushToken()
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
            rootViewModel.themeMode.collect { themeMode ->
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
