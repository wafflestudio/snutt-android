package com.wafflestudio.snutt2.feature.themeconfig

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.config.FeatureFlag
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.BottomSheetDismissEffect
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.util.toast
import kotlinx.coroutines.launch

@Composable
fun ThemeConfigRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (TableTheme) -> Unit,
    onClickAddTheme: () -> Unit,
    viewModel: ThemeConfigViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    BackHandler(enabled = uiState.bottomSheetType != ThemeConfigUiState.BottomSheetType.None) {
        viewModel.onCloseBottomSheet()
    }

    BottomSheetDismissEffect(sheetState, viewModel::onSheetDismissed)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ThemeConfigUiEvent.ShowToast -> context.toast(event.message)
                ThemeConfigUiEvent.OpenBottomSheet -> scope.launch { sheetState.show() }
                ThemeConfigUiEvent.CloseBottomSheet -> scope.launch { sheetState.hide() }
                is ThemeConfigUiEvent.NavigateToDetail -> onNavigateToDetail(event.theme)
            }
        }
    }

    ThemeConfigBottomSheetLayout(
        sheetState = sheetState,
        bottomSheetType = uiState.bottomSheetType,
        onClickDetail = viewModel::onClickDetail,
        onClickApply = viewModel::onClickApply,
        onClickDuplicate = viewModel::onClickDuplicate,
        onClickDelete = viewModel::onClickDelete,
    ) {
        ThemeConfigScreen(
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onClickAddTheme = onClickAddTheme,
            onClickThemeItem = viewModel::onOpenBottomSheet,
            onConfirmDeleteTheme = viewModel::onConfirmDeleteTheme,
            onDismissDialog = viewModel::onDismissDialog,
        )
    }
}

@Composable
private fun ThemeConfigScreen(
    uiState: ThemeConfigUiState,
    onNavigateBack: () -> Unit,
    onClickAddTheme: () -> Unit,
    onClickThemeItem: (TableTheme) -> Unit,
    onConfirmDeleteTheme: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.theme_config_app_bar_title),
            onClickNavigateBack = onNavigateBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState()),
        ) {
            ThemesRow(
                title = stringResource(R.string.theme_config_custom_theme),
                themes = uiState.myCustomThemes,
                onClickItem = onClickThemeItem,
                leadingItem = { AddThemeItem(onClick = onClickAddTheme) },
            )
            if (FeatureFlag.THEME_MARKET.isEnabled && uiState.marketCustomThemes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                ThemesRow(
                    title = stringResource(R.string.theme_config_market_custom_theme),
                    themes = uiState.marketCustomThemes,
                    onClickItem = onClickThemeItem,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            ThemesRow(
                title = stringResource(R.string.theme_config_builtin_theme),
                themes = uiState.builtInThemes,
                onClickItem = onClickThemeItem,
            )
            Spacer(modifier = Modifier.height(25.dp))
            ThemeGuideTexts(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }

    ThemeConfigDialogContent(
        dialogState = uiState.dialogState,
        onConfirm = onConfirmDeleteTheme,
        onDismiss = onDismissDialog,
    )
}

@SnuttPreview
@Composable
private fun ThemeConfigScreen_Default() {
    SnuttPreviewSurface {
        ThemeConfigScreen(
            uiState = ThemeConfigUiState(
                myCustomThemes = listOf(PreviewData.previewCustomTheme1, PreviewData.previewCustomTheme2),
                marketCustomThemes = emptyList(),
                builtInThemes = List(6) { BuiltInTheme.fromCode(it) },
            ),
            onNavigateBack = {},
            onClickAddTheme = {},
            onClickThemeItem = {},
            onConfirmDeleteTheme = {},
            onDismissDialog = {},
        )
    }
}
