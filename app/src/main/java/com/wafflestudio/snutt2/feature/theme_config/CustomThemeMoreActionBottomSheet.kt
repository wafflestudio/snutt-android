package com.wafflestudio.snutt2.feature.theme_config

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.ui.components.compose.MoreActionItem
import com.wafflestudio.snutt2.ui.components.compose.PaletteIcon
import com.wafflestudio.snutt2.ui.components.compose.TimetableIcon
import com.wafflestudio.snutt2.ui.components.compose.TrashIcon
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme

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
                PaletteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_edit),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                TimetableIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_apply),
            onClick = { onClickApply() },
        )
        MoreActionItem(
            icon = {
                DuplicateIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_duplicate),
            onClick = { onClickDuplicate() },
        )
        MoreActionItem(
            icon = {
                TrashIcon(
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
                PaletteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_view),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                TimetableIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_apply),
            onClick = { onClickApply() },
        )
        MoreActionItem(
            icon = {
                TrashIcon(
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
                PaletteIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            },
            text = stringResource(R.string.custom_theme_action_detail_view),
            onClick = { onClickDetail() },
        )
        MoreActionItem(
            icon = {
                TimetableIcon(
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

@Preview(name = "커스텀 테마")
@Composable
private fun MyCustomThemeMoreActionBottomSheetPreview() {
    SNUTTTheme {
        MyCustomThemeMoreActionBottomSheet(
            onClickDetail = {},
            onClickApply = {},
            onClickDuplicate = {},
            onClickDelete = {},
        )
    }
}

@Preview(name = "테마마켓 테마")
@Composable
private fun MarketCustomThemeMoreActionBottomSheetPreview() {
    SNUTTTheme {
        MarketCustomThemeMoreActionBottomSheet(
            onClickDetail = {},
            onClickApply = {},
            onClickDelete = {},
        )
    }
}

@Preview(name = "빌트인 테마")
@Composable
private fun BuiltInThemeClickBottomSheetPreview() {
    SNUTTTheme {
        BuiltInThemeClickBottomSheet(
            onClickDetail = {},
            onClickApply = {},
        )
    }
}

// endregion
