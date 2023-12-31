package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.AddThemeItem
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeDetailPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.theme.ThemeItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangeThemeBottomSheet(
    onLaunch: () -> Unit,
    onPreview: (Int) -> Unit,
    onApply: () -> Unit,
    onDispose: () -> Unit,
) {
    val themeList = listOf(
        stringResource(R.string.home_select_theme_snutt) to painterResource(R.drawable.theme_preview_snutt),
        stringResource(R.string.home_select_theme_modern) to painterResource(R.drawable.theme_preview_modern),
        stringResource(R.string.home_select_theme_autumn) to painterResource(R.drawable.theme_preview_autumn),
        stringResource(R.string.home_select_theme_pink) to painterResource(R.drawable.theme_preview_pink),
        stringResource(R.string.home_select_theme_ice) to painterResource(R.drawable.theme_preview_ice),
        stringResource(R.string.home_select_theme_grass) to painterResource(R.drawable.theme_preview_grass),
    )
    val bottomSheet = LocalBottomSheetState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onLaunch()
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose { onDispose() }
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
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            AddThemeItem(
                onClick = {
                    bottomSheet.setSheetContent {
                        ThemeDetailPage(
                            onClickCancel = {
                            },
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.width(20.dp))
            themeList.forEachIndexed { themeIdx, nameAndIdPair ->
                ThemeItem(
                    name = nameAndIdPair.first,
                    painter = nameAndIdPair.second,
                    modifier = Modifier.clicks { onPreview(themeIdx) },
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}
