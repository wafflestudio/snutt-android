package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ThemeIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.AddThemeItem
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeConfigViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel

@Composable
fun ChangeThemeBottomSheet(
    onPreview: (ThemeDto) -> Unit,
    onApply: () -> Unit,
    onDispose: () -> Unit,
    themeConfigViewModel: ThemeConfigViewModel = hiltViewModel(),
    timetableViewModel: TimetableViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val bottomSheet = LocalBottomSheetState.current
    val customThemes by themeConfigViewModel.customThemes.collectAsState()
    val builtInThemes by themeConfigViewModel.builtInThemes.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()

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
            .padding(top = 16.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.common_cancel),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clicks { onDispose() },
                style = SNUTTTypography.body1,
            )
            Text(
                text = stringResource(R.string.home_select_theme_confirm),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clicks { onApply() },
                style = SNUTTTypography.body1,
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        LazyRow(
            Modifier
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                AddThemeItem(
                    onClick = {
                        navController.navigate(NavigationDestination.ThemeDetail)
                    },
                )
            }
            items(
                items = customThemes,
            ) {
                ThemeItem(
                    theme = it,
                    onClick = { onPreview(it) },
                    selected = previewTheme?.isCustom == true && previewTheme?.id == it.id,
                )
            }
            items(
                items = builtInThemes,
            ) {
                ThemeItem(
                    theme = it,
                    onClick = { onPreview(it) },
                    selected = previewTheme?.isCustom == false && previewTheme?.code == it.code,
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: ThemeDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Column(
        modifier = modifier.clicks { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(6.dp)),
        ) {
            ThemeIcon(
                theme = theme,
                modifier = Modifier.size(80.dp),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = MaterialTheme.colors.surface.copy(alpha = 0.5f)),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = theme.name,
            modifier = Modifier
                .widthIn(max = 80.dp)
                .then(
                    if (selected) {
                        Modifier.background(
                            color = if (isDarkMode()) SNUTTColors.DarkerGray else SNUTTColors.Gray,
                            shape = CircleShape,
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = SNUTTTypography.body2,
        )
    }
}
