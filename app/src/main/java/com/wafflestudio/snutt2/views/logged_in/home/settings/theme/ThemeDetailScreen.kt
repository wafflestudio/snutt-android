package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.components.compose.CloseIcon
import com.wafflestudio.snutt2.components.compose.ColorBox
import com.wafflestudio.snutt2.components.compose.ColorCircle
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.components.compose.rememberModalState
import com.wafflestudio.snutt2.components.compose.showColorPickerDialog
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalModalState
import com.wafflestudio.snutt2.views.LocalNavBottomSheetState
import com.wafflestudio.snutt2.views.LocalCompactState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import kotlinx.coroutines.launch

@Composable
fun ThemeDetailRoute(
    onNavigateBack: () -> Unit,
    themeDetailViewModel: ThemeDetailViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val modalState = LocalModalState.current
    val composableStates = ComposableStatesWithScope(scope)
    val isDarkMode = isDarkMode()

    val themeDetailUiState by themeDetailViewModel.themeDetailUiState.collectAsState()

    LaunchedEffect(themeDetailViewModel) {
        themeDetailViewModel.initEditingTheme(isDarkMode)
    }

    ThemeDetailScreen(
        themeDetailUiState = themeDetailUiState,
        onNavigateBack = onNavigateBack,
        onUpdateName = {
            themeDetailViewModel.updateName(it)
        },
        onToggleColorExpanded = {
            themeDetailViewModel.toggleColorExpanded(it)
        },
        onDuplicateColor = {
            themeDetailViewModel.duplicateColor(it)
        },
        onRemoveColor = {
            themeDetailViewModel.removeColor(it)
        },
        onUpdateColor = { index, fgColor, bgColor ->
            themeDetailViewModel.updateColor(index, fgColor.toArgb(), bgColor.toArgb())
        },
        onAddColor = {
            themeDetailViewModel.addColor()
        },
        onSaveTheme = { isNew ->
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    themeDetailViewModel.saveTheme()
                    when (isNew) {
                        true -> {
                            showApplyToCurrentTableDialog(
                                composableStates = composableStates,
                                apply = {
                                    themeDetailViewModel.applyThemeToCurrentTable()
                                    modalState.hide()
                                    onNavigateBack()
                                },
                                avoid = {
                                    modalState.hide()
                                    onNavigateBack()
                                },
                            )
                        }

                        false -> {
                            themeDetailViewModel.refreshCurrentTableIfNeeded()
                            onNavigateBack()
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun ThemeDetailScreen(
    themeDetailUiState: ThemeDetailUiState,
    onNavigateBack: () -> Unit,
    onSaveTheme: (Boolean) -> Unit,
    onUpdateName: (String) -> Unit,
    onToggleColorExpanded: (Int) -> Unit,
    onDuplicateColor: (Int) -> Unit,
    onRemoveColor: (Int) -> Unit,
    onUpdateColor: (Int, foreground: Color, background: Color) -> Unit,
    onAddColor: () -> Unit,
) {
    when (themeDetailUiState) {
        is ThemeDetailUiState.Success -> {
            val editingTheme = themeDetailUiState.editingTheme
            val composableStates = ComposableStatesWithScope(rememberCoroutineScope())
            val modalState = LocalModalState.current

            val onBackPressed: () -> Unit = {
                if (editingTheme.hasChange()) {
                    showCancelEditDialog(
                        composableStates = composableStates,
                        cancelEdit = {
                            modalState.hide()
                            onNavigateBack()
                        },
                    )
                } else {
                    onNavigateBack()
                }
            }

            BackHandler {
                onBackPressed()
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth()
                    .logImpression(
                        when {
                            themeDetailUiState.editingTheme.isNew -> AnalyticsScreen.ThemeCustomNew
                            themeDetailUiState.editingTheme.isFromMarket -> AnalyticsScreen.ThemeDownloaded
                            themeDetailUiState.editingTheme.isCustomTheme -> AnalyticsScreen.ThemeCustomEdit
                            else -> AnalyticsScreen.ThemeBasicDetail
                        },
                    ),
            ) {
                ThemeDetailTopBar(
                    isCustomTheme = themeDetailUiState.editingTheme.isCustomTheme,
                    onClickBack = onBackPressed,
                    onClickSave = {
                        onSaveTheme(editingTheme.isNew)
                    },
                )
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    ThemeDetailItem(
                        title = stringResource(R.string.theme_detail_theme_name),
                        titleColor = MaterialTheme.colors.onSurfaceVariant.copy(alpha = if (editingTheme.isEditable) 1f else 0.5f),
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
                                    MaterialTheme.colors.onSurfaceVariant.copy(
                                        alpha = 0.5f,
                                    )
                                },
                            ),
                        )
                    }
                    SettingColumn(
                        title = stringResource(R.string.theme_detail_theme_colors),
                    ) {
                        editingTheme.colors.forEachIndexed { idx, colorWithExpanded ->
                            ThemeColorRow(
                                index = idx,
                                isEditable = editingTheme.isEditable,
                                color = colorWithExpanded.item,
                                isExpanded = colorWithExpanded.state,
                                isDuplicateEnabled = editingTheme.colors.size < 9,
                                isRemoveEnabled = editingTheme.colors.size > 1,
                                onToggleColorExpanded = onToggleColorExpanded,
                                onDuplicateColor = onDuplicateColor,
                                onRemoveColor = onRemoveColor,
                                onUpdateColor = onUpdateColor,
                            )
                        }
                        AnimatedVisibility(editingTheme.isEditable && editingTheme.colors.size < 9) {
                            ThemeColorAddRow(
                                onClick = onAddColor,
                            )
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
                                    (LocalConfiguration.current.screenWidthDp * 0.8).dp,
                                    (LocalConfiguration.current.screenHeightDp * 0.6).dp,
                                )
                                .align(Alignment.CenterHorizontally),
                        ) {
                            TimeTable(
                                table = themeDetailUiState.tableState.table,
                                trimParam = themeDetailUiState.tableState.trimParam,
                                tableLectureCustomOptions = themeDetailUiState.tableState.tableLectureCustomOptions,
                                previewTheme = themeDetailUiState.tableState.previewTheme,
                                compactMode = LocalCompactState.current,
                                navigator = LocalNavController.current,
                                selectedLecture = null,
                                touchEnabled = false,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
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

@Composable
private fun ThemeDetailItem(
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
            style = SNUTTTypography.body2.copy(
                color = titleColor,
            ),
        )
        content()
        Spacer(modifier = Modifier.weight(1f))
        actions()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ThemeColorRow(
    index: Int,
    isEditable: Boolean,
    color: ThemeColor,
    isExpanded: Boolean,
    isDuplicateEnabled: Boolean,
    isRemoveEnabled: Boolean,
    onToggleColorExpanded: (Int) -> Unit,
    onDuplicateColor: (Int) -> Unit,
    onRemoveColor: (Int) -> Unit,
    onUpdateColor: (Int, foreground: Color, background: Color) -> Unit,
) {
    val navBottomSheetState = LocalNavBottomSheetState.current
    val state = remember {
        MutableTransitionState(
            navBottomSheetState.currentValue == ModalBottomSheetValue.Hidden, // 바텀시트 올라올 때에는 애니메이션 적용 안하기 위함
        ).apply { targetState = true }
    }
    AnimatedVisibility(
        visibleState = state,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column {
            ThemeDetailItem(
                title = stringResource(R.string.theme_detail_color_item, index + 1),
                titleColor = if (isEditable) {
                    MaterialTheme.colors.onSurfaceVariant
                } else {
                    MaterialTheme.colors.onSurfaceVariant.copy(
                        alpha = 0.5f,
                    )
                },
                modifier = Modifier.clicks {
                    if (isEditable) {
                        onToggleColorExpanded(index)
                    }
                },
                actions = {
                    if (isEditable) {
                        DuplicateIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (isDuplicateEnabled) {
                                        onDuplicateColor(index)
                                    }
                                },
                            colorFilter = ColorFilter.tint(
                                (if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray40).copy(
                                    alpha = if (isDuplicateEnabled) 1f else 0.3f,
                                ),
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CloseIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks {
                                    if (isRemoveEnabled) {
                                        onRemoveColor(index)
                                    }
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
                    onFgColorPicked = { pickedColor ->
                        onUpdateColor(
                            index,
                            pickedColor,
                            Color(color.background),
                        )
                    },
                    onBgColorPicked = { pickedColor ->
                        onUpdateColor(
                            index,
                            Color(color.foreground),
                            pickedColor,
                        )
                    },
                )
            }
            Divider(thickness = 0.5.dp, color = MaterialTheme.colors.background)
        }
    }
}

@Composable
private fun ColorEditItem(
    fgColor: Color,
    bgColor: Color,
    onFgColorPicked: (Color) -> Unit,
    onBgColorPicked: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val modalState = LocalModalState.current
    Row(
        modifier = modifier
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
                        .clicks {
                            showColorPickerDialog(
                                context = context,
                                modalState = modalState,
                                initialColor = fgColor,
                                onColorPicked = { color ->
                                    onFgColorPicked(color)
                                },
                            )
                        },
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
                        .clicks {
                            showColorPickerDialog(
                                context = context,
                                modalState = modalState,
                                initialColor = bgColor,
                                onColorPicked = { color ->
                                    onBgColorPicked(color)
                                },
                            )
                        },
                )
            }
        }
    }
}

@Composable
private fun ThemeColorAddRow(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clicks {
                onClick()
            },
    ) {
        Text(
            text = stringResource(R.string.theme_detail_add_color),
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
private fun ThemeDetailTopBar(
    isCustomTheme: Boolean,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
) {
    CenteredTopBar(
        title = {
            Text(
                text = if (isCustomTheme) {
                    stringResource(R.string.theme_detail_app_bar_title_custom)
                } else {
                    stringResource(R.string.theme_detail_app_bar_title_builtin)
                },
                style = SNUTTTypography.h3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier
                    .clicks {
                        onClickBack()
                    },
            )
        },
        actions = {
            Text(
                text = stringResource(R.string.common_save),
                style = SNUTTTypography.body1,
                modifier = Modifier
                    .clicks {
                        onClickSave()
                    },
            )
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun ThemeColorRowPreview() {
    SNUTTTheme {
        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
        val modalState = rememberModalState()
        CompositionLocalProvider(
            LocalNavBottomSheetState provides modalBottomSheetState,
            LocalModalState provides modalState,
        ) {
            ThemeColorRow(
                index = 0,
                isEditable = true,
                color = ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF1BD0C8.toInt()),
                isExpanded = true,
                isDuplicateEnabled = true,
                isRemoveEnabled = true,
                onToggleColorExpanded = {},
                onDuplicateColor = {},
                onRemoveColor = {},
                onUpdateColor = { _, _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun ThemeColorAddRowPreview() {
    SNUTTTheme {
        ThemeColorAddRow(
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun ThemeDetailTopBarPreview() {
    SNUTTTheme {
        ThemeDetailTopBar(
            isCustomTheme = true,
            onClickBack = {},
            onClickSave = {},
        )
    }
}
