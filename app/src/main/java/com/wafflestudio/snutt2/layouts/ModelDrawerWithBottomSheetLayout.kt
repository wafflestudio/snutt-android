package com.wafflestudio.snutt2.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.logged_in.home.drawer.HomeDrawer
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalDrawerWithBottomSheetLayout(
    sheetShape: RoundedCornerShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
    drawerContent: @Composable ColumnScope.() -> Unit = { HomeDrawer() },
    drawerState: DrawerState,
    content: @Composable ColumnScope.() -> Unit
) {
    val bottomSheet = LocalBottomSheetState.current
    val pageController = LocalHomePageController.current

    ModalBottomSheetLayout(
        sheetContent = bottomSheet.content,
        sheetState = bottomSheet.state,
        sheetShape = sheetShape,
    ) {
        ModalDrawer(
            drawerContent = drawerContent,
            drawerState = drawerState,
            gesturesEnabled = (pageController.homePageState.value == HomeItem.Timetable) && bottomSheet.isVisible.not(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}
