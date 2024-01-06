package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.AddThemeItem
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeConfigViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeItem

@Composable
fun ChangeThemeBottomSheet(
    onPreview: (ThemeDto) -> Unit,
    onApply: () -> Unit,
    onDispose: () -> Unit,
    themeConfigViewModel: ThemeConfigViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val bottomSheet = LocalBottomSheetState.current
    val customThemes by themeConfigViewModel.customThemes.collectAsState()

    LaunchedEffect(Unit) {
        themeConfigViewModel.fetchCustomThemes()
    }

    if (bottomSheet.isVisible) {
        DisposableEffect(LocalLifecycleOwner.current) {
            onDispose { onDispose() }
        }
    }

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(
                text = stringResource(R.string.home_drawer_table_theme_change),
                modifier = Modifier.padding(10.dp),
                style = SNUTTTypography.body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clicks { onApply() }) {
                Text(
                    text = stringResource(R.string.home_select_theme_confirm),
                    modifier = Modifier.padding(10.dp),
                    style = SNUTTTypography.body1,
                )
            }
        }
        LazyRow(
            Modifier
                .padding(16.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                AddThemeItem(
                    onClick = {
                        navController.navigate("${NavigationDestination.CustomThemeDetail}/0")
                    },
                )
            }
            items(
                items = customThemes,
            ) {
                ThemeItem(
                    theme = it,
                    onClick = { onPreview(it) },
                )
            }
            items(
                ThemeDto.builtInThemes,
            ) {
                ThemeItem(
                    theme = it,
                    onClick = { onPreview(it) },
                )
            }
        }
    }
}
