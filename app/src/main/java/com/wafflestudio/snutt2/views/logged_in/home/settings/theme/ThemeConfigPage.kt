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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.ThemeIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeConfigPage(
    themeConfigViewModel: ThemeConfigViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val bottomSheet = BottomSheet()
    val scope = rememberCoroutineScope()

    val customThemes by themeConfigViewModel.themes.collectAsState()

    val onBackPressed: () -> Unit = {
        navController.popBackStack()
    }

    BackHandler {
        onBackPressed()
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheet.state,
        sheetContent = bottomSheet.content,
        sheetShape = RoundedCornerShape(5.dp),
        sheetGesturesEnabled = false,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SimpleTopBar(
                title = "시간표 테마",
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
                    title = "커스텀 테마",
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
                                modifier = Modifier.clicks {
                                    bottomSheet.setSheetContent {
                                        CompositionLocalProvider(LocalBottomSheetState provides bottomSheet) {
                                            ThemeDetailPage(
                                                theme = ThemeDto.Default,
                                                onClickSave = { themeConfigViewModel.fetchCustomThemes() },
                                            )
                                        }
                                    }
                                    scope.launch { bottomSheet.show() }
                                },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        items(
                            items = customThemes,
                        ) { theme ->
                            ThemeItem(
                                theme = theme,
                                modifier = Modifier.clicks {
                                    bottomSheet.setSheetContent {
                                        CompositionLocalProvider(LocalBottomSheetState provides bottomSheet) {
                                            ThemeDetailPage(
                                                theme = theme,
                                                onClickSave = { themeConfigViewModel.fetchCustomThemes() },
                                            )
                                        }
                                    }
                                    scope.launch { bottomSheet.show() }
                                },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                }
                SettingColumn(
                    title = "제공 테마",
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(top = 20.dp, bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))
                        ThemeDto.builtInThemes.forEachIndexed { idx, theme ->
                            ThemeItem(
                                theme = theme,
                                modifier = Modifier.clicks {
                                    bottomSheet.setSheetContent {
                                        CompositionLocalProvider(LocalBottomSheetState provides bottomSheet) {
                                            ThemeDetailPage(
                                                theme = theme,
                                                canEdit = false,
                                                onClickSave = { themeConfigViewModel.fetchCustomThemes() },
                                            )
                                        }
                                    }
                                    scope.launch { bottomSheet.show() }
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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
            )
        }
        Spacer(modifier.height(8.dp))
        Text(
            text = stringResource(R.string.theme_new),
            style = SNUTTTypography.body1,
        )
    }
}

@Composable
fun ThemeItem(
    theme: ThemeDto,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ThemeIcon(
            theme = theme,
            modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = theme.name,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = SNUTTTypography.body1,
        )
    }
}
