package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.TableTheme

@Composable
fun TableTheme.displayName(): String = when (this) {
    is BuiltInTheme -> when (this) {
        BuiltInTheme.SNUTT -> stringResource(R.string.home_select_theme_snutt)
        BuiltInTheme.MODERN -> stringResource(R.string.home_select_theme_modern)
        BuiltInTheme.AUTUMN -> stringResource(R.string.home_select_theme_autumn)
        BuiltInTheme.CHERRY -> stringResource(R.string.home_select_theme_pink)
        BuiltInTheme.ICE -> stringResource(R.string.home_select_theme_ice)
        BuiltInTheme.GRASS -> stringResource(R.string.home_select_theme_grass)
        else -> name
    }
    is CustomTheme -> name
}
