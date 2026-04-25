package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.feature.lecturedetail.ColorItem
import com.wafflestudio.snutt2.feature.lecturedetail.ColorPickerDialog
import com.wafflestudio.snutt2.feature.lecturedetail.PickerColorSection
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode

@Composable
fun LectureColorSelectorScreen(
    uiState: LectureColorSelectorUiState,
    onBackPressed: () -> Unit,
    onSelectPalette: (Int) -> Unit,
    onSelectPicker: () -> Unit,
    onOpenFgPicker: () -> Unit,
    onOpenBgPicker: () -> Unit,
    onDismissDialog: () -> Unit,
    onPickFgColor: (Int) -> Unit,
    onPickBgColor: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.lecture_color_selector_page_app_bar_title),
            onClickNavigateBack = onBackPressed,
        )
        Spacer(modifier = Modifier.height(10.dp))

        val isDarkMode = isDarkMode()

        when (uiState) {
            is LectureColorSelectorUiState.CustomThemeMode -> {
                uiState.tableTheme.getColors(isDarkMode).forEachIndexed { idx, color ->
                    ColorItem(
                        foreground = Color(color.foreground),
                        background = Color(color.background),
                        title = "${uiState.tableTheme.name} ${idx + 1}",
                        isSelected = idx == uiState.selection.index,
                        onClick = { onSelectPalette(idx) },
                    )
                }
            }
            is LectureColorSelectorUiState.BuiltInThemeMode -> {
                val selectedPaletteIndex = (uiState.selection as? LectureColorSelectorUiState.ColorSelection.Palette)?.index
                uiState.tableTheme.getColors(isDarkMode).forEachIndexed { idx, color ->
                    ColorItem(
                        foreground = Color(color.foreground),
                        background = Color(color.background),
                        title = "${uiState.tableTheme.name} ${idx + 1}",
                        isSelected = idx == selectedPaletteIndex,
                        onClick = { onSelectPalette(idx) },
                    )
                }

                Column {
                    ColorItem(
                        foreground = Color(uiState.pickerFgColor),
                        background = Color(uiState.pickerBgColor),
                        title = stringResource(R.string.lecture_color_selector_page_custom_color),
                        isSelected = uiState.selection is LectureColorSelectorUiState.ColorSelection.Picker,
                        onClick = onSelectPicker,
                    )
                    PickerColorSection(
                        fgColor = Color(uiState.pickerFgColor),
                        bgColor = Color(uiState.pickerBgColor),
                        onFgPickerClick = onOpenFgPicker,
                        onBgPickerClick = onOpenBgPicker,
                    )
                }
            }
        }
    }

    if (uiState is LectureColorSelectorUiState.BuiltInThemeMode) {
        when (val dialogState = uiState.dialogState) {
            is LectureColorSelectorUiState.DialogState.None -> {}
            is LectureColorSelectorUiState.DialogState.ForegroundPicker -> {
                ColorPickerDialog(
                    initialColor = Color(dialogState.initialColor),
                    onConfirm = { color -> onPickFgColor(color.toArgb()) },
                    onDismiss = onDismissDialog,
                )
            }

            is LectureColorSelectorUiState.DialogState.BackgroundPicker -> {
                ColorPickerDialog(
                    initialColor = Color(dialogState.initialColor),
                    onConfirm = { color -> onPickBgColor(color.toArgb()) },
                    onDismiss = onDismissDialog,
                )
            }
        }
    }
}

private val previewBuiltInPickerColors = LectureColor.Custom.Default

@SnuttPreview
@Composable
private fun LectureColorSelectorScreen_BuiltInTheme_Palette() {
    SnuttPreviewSurface {
        LectureColorSelectorScreen(
            uiState = LectureColorSelectorUiState.BuiltInThemeMode(
                tableTheme = BuiltInTheme.SNUTT,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(2),
                pickerFgColor = previewBuiltInPickerColors.foreground,
                pickerBgColor = previewBuiltInPickerColors.background,
            ),
            onBackPressed = {},
            onSelectPalette = {},
            onSelectPicker = {},
            onOpenFgPicker = {},
            onOpenBgPicker = {},
            onDismissDialog = {},
            onPickFgColor = {},
            onPickBgColor = {},
        )
    }
}

@SnuttPreview
@Composable
private fun LectureColorSelectorScreen_BuiltInTheme_Picker() {
    SnuttPreviewSurface {
        LectureColorSelectorScreen(
            uiState = LectureColorSelectorUiState.BuiltInThemeMode(
                tableTheme = BuiltInTheme.SNUTT,
                selection = LectureColorSelectorUiState.ColorSelection.Picker,
                pickerFgColor = SNUTTColors.White.toArgb(),
                pickerBgColor = 0xFF5965B2.toInt(),
            ),
            onBackPressed = {},
            onSelectPalette = {},
            onSelectPicker = {},
            onOpenFgPicker = {},
            onOpenBgPicker = {},
            onDismissDialog = {},
            onPickFgColor = {},
            onPickBgColor = {},
        )
    }
}

@SnuttPreview
@Composable
private fun LectureColorSelectorScreen_CustomTheme() {
    SnuttPreviewSurface {
        LectureColorSelectorScreen(
            uiState = LectureColorSelectorUiState.CustomThemeMode(
                tableTheme = PreviewData.previewCustomTheme1,
                selection = LectureColorSelectorUiState.ColorSelection.Palette(0),
            ),
            onBackPressed = {},
            onSelectPalette = {},
            onSelectPicker = {},
            onOpenFgPicker = {},
            onOpenBgPicker = {},
            onDismissDialog = {},
            onPickFgColor = {},
            onPickBgColor = {},
        )
    }
}
