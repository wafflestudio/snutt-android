package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domain.model.PushPreferenceType
import com.wafflestudio.snutt2.domain.model.PushPreferences
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun PushPreferencesRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: PushPreferencesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.pushPreferenceUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPushPreferences()
    }

    LaunchedEffect(Unit) {
        viewModel.pushPreferencesUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is PushPreferencesUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is PushPreferencesUiEvent.NavigateToOnboard -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    PushPreferencesScreen(
        modifier = modifier,
        onClickBack = onNavigateBack,
        uiState = uiState,
        toggleUiState = viewModel::togglePushPreferences,
    )
}

@Composable
fun PushPreferencesScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    uiState: PushPreferencesUiState,
    toggleUiState: (PushPreferenceType) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_push_preferences_title),
            onClickNavigateBack = onClickBack,
        )

        when (uiState) {
            is PushPreferencesUiState.Loading -> PushPreferencesLoading()
            is PushPreferencesUiState.Error -> PushPreferencesError()
            is PushPreferencesUiState.Success -> {
                Column {
                    Spacer(Modifier.height(10.dp))
                    SettingItem(
                        title = stringResource(R.string.settings_push_preferences_lecture_update),
                        hasNextPage = false,
                        onClick = {
                            toggleUiState(PushPreferenceType.LECTURE_UPDATE)
                        },
                    ) {
                        PoorSwitch(state = uiState.pushPreferences.lectureUpdate)
                    }
                    SettingItem(
                        title = stringResource(R.string.settings_push_preferences_vacancy),
                        hasNextPage = false,
                        onClick = {
                            toggleUiState(PushPreferenceType.VACANCY_NOTIFICATION)
                        },
                    ) {
                        PoorSwitch(state = uiState.pushPreferences.vacancyNotification)
                    }
                    SettingItem(
                        title = stringResource(R.string.settings_push_preferences_lecture_diary),
                        hasNextPage = false,
                        onClick = {
                            toggleUiState(PushPreferenceType.DIARY)
                        },
                    ) {
                        PoorSwitch(state = uiState.pushPreferences.lectureDiary)
                    }
                }
            }
        }
    }
}

@Composable
fun PushPreferencesLoading() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PushPreferencesError() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.error_unknown),
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PushPreferencesLoadingPreview() {
    PushPreferencesScreen(
        onClickBack = {},
        uiState = PushPreferencesUiState.Loading,
        toggleUiState = {},
    )
}

@Composable
@Preview(showBackground = true)
fun PushPreferencesErrorPreview() {
    PushPreferencesScreen(
        onClickBack = {},
        uiState = PushPreferencesUiState.Error,
        toggleUiState = {},
    )
}

@Composable
@Preview(showBackground = true)
fun PushPreferencesSuccessPreview() {
    PushPreferencesScreen(
        onClickBack = {},
        uiState = PushPreferencesUiState.Success(
            PushPreferences(lectureUpdate = false, vacancyNotification = true, lectureDiary = true),
        ),
        toggleUiState = {},
    )
}
