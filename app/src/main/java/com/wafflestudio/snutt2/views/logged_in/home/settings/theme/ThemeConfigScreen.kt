package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.ArrowRight
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.CustomThemeMoreIcon
import com.wafflestudio.snutt2.components.compose.QuestionCircleIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.ThemeIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalModalState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import kotlinx.coroutines.launch

@Composable
fun ThemeConfigRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (TableTheme) -> Unit,
    onClickAddTheme: () -> Unit,
    themeConfigViewModel: ThemeConfigViewModel = hiltViewModel(),
) {
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val myCustomThemes by themeConfigViewModel.myCustomThemes.collectAsState()
    val marketCustomThemes by themeConfigViewModel.marketCustomThemes.collectAsState()
    val builtInThemes by themeConfigViewModel.builtInThemes.collectAsState()

    ThemeConfigScreen(
        myCustomThemes = myCustomThemes,
        marketCustomThemes = marketCustomThemes,
        builtInThemes = builtInThemes,
        onNavigateBack = onNavigateBack,
        onFetchThemes = {
            launchSuspendApi(apiOnProgress, apiOnError) {
                themeConfigViewModel.fetchThemes()
            }
        },
        onNavigateToDetail = onNavigateToDetail,
        onClickAddTheme = onClickAddTheme,
        onDuplicateTheme = {
            launchSuspendApi(apiOnProgress, apiOnError) {
                themeConfigViewModel.copyTheme(it)
            }
        },
        onDeleteTheme = {
            launchSuspendApi(apiOnProgress, apiOnError) {
                themeConfigViewModel.deleteThemeAndRefreshTableIfNeeded(it)
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeConfigScreen(
    myCustomThemes: List<CustomTheme>,
    marketCustomThemes: List<CustomTheme>,
    builtInThemes: List<BuiltInTheme>,
    onNavigateBack: () -> Unit,
    onFetchThemes: suspend () -> Unit,
    onNavigateToDetail: (TableTheme) -> Unit,
    onClickAddTheme: () -> Unit,
    onDuplicateTheme: suspend (TableTheme) -> Unit,
    onDeleteTheme: suspend (TableTheme) -> Unit,
) {
    val modalState = LocalModalState.current
    val bottomSheet = BottomSheet()
    val scope = rememberCoroutineScope()
    val composableStates = ComposableStatesWithScope(scope)

    val onBackPressed: () -> Unit = {
        if (bottomSheet.isVisible) {
            scope.launch { bottomSheet.hide() }
        } else {
            onNavigateBack()
        }
    }

    BackHandler {
        onBackPressed()
    }

    LaunchedEffect(Unit) {
        onFetchThemes()
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheet.state,
        sheetContent = bottomSheet.content,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SimpleTopBar(
                title = stringResource(R.string.theme_config_app_bar_title),
                onClickNavigateBack = {
                    onBackPressed()
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .verticalScroll(rememberScrollState()),
            ) {
                ThemesRow(
                    title = stringResource(R.string.theme_config_custom_theme),
                    themes = myCustomThemes,
                    onClickItem = onNavigateToDetail,
                    onClickMore = { theme ->
                        scope.launch {
                            bottomSheet.setSheetContent {
                                MyCustomThemeMoreActionBottomSheet(
                                    onClickDetail = {
                                        scope.launch {
                                            onNavigateToDetail(theme)
                                            bottomSheet.hide()
                                        }
                                    },
                                    onClickDuplicate = {
                                        scope.launch {
                                            onDuplicateTheme(theme)
                                            bottomSheet.hide()
                                        }
                                    },
                                    onClickDelete = {
                                        showDeleteThemeDialog(
                                            composableStates = composableStates,
                                            onConfirm = {
                                                onDeleteTheme(theme)
                                                modalState.hide()
                                                bottomSheet.hide()
                                            },
                                        )
                                    },
                                )
                            }
                            bottomSheet.show()
                        }
                    },
                    leadingItem = {
                        AddThemeItem(
                            onClick = onClickAddTheme,
                        )
                    },
                )
                if (marketCustomThemes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ThemesRow(
                        title = stringResource(R.string.theme_config_market_custom_theme),
                        themes = marketCustomThemes,
                        onClickItem = onNavigateToDetail,
                        onClickMore = { theme ->
                            scope.launch {
                                bottomSheet.setSheetContent {
                                    MarketCustomThemeMoreActionBottomSheet(
                                        onClickDetail = {
                                            scope.launch {
                                                onNavigateToDetail(theme)
                                                bottomSheet.hide()
                                            }
                                        },
                                        onClickDelete = {
                                            showDeleteThemeDialog(
                                                composableStates = composableStates,
                                                onConfirm = {
                                                    onDeleteTheme(theme)
                                                    modalState.hide()
                                                    bottomSheet.hide()
                                                },
                                            )
                                        },
                                    )
                                }
                                bottomSheet.show()
                            }
                        },
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                ThemesRow(
                    title = stringResource(R.string.theme_config_builtin_theme),
                    themes = builtInThemes,
                    onClickItem = onNavigateToDetail,
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
    }
}

@Composable
private fun ThemesRow(
    title: String,
    themes: List<TableTheme>,
    onClickItem: (TableTheme) -> Unit,
    modifier: Modifier = Modifier,
    onClickMore: ((TableTheme) -> Unit)? = null,
    leadingItem: (@Composable () -> Unit)? = null,
) {
    SettingColumn(
        title = title,
        titleStyle = SNUTTTypography.body2.copy(
            color = MaterialTheme.colors.onSurfaceVariant,
            fontSize = 13.sp,
        ),
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 20.dp, bottom = 12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
                leadingItem?.let {
                    it()
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            items(
                items = themes,
            ) { theme ->
                ThemeItem(
                    theme = theme,
                    onClick = {
                        onClickItem(theme)
                    },
                    onClickMore = onClickMore?.let {
                        { it(theme) }
                    },
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
fun AddThemeItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clicks { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = SNUTTColors.VacancyGray, shape = RoundedCornerShape(6.dp)),
        ) {
            AddIcon(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            )
        }
        Spacer(modifier.height(8.dp))
        Text(
            text = stringResource(R.string.theme_create),
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun ThemeItem(
    theme: TableTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickMore: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.clicks { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            ThemeIcon(
                theme = theme,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            onClickMore?.let {
                CustomThemeMoreIcon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .clicks {
                            it()
                        },
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .widthIn(max = 80.dp)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = theme.name,
                modifier = Modifier.weight(1f, false),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = SNUTTTypography.body2,
            )
            ArrowRight(
                modifier = Modifier
                    .size(10.dp)
                    .offset(y = 1.dp),
                colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
            )
        }
    }
}

@Composable
private fun ThemeGuideTexts(
    modifier: Modifier = Modifier,
) {
    val texts = listOf(
        stringResource(R.string.theme_config_guide_0),
        stringResource(R.string.theme_config_guide_1),
        stringResource(R.string.theme_config_guide_2),
    )
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QuestionCircleIcon(
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.theme_config_guide_title),
                style = SNUTTTypography.h5.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    ParagraphStyle(lineHeight = 12.sp * 1.3f),
                ) {
                    withStyle(
                        SpanStyle(fontWeight = FontWeight.SemiBold),
                    ) {
                        append(texts[0])
                    }
                    withStyle(
                        SpanStyle(fontWeight = FontWeight.Normal),
                    ) {
                        append(texts[1])
                    }
                }
            },
            style = SNUTTTypography.body2.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.theme_config_guide_2),
            style = SNUTTTypography.body2.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
        )
    }
}

@Preview
@Composable
private fun ThemesRowPreview() {
    SNUTTTheme {
        ThemesRow(
            title = "title",
            themes = List(5) { BuiltInTheme.fromCode(it) },
            onClickItem = {},
            onClickMore = {},
            leadingItem = {
                AddThemeItem(
                    onClick = {},
                )
            },
        )
    }
}

@Preview
@Composable
private fun ThemeGuideTextsPreview() {
    SNUTTTheme {
        ThemeGuideTexts()
    }
}
