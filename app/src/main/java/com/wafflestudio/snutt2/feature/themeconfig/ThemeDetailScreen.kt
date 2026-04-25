package com.wafflestudio.snutt2.feature.themeconfig

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.feature.home.timetable.TimeTable
import com.wafflestudio.snutt2.feature.settings.SettingColumn
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.theme.onSurfaceVariant
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun ThemeDetailRoute(
    onNavigateBack: () -> Unit,
    themeDetailViewModel: ThemeDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by themeDetailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        themeDetailViewModel.uiEvent.collect { event ->
            when (event) {
                is ThemeDetailUiEvent.ShowToast -> context.toast(event.message)
                ThemeDetailUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    ThemeDetailScreen(
        uiState = uiState,
        onClickBack = themeDetailViewModel::onClickBack,
        onSaveTheme = themeDetailViewModel::onSaveTheme,
        onUpdateName = themeDetailViewModel::updateName,
        onToggleColorExpanded = themeDetailViewModel::toggleColorExpanded,
        onDuplicateColor = themeDetailViewModel::duplicateColor,
        onRemoveColor = themeDetailViewModel::removeColor,
        onUpdateColor = { index, fgColor, bgColor ->
            themeDetailViewModel.updateColor(index, fgColor.toArgb(), bgColor.toArgb())
        },
        onAddColor = themeDetailViewModel::addColor,
        onConfirmCancelEdit = themeDetailViewModel::onConfirmCancelEdit,
        onDismissCancelEdit = themeDetailViewModel::onDismissCancelEdit,
        onConfirmApplyToTable = themeDetailViewModel::onConfirmApplyToTable,
        onDismissApplyToTable = themeDetailViewModel::onDismissApplyToTable,
    )
}

@Composable
fun ThemeDetailScreen(
    uiState: ThemeDetailUiState,
    onClickBack: () -> Unit,
    onSaveTheme: () -> Unit,
    onUpdateName: (String) -> Unit,
    onToggleColorExpanded: (Int) -> Unit,
    onDuplicateColor: (Int) -> Unit,
    onRemoveColor: (Int) -> Unit,
    onUpdateColor: (Int, foreground: Color, background: Color) -> Unit,
    onAddColor: () -> Unit,
    onConfirmCancelEdit: () -> Unit,
    onDismissCancelEdit: () -> Unit,
    onConfirmApplyToTable: () -> Unit,
    onDismissApplyToTable: () -> Unit,
) {
    when (uiState) {
        is ThemeDetailUiState.Success -> {
            val editingTheme = uiState.editingTheme
            val containerSize = LocalWindowInfo.current.containerSize
            val containerWidthDp = with(LocalDensity.current) { containerSize.width.toDp() }
            val containerHeightDp = with(LocalDensity.current) { containerSize.height.toDp() }

            BackHandler { onClickBack() }

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth()
                    .logImpression(
                        when {
                            editingTheme.isNew -> AnalyticsScreen.ThemeCustomNew
                            editingTheme.isFromMarket -> AnalyticsScreen.ThemeDownloaded
                            editingTheme.isCustomTheme -> AnalyticsScreen.ThemeCustomEdit
                            else -> AnalyticsScreen.ThemeBasicDetail
                        },
                    ),
            ) {
                CenteredTopBar(
                    title = {
                        Text(
                            text = stringResource(
                                if (editingTheme.isCustomTheme) {
                                    R.string.theme_detail_app_bar_title_custom
                                } else {
                                    R.string.theme_detail_app_bar_title_builtin
                                },
                            ),
                            style = SNUTTTypography.h3,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        Text(
                            text = stringResource(R.string.common_cancel),
                            style = SNUTTTypography.body1,
                            modifier = Modifier.clicks { onClickBack() },
                        )
                    },
                    actions = {
                        Text(
                            text = stringResource(R.string.common_save),
                            style = SNUTTTypography.body1,
                            modifier = Modifier.clicks { onSaveTheme() },
                        )
                    },
                )
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    ThemeDetailRow(
                        title = stringResource(R.string.theme_detail_theme_name),
                        titleColor = MaterialTheme.colors.onSurfaceVariant.copy(
                            alpha = if (editingTheme.isEditable) 1f else 0.5f,
                        ),
                    ) {
                        EditText(
                            value = editingTheme.name,
                            onValueChange = onUpdateName,
                            enabled = editingTheme.isEditable,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            underlineEnabled = false,
                            textStyle = SNUTTTypography.body1.copy(
                                color = if (editingTheme.isEditable) {
                                    MaterialTheme.colors.onSurface
                                } else {
                                    MaterialTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                            ),
                        )
                    }
                    SettingColumn(
                        title = stringResource(R.string.theme_detail_theme_colors),
                    ) {
                        editingTheme.getDisplayColors(isDarkMode()).forEachIndexed { idx, colorWithExpanded ->
                            ThemeColorRow(
                                index = idx,
                                isEditable = editingTheme.isEditable,
                                color = colorWithExpanded.item,
                                isExpanded = colorWithExpanded.state,
                                isDuplicateEnabled = editingTheme.canDuplicateColor,
                                isRemoveEnabled = editingTheme.canRemoveColor,
                                onToggleColorExpanded = onToggleColorExpanded,
                                onDuplicateColor = onDuplicateColor,
                                onRemoveColor = onRemoveColor,
                                onUpdateColor = onUpdateColor,
                            )
                        }
                        AnimatedVisibility(editingTheme.canAddColor) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clicks { onAddColor() },
                            ) {
                                Text(
                                    text = stringResource(R.string.theme_detail_add_color),
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colors.onBackground,
                                )
                            }
                        }
                    }
                    SettingColumn(
                        title = stringResource(R.string.theme_detail_preview),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colors.surface)
                                .padding(15.dp)
                                .size(
                                    containerWidthDp * 0.8f,
                                    containerHeightDp * 0.6f,
                                )
                                .align(Alignment.CenterHorizontally),
                        ) {
                            TimeTable(
                                lectures = uiState.lectures,
                                selectedLecture = null,
                                fittedTrimParam = uiState.fittedTrimParam,
                                theme = uiState.theme,
                                previewTheme = uiState.previewTheme,
                                isDarkMode = isDarkMode(),
                                compactMode = uiState.compactMode,
                                tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                                touchEnabled = false,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            ThemeDetailDialogContent(
                dialogState = uiState.dialogState,
                onConfirmCancelEdit = onConfirmCancelEdit,
                onDismissCancelEdit = onDismissCancelEdit,
                onConfirmApplyToTable = onConfirmApplyToTable,
                onDismissApplyToTable = onDismissApplyToTable,
            )
        }

        is ThemeDetailUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.error_unknown),
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }

        is ThemeDetailUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// region Preview

@SnuttPreview
@Composable
private fun ThemeDetailScreen_Success() {
    SnuttPreviewSurface {
        ThemeDetailScreen(
            uiState = ThemeDetailUiState.Success(
                editingTheme = PreviewData.previewEditingThemeCustom,
                lectures = emptyList(),
                theme = BuiltInTheme.SNUTT,
                previewTheme = PreviewData.previewCustomTheme1,
                fittedTrimParam = TableTrimParam.Default,
                tableLectureCustomOptions = TableLectureCustom.Default,
                compactMode = false,
            ),
            onClickBack = {},
            onSaveTheme = {},
            onUpdateName = {},
            onToggleColorExpanded = {},
            onDuplicateColor = {},
            onRemoveColor = {},
            onUpdateColor = { _, _, _ -> },
            onAddColor = {},
            onConfirmCancelEdit = {},
            onDismissCancelEdit = {},
            onConfirmApplyToTable = {},
            onDismissApplyToTable = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ThemeDetailScreen_Loading() {
    SnuttPreviewSurface {
        ThemeDetailScreen(
            uiState = ThemeDetailUiState.Loading,
            onClickBack = {},
            onSaveTheme = {},
            onUpdateName = {},
            onToggleColorExpanded = {},
            onDuplicateColor = {},
            onRemoveColor = {},
            onUpdateColor = { _, _, _ -> },
            onAddColor = {},
            onConfirmCancelEdit = {},
            onDismissCancelEdit = {},
            onConfirmApplyToTable = {},
            onDismissApplyToTable = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ThemeDetailScreen_Error() {
    SnuttPreviewSurface {
        ThemeDetailScreen(
            uiState = ThemeDetailUiState.Error,
            onClickBack = {},
            onSaveTheme = {},
            onUpdateName = {},
            onToggleColorExpanded = {},
            onDuplicateColor = {},
            onRemoveColor = {},
            onUpdateColor = { _, _, _ -> },
            onAddColor = {},
            onConfirmCancelEdit = {},
            onDismissCancelEdit = {},
            onConfirmApplyToTable = {},
            onDismissApplyToTable = {},
        )
    }
}

// endregion
