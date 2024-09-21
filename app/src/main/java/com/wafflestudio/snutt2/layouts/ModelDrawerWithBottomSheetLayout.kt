package com.wafflestudio.snutt2.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.drawer.HomeDrawer

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalDrawerWithBottomSheetLayout(
    sheetShape: RoundedCornerShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
    drawerContent: @Composable ColumnScope.() -> Unit = { HomeDrawer() },
    drawerState: DrawerState,
    sheetGesturesEnabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheet = LocalBottomSheetState.current
    val pageController = LocalHomePageController.current

    ModalBottomSheetLayout(
        sheetContent = bottomSheet.content,
        sheetState = bottomSheet.state,
        sheetShape = sheetShape,
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = sheetGesturesEnabled,
//        onDismissScrim = {
//            scope.launch { bottomSheet.hide() }
//        }
    ) {
        ModalDrawer(
            drawerContent = drawerContent,
            drawerState = drawerState,
            gesturesEnabled = (pageController.homePageState.value == HomeItem.Timetable) && bottomSheet.isVisible.not(),
            scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                content()
            }
        }
    }
}
