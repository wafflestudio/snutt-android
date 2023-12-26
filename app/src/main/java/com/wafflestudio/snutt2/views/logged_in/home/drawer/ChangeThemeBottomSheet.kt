package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.logged_in.home.settings.ThemeDetailPage
import kotlinx.coroutines.launch

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
            AddThemeItem(bottomSheet = bottomSheet)
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

@Composable
private fun AddThemeItem(
    bottomSheet: BottomSheet,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .clicks {
                bottomSheet.setSheetContent {
                    ThemeDetailPage()
                }
                scope.launch {
                    bottomSheet.show()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = SNUTTColors.VacancyGray, shape = RoundedCornerShape(6.dp)),
        ) {
            AddIcon(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center),
            )
        }
        Spacer(modifier.height(10.dp))
        Text(
            text = stringResource(R.string.theme_new),
            style = SNUTTTypography.body1,
        )
    }
}

@Composable
private fun ThemeItem(
    name: String,
    painter: Painter,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Image(
            painter = painter, contentDescription = "", modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = name, textAlign = TextAlign.Center, style = SNUTTTypography.body1)
        }
    }
}
