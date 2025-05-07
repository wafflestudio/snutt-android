package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin

@Composable
fun PushPreferencesRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    PushPreferencesScreen(
        modifier = modifier,
        onBackClick = onBackClick,
    )
}

@Composable
fun PushPreferencesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
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
        Column {
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_push_preferences_lecture_update),
                hasNextPage = false,
                onClick = {
                },
            ) {
                PoorSwitch(state = false)
            }
            SettingItem(
                title = stringResource(R.string.settings_push_preferences_vacancy),
                hasNextPage = false,
                onClick = {
                },
            ) {
                PoorSwitch(state = false)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PushPreferencesScreenPreview() {
    PushPreferencesScreen(
        onBackClick = {},
    )
}
