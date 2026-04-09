package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.activity.compose.BackHandler
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
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ColorItem
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ColorPickerDialog
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.CustomColorSection

@Composable
fun LectureColorSelectorScreen(
    uiState: LectureColorSelectorUiState,
    onBackPressed: () -> Unit,
    onSelectPalette: (Int) -> Unit,
    onSelectCustom: () -> Unit,
    onOpenFgPicker: () -> Unit,
    onOpenBgPicker: () -> Unit,
    onDismissDialog: () -> Unit,
    onPickFgColor: (Int) -> Unit,
    onPickBgColor: (Int) -> Unit,
) {
    BackHandler { onBackPressed() }

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

        when (uiState) {
            is LectureColorSelectorUiState.Loading -> {
                // empty
            }

            is LectureColorSelectorUiState.Loaded -> {
                val isDarkMode = isDarkMode()
                val paletteColors = uiState.tableTheme.getColors(isDarkMode)

                if (uiState.tableTheme is CustomTheme) {
                    paletteColors.forEachIndexed { idx, color ->
                        ColorItem(
                            foreground = Color(color.foreground),
                            background = Color(color.background),
                            title = "${uiState.tableTheme.name} ${idx + 1}",
                            isSelected = idx == uiState.selectedIndex,
                            onClick = { onSelectPalette(idx) },
                        )
                    }
                } else {
                    paletteColors.forEachIndexed { idx, color ->
                        ColorItem(
                            foreground = Color(color.foreground),
                            background = Color(color.background),
                            title = "${uiState.tableTheme.name} ${idx + 1}",
                            isSelected = idx == uiState.selectedIndex,
                            onClick = { onSelectPalette(idx) },
                        )
                    }

                    Column {
                        ColorItem(
                            foreground = Color(uiState.customFgColor),
                            background = Color(uiState.customBgColor),
                            title = stringResource(R.string.lecture_color_selector_page_custom_color),
                            isSelected = uiState.selectedIndex == -1,
                            onClick = onSelectCustom,
                        )
                        CustomColorSection(
                            fgColor = Color(uiState.customFgColor),
                            bgColor = Color(uiState.customBgColor),
                            onFgPickerClick = onOpenFgPicker,
                            onBgPickerClick = onOpenBgPicker,
                        )
                    }
                }

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
    }
}
