package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TimeTableRoute(
    viewModel: HomeDrawerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeDrawerScreen(
        modifier = Modifier,
        uiState = uiState,
        onToggleExpand = viewModel::toggleCourseBookDrawerItem
    )
}