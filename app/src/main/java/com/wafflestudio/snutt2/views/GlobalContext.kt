package com.wafflestudio.snutt2.views

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.ModalState
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState

val LocalApiOnError = compositionLocalOf<ApiOnError> {
    throw RuntimeException("")
}

val LocalApiOnProgress = compositionLocalOf<ApiOnProgress> {
    throw RuntimeException("")
}

val LocalDrawerState = compositionLocalOf {
    DrawerState(DrawerValue.Closed)
}

val LocalBottomSheetState = compositionLocalOf<BottomSheet> {
    throw RuntimeException("")
}

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

val LocalReviewWebView = compositionLocalOf<WebViewContainer> {
    throw RuntimeException("")
}

val LocalHomePageController = compositionLocalOf<HomePageController> {
    throw RuntimeException("")
}

val LocalPopupState = compositionLocalOf<PopupState> {
    throw RuntimeException("")
}

val LocalThemeState = compositionLocalOf<ThemeMode> {
    throw RuntimeException("")
}

val LocalModalState = compositionLocalOf<ModalState> {
    throw RuntimeException("")
}

val LocalCompactState = compositionLocalOf<Boolean> {
    throw RuntimeException("")
}

val LocalTableState = compositionLocalOf<TableState> {
    throw RuntimeException("")
}

val LocalRemoteConfig = staticCompositionLocalOf<RemoteConfig> {
    throw RuntimeException("")
}
