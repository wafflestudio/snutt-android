package com.wafflestudio.snutt2.views

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.wafflestudio.snutt2.components.compose.ModalState
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState

val LocalApiOnError = compositionLocalOf<ApiOnError> {
    throw RuntimeException("")
}

val LocalApiOnProgress = compositionLocalOf<ApiOnProgress> {
    throw RuntimeException("")
}

val LocalDrawerState = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}

@OptIn(ExperimentalMaterialApi::class)
val LocalBottomSheetState = compositionLocalOf<ModalBottomSheetState> {
    throw RuntimeException("")
}

val LocalBottomSheetContentSetter =
    compositionLocalOf<(@Composable ColumnScope.() -> Unit) -> Unit> {
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
