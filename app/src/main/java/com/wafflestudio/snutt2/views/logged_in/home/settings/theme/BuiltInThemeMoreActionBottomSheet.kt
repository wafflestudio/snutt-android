package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.MoreActionItem
import com.wafflestudio.snutt2.components.compose.PaletteIcon
import com.wafflestudio.snutt2.components.compose.PinIcon
import com.wafflestudio.snutt2.components.compose.PinOffIcon
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
fun BuiltInThemeMoreActionBottomSheet(
    themeCode: Int,
    isThemeDefault: Boolean,
    onClickDetail: () -> Unit,
    onClickSetDefault: () -> Unit,
    onClickUnsetDefault: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = {
                PaletteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_view),
            onClick = { onClickDetail() },
        )
        if (isThemeDefault) {
            MoreActionItem(
                icon = {
                    PinOffIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(
                            if (themeCode == 0) {
                                if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2
                            } else {
                                MaterialTheme.colors.onSurface
                            },
                        ),
                    )
                },
                text = stringResource(R.string.custom_theme_action_unset_default),
                enabled = themeCode != 0,
                onClick = { onClickUnsetDefault() },
            )
        } else {
            MoreActionItem(
                icon = {
                    PinIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                    )
                },
                text = stringResource(R.string.custom_theme_action_set_default),
                onClick = { onClickSetDefault() },
            )
        }
    }
}
