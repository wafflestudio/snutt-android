package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalDrawerWithBottomSheetLayout(
    bottomSheetContent: @Composable ColumnScope.() -> Unit,
    sheetState: ModalBottomSheetState,
    sheetShape: RoundedCornerShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
    drawerContent: @Composable ColumnScope.() -> Unit,
    drawerState: DrawerState,
    gesturesEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheetLayout(
        sheetContent = bottomSheetContent,
        sheetState = sheetState,
        sheetShape = sheetShape,
    ) {
        ModalDrawer(
            drawerContent = drawerContent,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}
