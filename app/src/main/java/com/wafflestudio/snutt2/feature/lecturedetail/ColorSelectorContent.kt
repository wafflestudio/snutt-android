package com.wafflestudio.snutt2.feature.lecturedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CheckedIcon
import com.wafflestudio.snutt2.ui.components.compose.ColorBox
import com.wafflestudio.snutt2.ui.components.compose.ColorCircle
import com.wafflestudio.snutt2.ui.components.compose.ColorPicker
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.onSurfaceVariant

@Composable
internal fun ColorItem(
    foreground: Color,
    background: Color,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .clicks { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColorBox(foreground = foreground, background = background)
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = SNUTTTypography.body1,
        )
        if (isSelected) {
            CheckedIcon(
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500),
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
internal fun PickerColorSection(
    fgColor: Color,
    bgColor: Color,
    onFgPickerClick: () -> Unit,
    onBgPickerClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface),
    ) {
        Spacer(modifier = Modifier.width(92.dp))
        Column(
            modifier = Modifier.padding(top = 5.dp, bottom = 12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.theme_detail_color_fg),
                    color = MaterialTheme.colors.onSurfaceVariant,
                    style = SNUTTTypography.body2,
                )
                Spacer(modifier = Modifier.width(11.dp))
                ColorCircle(
                    color = fgColor,
                    modifier = Modifier
                        .size(25.dp)
                        .clicks { onFgPickerClick() },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.theme_detail_color_bg),
                    color = MaterialTheme.colors.onSurfaceVariant,
                    style = SNUTTTypography.body2,
                )
                Spacer(modifier = Modifier.width(11.dp))
                ColorCircle(
                    color = bgColor,
                    modifier = Modifier
                        .size(25.dp)
                        .clicks { onBgPickerClick() },
                )
            }
        }
    }
}

@Composable
internal fun ColorPickerDialog(
    initialColor: Color,
    onConfirm: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableStateOf(initialColor) }

    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(currentColor) },
        title = stringResource(R.string.color_picker_dialog_title),
        positiveButtonText = stringResource(R.string.common_ok),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        ColorPicker(
            initialColor = initialColor,
            onColorChanged = { currentColor = it },
        )
    }
}

@SnuttPreview
@Composable
private fun ColorItem_Selected() {
    SnuttPreviewSurface {
        ColorItem(
            foreground = Color.White,
            background = Color(0xFFE54459),
            title = "색상 1",
            isSelected = true,
            onClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun PickerColorSection_Default() {
    SnuttPreviewSurface {
        PickerColorSection(
            fgColor = Color.White,
            bgColor = Color(0xFF5965B2),
            onFgPickerClick = {},
            onBgPickerClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ColorPickerDialog_Default() {
    SnuttPreviewSurface {
        ColorPickerDialog(
            initialColor = Color(0xFF5965B2),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
