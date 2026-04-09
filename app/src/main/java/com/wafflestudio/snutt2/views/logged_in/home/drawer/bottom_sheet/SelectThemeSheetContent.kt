package com.wafflestudio.snutt2.views.logged_in.home.drawer.bottom_sheet

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ThemeIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.components.compose.displayName
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.drawer.HomeDrawerBottomSheetType
import com.wafflestudio.snutt2.feature.theme_config.AddThemeItem

@Composable
fun SelectThemeSheetContent(
    sheetType: HomeDrawerBottomSheetType.SelectTheme,
    onClickPreviewTheme: (TableTheme) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    onClickAddTheme: () -> Unit,
) {
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
                    .clicks { onDismiss() },
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
                    onClick = onClickAddTheme,
                )
            }
            items(
                items = sheetType.customThemes,
            ) { theme ->
                ThemeItem(
                    theme = theme,
                    onClick = { onClickPreviewTheme(theme) },
                    selected = sheetType.selectedPreviewTheme == theme,
                )
            }
            items(
                items = sheetType.builtInThemes,
            ) { theme ->
                ThemeItem(
                    theme = theme,
                    onClick = { onClickPreviewTheme(theme) },
                    selected = sheetType.selectedPreviewTheme == theme,
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: TableTheme,
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
            text = theme.displayName(),
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
