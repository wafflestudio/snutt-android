package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.CheckId

@Composable
fun ResetPasswordPage() {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<FindPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.find_password_title),
            onClickNavigateBack = {
                if (uiState is CheckId) {
                    navController.popBackStack()
                } else {
                    viewModel.goToPreviousStep()
                }
            },
        )
    }
}
