package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.CheckId
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.EnterFullEmail
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetPasswordPage() {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val keyboardManager = LocalSoftwareKeyboardController.current
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

        when (uiState) {
            is CheckId -> CheckIdStep(
                userId = (uiState as CheckId).userId,
                onSubmit = { userId ->
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            viewModel.checkEmailById(userId)
                            keyboardManager?.hide()
                        }
                    }
                },
            )
            is EnterFullEmail -> EnterFullEmailStep(
                userId = (uiState as EnterFullEmail).userId,
                maskedEmail = (uiState as EnterFullEmail).maskedEmail,
                notMyEmail = viewModel::goToPreviousStep,
                onSubmitFullEmail = { fullEmail ->
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            viewModel.sendFullEmailAndRequestCode(fullEmail)
                            keyboardManager?.hide()
                        }
                    }
                },
            )
        }
    }
}
