package com.wafflestudio.snutt2.feature.themeconfig

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
import com.wafflestudio.snutt2.ui.components.compose.MoreActionItem
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
fun MyCustomThemeMoreActionBottomSheet(
    onClickDetail: () -> Unit,
    onClickApply: () -> Unit,
    onClickDuplicate: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_palette,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_edit),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_timetable_unselected,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_apply),
            onClick = { onClickApply() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_duplicate,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_duplicate),
            onClick = { onClickDuplicate() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_trash,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_delete),
            onClick = { onClickDelete() },
        )
    }
}

@Composable
fun MarketCustomThemeMoreActionBottomSheet(
    onClickDetail: () -> Unit,
    onClickApply: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_palette,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_view),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_timetable_unselected,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_apply),
            onClick = { onClickApply() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_trash,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_delete),
            onClick = { onClickDelete() },
        )
    }
}

@Composable
fun BuiltInThemeClickBottomSheet(
    onClickDetail: () -> Unit,
    onClickApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_palette,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_view),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                SnuttIcon(
                    R.drawable.ic_timetable_unselected,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_apply),
            onClick = { onClickApply() },
        )
    }
}

// region Previews

@SnuttPreview
@Composable
private fun MyCustomThemeMoreActionBottomSheet_Default() {
    SnuttPreviewSurface {
        MyCustomThemeMoreActionBottomSheet(
            onClickDetail = {},
            onClickApply = {},
            onClickDuplicate = {},
            onClickDelete = {},
        )
    }
}

@SnuttPreview
@Composable
private fun MarketCustomThemeMoreActionBottomSheet_Default() {
    SnuttPreviewSurface {
        MarketCustomThemeMoreActionBottomSheet(
            onClickDetail = {},
            onClickApply = {},
            onClickDelete = {},
        )
    }
}

@SnuttPreview
@Composable
private fun BuiltInThemeClickBottomSheet_Default() {
    SnuttPreviewSurface {
        BuiltInThemeClickBottomSheet(
            onClickDetail = {},
            onClickApply = {},
        )
    }
}

// endregion
