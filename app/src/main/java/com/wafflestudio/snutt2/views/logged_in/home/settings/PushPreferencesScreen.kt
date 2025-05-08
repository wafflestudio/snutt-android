package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domainmodel.PushPreferenceType
import com.wafflestudio.snutt2.domainmodel.PushPreferences
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PushPreferencesRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: PushPreferencesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.pushPreferenceUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPushPreferences()

        viewModel.pushPreferencesUiEvent.collectLatest { uiEvent ->
            context.toast(uiEvent)
        }
    }

    PushPreferencesScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        uiState = uiState,
        toggleUiState = viewModel::togglePushPreferences,
    )
}

@Composable
fun PushPreferencesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
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
            onClickNavigateBack = onBackClick,
        )

        when (uiState) {
            is PushPreferencesUiState.Loading -> {
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
            is PushPreferencesUiState.Error -> {
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
            is PushPreferencesUiState.Success -> {
                Column {
                    Margin(height = 10.dp)
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
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PushPreferencesScreenPreview() {
    PushPreferencesScreen(
        onBackClick = {},
        uiState = PushPreferencesUiState.Success(
            PushPreferences(lectureUpdate = false, vacancyNotification = true),
        ),
        toggleUiState = {},
    )
}
