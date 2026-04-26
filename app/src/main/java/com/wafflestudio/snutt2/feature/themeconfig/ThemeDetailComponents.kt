package com.wafflestudio.snutt2.feature.themeconfig

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.ui.components.compose.ColorBox
import com.wafflestudio.snutt2.ui.components.compose.ColorCircle
import com.wafflestudio.snutt2.ui.components.compose.ColorPicker
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.theme.onSurfaceVariant

@Composable
internal fun ThemeColorRow(
    index: Int,
    isEditable: Boolean,
    color: ThemeColor,
    isExpanded: Boolean,
    isDuplicateEnabled: Boolean,
    isRemoveEnabled: Boolean,
    onToggleColorExpanded: (Int) -> Unit,
    onDuplicateColor: (Int) -> Unit,
    onRemoveColor: (Int) -> Unit,
    onShowFgColorPicker: (index: Int, currentFg: Int, currentBg: Int) -> Unit,
    onShowBgColorPicker: (index: Int, currentFg: Int, currentBg: Int) -> Unit,
) {
    val state = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    AnimatedVisibility(
        visibleState = state,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column {
            ThemeDetailRow(
                title = stringResource(R.string.theme_detail_color_item, index + 1),
                titleColor = if (isEditable) {
                    MaterialTheme.colors.onSurfaceVariant
                } else {
                    MaterialTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
                },
                modifier = Modifier.clicks {
                    if (isEditable) onToggleColorExpanded(index)
                },
                actions = {
                    if (isEditable) {
                        SnuttIcon(
                            R.drawable.ic_duplicate,
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (isDuplicateEnabled) onDuplicateColor(index)
                                },
                            colorFilter = ColorFilter.tint(
                                (if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray40).copy(
                                    alpha = if (isDuplicateEnabled) 1f else 0.3f,
                                ),
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SnuttIcon(
                            R.drawable.ic_close,
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (isRemoveEnabled) onRemoveColor(index)
                                },
                            colorFilter = ColorFilter.tint(
                                (if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray40).copy(
                                    alpha = if (isRemoveEnabled) 1f else 0.3f,
                                ),
                            ),
                        )
                    }
                },
            ) {
                ColorBox(
                    foreground = Color(color.foreground),
                    background = Color(color.background),
                )
            }
            AnimatedVisibility(
                visible = isExpanded && isEditable,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                ColorEditItem(
                    fgColor = Color(color.foreground),
                    bgColor = Color(color.background),
                    onShowFgPicker = { onShowFgColorPicker(index, color.foreground, color.background) },
                    onShowBgPicker = { onShowBgColorPicker(index, color.foreground, color.background) },
                )
            }
            Divider(thickness = 0.5.dp, color = MaterialTheme.colors.background)
        }
    }
}

@Composable
internal fun ThemeDetailRow(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colors.onSurfaceVariant,
    actions: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.width(72.dp),
            style = SNUTTTypography.body2.copy(color = titleColor),
        )
        content()
        Spacer(modifier = Modifier.weight(1f))
        actions()
    }
}

@Composable
private fun ColorEditItem(
    fgColor: Color,
    bgColor: Color,
    onShowFgPicker: () -> Unit,
    onShowBgPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface),
    ) {
        Spacer(modifier = Modifier.width(92.dp))
        Column(modifier = Modifier.padding(top = 5.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        .clicks { onShowFgPicker() },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
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
                        .clicks { onShowBgPicker() },
                )
            }
        }
    }
}

@Composable
internal fun ColorPickerDialog(
    initialColor: Color,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableStateOf(initialColor) }

    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(currentColor.toArgb()) },
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

// region Previews

@SnuttPreview
@Composable
private fun ThemeColorRow_EditableExpanded() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
            val editableColor = PreviewData.previewEditingThemeCustom.colors[0]
            ThemeColorRow(
                index = 0,
                isEditable = true,
                color = editableColor.item,
                isExpanded = true,
                isDuplicateEnabled = true,
                isRemoveEnabled = true,
                onToggleColorExpanded = {},
                onDuplicateColor = {},
                onRemoveColor = {},
                onShowFgColorPicker = { _, _, _ -> },
                onShowBgColorPicker = { _, _, _ -> },
            )
        }
    }
}

@SnuttPreview
@Composable
private fun ThemeColorRow_Readonly() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
            val readonlyColor = PreviewData.previewEditingThemeBuiltIn.colors[0]
            ThemeColorRow(
                index = 0,
                isEditable = false,
                color = readonlyColor.item,
                isExpanded = false,
                isDuplicateEnabled = false,
                isRemoveEnabled = false,
                onToggleColorExpanded = {},
                onDuplicateColor = {},
                onRemoveColor = {},
                onShowFgColorPicker = { _, _, _ -> },
                onShowBgColorPicker = { _, _, _ -> },
            )
        }
    }
}

// endregion
