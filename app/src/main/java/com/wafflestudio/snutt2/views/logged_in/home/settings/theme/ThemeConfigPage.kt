package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.CustomThemeMoreIcon
import com.wafflestudio.snutt2.components.compose.CustomThemePinIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.ThemeIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.model.TableTheme
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeConfigPage(
    themeListViewModel: ThemeListViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val bottomSheet = BottomSheet()
    val scope = rememberCoroutineScope()

    val customThemes by themeListViewModel.customThemes.collectAsState()
    val builtInThemes by themeListViewModel.builtInThemes.collectAsState()

    val onBackPressed: () -> Unit = {
        if (bottomSheet.isVisible) {
            scope.launch { bottomSheet.hide() }
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        onBackPressed()
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheet.state,
        sheetContent = bottomSheet.content,
        sheetShape = RoundedCornerShape(5.dp),
        sheetGesturesEnabled = false,
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
                    .background(MaterialTheme.colors.background),
            ) {
                SettingColumn(
                    title = stringResource(R.string.theme_config_custom_theme),
                    titleStyle = SNUTTTypography.body2.copy(
                        color = MaterialTheme.colors.onSurfaceVariant,
                        fontSize = 13.sp,
                    ),
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(top = 20.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        item {
                            Spacer(modifier = Modifier.width(20.dp))
                            AddThemeItem(
                                onClick = {
                                    navController.navigate(NavigationDestination.ThemeDetail)
                                },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        items(
                            items = customThemes,
                        ) { theme ->
                            ThemeItem(
                                theme = theme,
                                onClick = {
                                    navController.navigate("${NavigationDestination.ThemeDetail}?themeId=${theme.id}")
                                },
                                onClickMore = {
                                    scope.launch {
                                        bottomSheet.setSheetContent {
                                            CustomThemeMoreActionBottomSheet(
                                                onClickDetail = {
                                                    navController.navigate("${NavigationDestination.ThemeDetail}?themeId=${theme.id}")
                                                },
                                                onClickRename = {
                                                },
                                                onClickDuplicate = {
                                                    scope.launch {
                                                        themeListViewModel.copyTheme(theme.id)
                                                        bottomSheet.hide()
                                                    }
                                                },
                                                onClickDelete = {
                                                    scope.launch {
                                                        themeListViewModel.deleteTheme(theme.id)
                                                        bottomSheet.hide()
                                                    }
                                                },
                                            )
                                        }
                                        bottomSheet.show()
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                SettingColumn(
                    title = stringResource(R.string.theme_config_builtin_theme),
                    titleStyle = SNUTTTypography.body2.copy(
                        color = MaterialTheme.colors.onSurfaceVariant,
                        fontSize = 13.sp,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(top = 20.dp, bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))
                        builtInThemes.forEach { theme ->
                            ThemeItem(
                                theme = theme,
                                onClick = {
                                    navController.navigate("${NavigationDestination.ThemeDetail}?theme=${theme.code}")
                                },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                }
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
            if (onClickMore != null) {
                CustomThemeMoreIcon(
                    modifier = Modifier
                        .size(32.dp)
                        .zIndex(1f)
                        .align(Alignment.TopEnd)
                        .offset(12.dp, (-8).dp)
                        .clicks { onClickMore() },
                )
            }
            if (theme.isDefault) {
                CustomThemePinIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .zIndex(1f)
                        .align(Alignment.TopStart)
                        .offset((-8).dp, (-8).dp),
                )
            }
            ThemeIcon(
                theme = theme,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = theme.name,
            modifier = Modifier
                .widthIn(max = 80.dp)
                .then(
                    if (theme.isDefault) {
                        Modifier.background(
                            color = if (isDarkMode()) SNUTTColors.DarkerGray else SNUTTColors.Gray,
                            shape = CircleShape,
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = SNUTTTypography.body2,
        )
    }
}
